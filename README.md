# lauth

여러 백엔드 서비스 (주로 개인 프로젝트)에서 공통으로 사용하기 위해 만든 인증,인가 서버입니다.

사용자 자격 증명, 세션, Refresh Token 기반 세션 연장과 권한 정보를 중앙에서 관리하고,
해당 인증 서버와 연동된 Resource Server가 검증할 수 있는 Access Token을 발급하는 것을 목표로 합니다.

> 현재 개발 중인 프로젝트로 큰 스펙 변경이 있습니다.

## 개요

일반적인 서비스에서 인증 기능은 사용자 인증뿐만 아니라 다음과 같은 문제를 함께 다뤄야합니다.

- 사용자의 자격 증명을 어떻게 안전하게 관리할 것인지
- 여러 기기에서 생성된 로그인 세션을 어떻게 독립적으로 관리할 것인지
- 세션 연장에 사용할 Refresh Token의 탈취 및 (일회성 토큰일 경우) 재사용을 어떻게 방어할 것인지
- 여러 Resource Server가 Access Token을 어떻게 검증할 것인지
- 비밀번호 재설정과 같은 일회성 인증 흐름을 어떻게 처리할 것인지

'lauth'는 이러한 인증 책임을 개별 서비스에서 분리하고,
인증 및 인가와 관련된 상태 및 정책을 하나의 Identity Server에서 관리하는 것을 목표로 합니다.

## Architecture

Client -> (Authentication) -> lauth -> (Access Token) -> Service A, Service B, Service C

각 Resource Server는 사용자 인증 상태를 직접 관리하지 않고, 'lauth'가 발급한 Access Token의 `signature`, `issuer`, `audience` 및 `scope`를 검증합니다.

## Core Concepts

### User & Credential

사용자의 서비스 내 신원과 로그인 자격 증명을 분리해서 관리합니다.

`User`는 서비스에서 사용하는 사용자 신원을 나타내며,
`CredentialAccount`는 이메일과 비밀번호 등 인증에 필요한 정보를 담당합니다.

이를 통해 향후 credential 기반 로그인 외에도 OAuth/OIDC와 같은 federated account를 동일 사용자에게 연결할 수 있도록 구성하고 있습니다.

### Scope & Grant

권한은 Role 대신 세분화된 Scope를 중심으로 표현합니다.

Scope는 `{resource}.{action}`과 같은 형식을 사용하며 예시는 아래와 같습니다.

- `reservation.read`
- `reservation.write`
- `user.write`

사용자와 Scope 사이 관계는 `UserGrant`가 관리하며,
발급된 Access Token에는 사용자에게 허용된 Scope가 포함됩니다.

OAuth2 Authorization Server가 발급하는 JWT의 리소스 Scope는 클라이언트 허용 범위,
사용자가 승인한 범위, 발급 시점의 `UserGrant`의 교집합입니다.
`openid`, `profile`은 승인된 경우 유지하며, ID Token에는 리소스 권한을 추가하지 않습니다.
현재 `oidc-client`는 `user.read`, `user.write`를 요청할 수 있습니다.
인가 요청에 `scope=openid profile user.read user.write`처럼 필요한 Scope를 지정해야 합니다.

Refresh Token으로 재발급할 때도 DB 권한을 다시 조회하므로 회수된 권한은 새 JWT에서 제외됩니다.
이미 발급된 JWT에는 권한 변경이 소급 반영되지 않습니다.
이 필터링은 JWT의 `scope` claim에 적용되며, OAuth2 동의 기록과 토큰 응답의 `scope`는
기존 승인 범위를 나타낼 수 있습니다. API 권한 검사는 검증된 JWT를 기준으로 수행합니다
(예: `hasAuthority('SCOPE_user.write')`).

### Session & Refresh Token

로그인 한 번을 하나의 `Session`으로 취급합니다.

한 사용자가 여러 기기에서 로그인한 경우 각각 독립적인 Session을 가지므로, 현재 기기의 세션 로그아웃, 특정 기기의 세션 로그아웃 및 전체 세션 로그아웃과 같은 동작을 구분할 수 있습니다.

Refresh Token은 Session에 종속되며, Access Token 재발급 과정에서 Refresh Token의 상태와 Session의 상태를 함께 검증합니다.

## Key Architecture Decision

### Opaque Token

Refresh Token, 비밀번호 재설정 토큰 등 일종의 일회성 Credential로 사용되는 토큰은 공통된 Opaque Token 구조로 관리합니다.

토큰은 다음과 같이 lookup을 위한 `selector`와 Credential 검증을 위한 비밀 값으로 구성합니다.

`{selector}.{token}`

`selector`는 서버에서 토큰 레코드 조회를 위해 사용하며, 실제 Credential 역할을 하는 `token` 원문은 서버에 저장하지 않습니다.
서버에는 HMAC 기반 hash만 보관하고, 요청 시 전달받은 token을 다시 hashing하여 저장된 값과 비교합니다.

각 토큰은 다음 시각 정보를 통해 수명 주기를 관리합니다.

- `issuedAt`: 토큰 발행 시각
- `expiresAt`: 토큰 만료 시각
- `consumedAt`: 정상적으로 토큰을 사용한 시각
- `revokedAt`: 사용되지 않은 상태에서 명시적으로 폐기한 시각

위 상태를 바탕으로 토큰의 유효성 판단 과정에는 만료 시각뿐만 아니라 소비 및 폐기 여부도 포함됩니다.

이 구조는 Refresh Token, Password Reset Token 등 서로 다른 종류의 Credential에 공통적으로 적용하며, 토큰 생성 및 검증 방식은 재사용하나, 각 토큰의 사용 방법은 개별
유스케이스가 따로 정의합니다.

### Refresh Token

클라이언트 세션 연장을 위한 Refresh Token은 일회성 Opaque Token을 사용합니다.

클라이언트가 Access Token 재발급 요청 시 다음 과정을 수행합니다.

1. `selector`를 이용해 Refresh Token 조회
2. 전달된 `token plain`의 해시와 저장된 Refresh Token의 `token hash`를 비교
3. 토큰 만료 여부 확인
4. session revoke 여부 확인
5. token 사용 여부 확인
6. 새로운 토큰 발급

이미 소비된 토큰이 다시 사용됐다면, 정상 클라이언트 요청인지, 탈취자의 요청인지 구분할 수 없다는 점을 근거로, 해당 Session 자체를 폐기하는 방향으로 설계하고 있습니다.

### Password Reset Token

비밀번호 재설정 또한 일회성 Opaque Token을 사용합니다.

하나의 Credential에 동시에 여러 개의 유효한 재설정 토큰이 존재하지 않도록 제한하고,
새로운 재설정 요청이 생성되면 기존에 활성화 되어있던 토큰을 폐기합니다.

## Run the Service

### k6 테스트 사용자

Identity Service에 `LAUTH_TEST_ENABLED=true`, `LAUTH_TEST_USER_COUNT=1000`,
`LAUTH_TEST_IDS_FILE=/opt/lauth/test/user-ids.txt`, `LAUTH_TEST_CLIENT_SECRET=<별도 비밀값>`을 설정하면
시작 과정에서 테스트 사용자를 시딩합니다. `user.read`와 `user.write` 스코프가 DB에 있어야 하므로
`LAUTH_SYNC_SCOPE_DEFINITION=true`도 설정하세요. 동일한 DB로 재시작해도 사용자 ID와 권한은 재사용하고,
ID 파일은 최신 사용자 목록으로 갱신합니다. 파일은 한 줄에 UUID 하나씩 기록됩니다.

Docker에서 다른 프로젝트가 ID 파일을 읽으려면 `/opt/lauth/test` 디렉터리를 호스트나 공유 볼륨에
쓰기 가능하게 마운트하고, 읽는 쪽에도 같은 볼륨을 마운트하세요. 테스트 플래그를 끄면 시딩과
테스트 클라이언트 및 grant가 등록되지 않습니다.

시딩된 ID로 토큰을 발급하려면 다음 요청을 사용합니다.

```bash
curl -u "test-client:${LAUTH_TEST_CLIENT_SECRET}" \
  -d 'grant_type=urn:lauth:grant-type:test' \
  -d "user_id=$(head -n 1 /path/to/shared/user-ids.txt)" \
  http://localhost:8090/oauth2/token
```

OAuth2 Authorization Server가 액세스 토큰과 리프레시 토큰을 발급하고 DB에 발급 기록을 저장합니다.
`grant_type=refresh_token`으로 갱신할 수 있습니다. 이 토큰 엔드포인트는 브라우저 HTTP 세션을
생성하지 않습니다.

### 데모 실행

저장소 루트에서 아래 명령을 실행하면 Identity Service, PostgreSQL, [lauth-client](https://github.com/lilamaris/lauth-client)를 시작합니다.
프론트는 `http://localhost:5174`, Identity Service는 `http://localhost:8090`에서 접근할 수 있습니다.

```bash
bash script/run-demo.sh
```

데모는 테스트 사용자 시딩을 켜고 기본값인 1,000명의 ID를 임시 디렉터리의 `generated/test-user-ids.txt`에 기록합니다.
다른 위치에 ID 파일을 공유하려면 아직 존재하지 않는 전용 디렉터리를 지정하세요. 상대 경로는 저장소 루트를 기준으로 해석합니다.

```bash
bash script/run-demo.sh --generated-output ./demo-test-users
```

이 경우 ID 파일은 `./demo-test-users/test-user-ids.txt`에 생성됩니다. Identity Service는 스크립트를 실행한 호스트 사용자의 UID:GID로 실행되므로, 같은 사용자가 호스트에서 파일을 읽을 수 있습니다. 데모 테스트 클라이언트의 ID는 `test-client`, 비밀값은 `test-secret`입니다.

스크립트는 다음 순서로 실행합니다.

1. 기존 데모 상태가 있으면 정리하고 임시 디렉터리를 만듭니다.
2. Gradle로 `identity-service/launcher`를 빌드하고, 호스트 아키텍처에 맞는 Docker 이미지를 로컬 엔진에 불러옵니다.
3. 프론트 저장소를 임시 디렉터리에 복제하고 해당 저장소의 Dockerfile로 이미지를 빌드합니다. 이미지 태그에는 프론트 커밋 해시를 사용합니다.
4. RSA 키, 닉네임 사전 파일, 생성 파일을 저장할 디렉터리를 준비합니다.
5. Docker Compose로 PostgreSQL, Identity Service, 프론트를 시작합니다. Identity Service는 PostgreSQL이 준비된 뒤 테스트 사용자를 시딩하고 ID 파일을 기록합니다.
6. 실행이 끝나면 생성된 컨테이너, 이미지, 상태 파일에 기록된 경로를 출력합니다. 서비스는 스크립트가 종료된 뒤에도 계속 실행됩니다.

데모에서는 프론트 서버와 Identity Service가 네트워크를 공유해 모두 `localhost:8090`을 인증 서버 주소로 사용합니다. API 요청은 프론트 서버에서 보내고 브라우저의 인증 요청은 페이지 이동으로 처리하므로 별도의 CORS 설정이 필요하지 않습니다. Google과 GitHub 로그인에는 실제 OAuth 공급자 인증 정보가 필요합니다.

직접 정리하려면 아래 명령을 실행합니다.

```bash
bash script/cleanup-demo.sh
```

`cleanup-demo.sh`는 데모용 Docker Compose 컨테이너와 PostgreSQL 볼륨, 데모에서 빌드한 Docker 이미지를 제거합니다. `script/.state.tmp`에 기록된 임시 디렉터리와 생성 파일 디렉터리도 삭제하므로 `--generated-output`에는 전용 디렉터리를 사용하세요. 이미지 이름은 `script/.state.img`에 기록되며, 정리 후 두 상태 파일도 삭제됩니다.

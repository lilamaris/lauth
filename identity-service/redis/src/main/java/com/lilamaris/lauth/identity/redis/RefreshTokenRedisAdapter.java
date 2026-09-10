package com.lilamaris.lauth.identity.redis;

import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("cache")
public class RefreshTokenRedisAdapter implements RefreshTokenReader {
}

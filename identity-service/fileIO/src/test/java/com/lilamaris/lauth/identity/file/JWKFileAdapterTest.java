package com.lilamaris.lauth.identity.file;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JWKFileAdapterTest {
    private final JWKFileAdapter adapter = new JWKFileAdapter();
    @TempDir
    Path keySource;

    @Test
    void loadsEd25519PrivateKeyAndDerivesMatchingPublicKey() throws Exception {
        var pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        writeKey("Ed25519", "PRIVATE KEY", pair.getPrivate().getEncoded());

        var metadata = adapter.readJWKMetadataFrom(keySource, "test-key").getFirst();
        assertThat(metadata.jwk()).isInstanceOf(OctetKeyPair.class);
        var key = (OctetKeyPair) metadata.jwk();
        assertThat(key.getCurve()).isEqualTo(Curve.Ed25519);
        assertThat(key.isPrivate()).isTrue();
        assertThat(key.getDecodedD()).hasSize(32);
        assertThat(key.getDecodedX()).isEqualTo(
                SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded()).getPublicKeyData().getBytes());
        assertThat(key.toPublicJWK().toJSONObject()).doesNotContainKey("d");
    }

    @Test
    void rejectsDifferentCurveMarkedAsEd25519() throws Exception {
        var pair = KeyPairGenerator.getInstance("X25519").generateKeyPair();
        writeKey("Ed25519", "PRIVATE KEY", pair.getPrivate().getEncoded());

        assertThatThrownBy(() -> adapter.readJWKMetadataFrom(keySource, "test-key"))
                .isInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("Private key type does not match Ed25519");
    }

    @Test
    void rejectsPublicOnlyEd25519Pem() throws Exception {
        var pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        writeKey("Ed25519", "PUBLIC KEY", pair.getPublic().getEncoded());

        assertThatThrownBy(() -> adapter.readJWKMetadataFrom(keySource, "test-key"))
                .isInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("Expected an unencrypted PKCS#8 Ed25519 private key");
    }

    @Test
    void stillLoadsRsaPrivateKey() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        writeKey("RS256", "PRIVATE KEY", pair.getPrivate().getEncoded());

        var key = adapter.readJWKMetadataFrom(keySource, "test-key").getFirst().jwk();
        assertThat(key).isInstanceOf(RSAKey.class);
        assertThat(key.isPrivate()).isTrue();
        assertThat(((RSAKey) key).toRSAPublicKey()).isEqualTo(pair.getPublic());
    }

    private void writeKey(String alg, String label, byte[] encoded) throws Exception {
        var directory = Files.createDirectory(keySource.resolve("test-key"));
        Files.writeString(directory.resolve("metadata.env"),
                "KID=test-key\nALG=" + alg + "\nCREATED_AT=2026-09-08T00:00:00Z\n");
        Files.writeString(directory.resolve("private.pem"),
                "-----BEGIN " + label + "-----\n"
                        + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded)
                        + "\n-----END " + label + "-----\n");
    }
}

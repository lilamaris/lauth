package com.lilamaris.lauth.identity.file;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;
import com.lilamaris.lauth.identity.application.model.jwks.KeyRotationStatus;
import com.lilamaris.lauth.identity.application.port.out.JWKSReader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class JWKFileAdapter implements JWKSReader {
    private final static String KEY_FILE = "private.pem";
    private final static String METADATA_FILE = "metadata.env";

    @Override
    public List<JWKMetadata> readJWKMetadataFrom(Path keySource, String activeKid) {
        if (!Files.exists(keySource))
            throw new IllegalStateException("keySource does not exists. keySource=" + keySource);
        if (!Files.isDirectory(keySource))
            throw new IllegalStateException("keySource is not directory. keySource=" + keySource);

        try (var files = Files.list(keySource)) {
            var result = new ArrayList<JWKMetadata>();

            for (var path : files.filter(Files::isDirectory).toList()) {
                var metadata = readMetadata(path);
                var jwk = readJwk(path, metadata.alg());
                var jwkMetadata = JWKMetadata.of(
                        metadata.kid().equals(activeKid)
                                ? KeyRotationStatus.ACTIVE
                                : KeyRotationStatus.RETIRED,
                        metadata.kid(),
                        metadata.alg(),
                        metadata.createdAt(),
                        jwk
                );

                result.add(jwkMetadata);
            }

            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to list keySource directories. keySource=" + keySource, e);
        }
    }

    private JWK readJwk(Path path, JWSAlgorithm alg) throws IOException {
        var keyPath = path.resolve(KEY_FILE);
        var key = Files.readString(keyPath, StandardCharsets.UTF_8);

        try {
            if (JWSAlgorithm.Ed25519.equals(alg))
                return readEd25519(key);
            return JWK.parseFromPEMEncodedObjects(key);
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed parse pem to JWK. path=" + path, e);
        }
    }

    private OctetKeyPair readEd25519(String pem) throws IOException, JOSEException {
        try (var parser = new PEMParser(new StringReader(pem))) {
            if (!(parser.readObject() instanceof PrivateKeyInfo privateKeyInfo))
                throw new JOSEException("Expected an unencrypted PKCS#8 Ed25519 private key");
            if (parser.readObject() != null)
                throw new JOSEException("Expected a single Ed25519 private key");
            if (!(PrivateKeyFactory.createKey(privateKeyInfo) instanceof Ed25519PrivateKeyParameters privateKey))
                throw new JOSEException("Private key type does not match Ed25519");

            var publicKey = privateKey.generatePublicKey();
            var x = Base64URL.encode(publicKey.getEncoded());
            var d = Base64URL.encode(privateKey.getEncoded());

            return new OctetKeyPair.Builder(Curve.Ed25519, x).d(d).build();
        }
    }

    private KeyMetadata readMetadata(Path path) throws IOException {
        var metadataPath = path.resolve(METADATA_FILE);
        var metadata = new Properties();
        try (var reader = Files.newBufferedReader(metadataPath, StandardCharsets.UTF_8)) {
            metadata.load(reader);
            return KeyMetadata.of(
                    requiredProperty(metadata, "KID"),
                    JWSAlgorithm.parse(requiredProperty(metadata, "ALG")),
                    Instant.parse(requiredProperty(metadata, "CREATED_AT"))
            );
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid key metadata. path=" + metadataPath, e);
        }
    }

    private String requiredProperty(Properties metadata, String name) {
        var value = metadata.getProperty(name);
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Missing key metadata property. name=" + name);
        return value.strip();
    }
}

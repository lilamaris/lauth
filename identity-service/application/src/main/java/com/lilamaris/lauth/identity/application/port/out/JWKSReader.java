package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;

import java.nio.file.Path;
import java.util.List;

public interface JWKSReader {
    List<JWKMetadata> readJWKMetadataFrom(Path keySource, String activeKid);
}

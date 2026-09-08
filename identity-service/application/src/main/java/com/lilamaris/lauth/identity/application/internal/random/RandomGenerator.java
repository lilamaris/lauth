package com.lilamaris.lauth.identity.application.internal.random;

public interface RandomGenerator<T> {
    T generate();

    T generate(int byteLength);
}

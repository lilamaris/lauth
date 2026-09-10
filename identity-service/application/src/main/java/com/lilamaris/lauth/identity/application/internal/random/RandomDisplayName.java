package com.lilamaris.lauth.identity.application.internal.random;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;

@RequiredArgsConstructor
public class RandomDisplayName implements RandomGenerator<String> {
    private final SecureRandom secureRandom;
    private final List<String> adjectives;
    private final List<String> nouns;

    public RandomDisplayName(SecureRandom secureRandom, Path adjectiveSource, Path nounsSource) throws IOException {
        this.secureRandom = secureRandom;
        this.adjectives = loadWords(adjectiveSource);
        this.nouns = loadWords(nounsSource);
    }

    private static List<String> loadWords(Path source) throws IOException {
        var words = Files.readAllLines(source).stream()
                .map(String::strip)
                .filter(word -> !word.isBlank())
                .toList();

        if (words.isEmpty())
            throw new IllegalStateException("Display name word source must contain at least one word. source=" + source);

        return words;
    }

    @Override
    public String generate() {
        var adjective = adjectives.get(secureRandom.nextInt(adjectives.size()));
        var noun = nouns.get(secureRandom.nextInt(nouns.size()));
        var n = secureRandom.nextInt(10_000);

        return "%s%s%04d".formatted(adjective, noun, n);
    }
}

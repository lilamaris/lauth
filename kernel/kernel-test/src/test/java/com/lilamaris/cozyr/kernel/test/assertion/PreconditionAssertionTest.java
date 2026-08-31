package com.lilamaris.cozyr.kernel.test.assertion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.lilamaris.cozyr.kernel.test.assertion.PreconditionAssertion.assertThatDomainThrownBy;

@DisplayName("PreconditionAssertion 테스트")
class PreconditionAssertionTest {

    @Test
    @DisplayName("null 불가 메시지를 검증할 수 있다")
    void assert_non_null_message() {
        assertThatDomainThrownBy(() -> {
            throw new NullPointerException("value must not be null.");
        }).hasNonNullMessage();
    }

    @Test
    @DisplayName("필드 이름을 포함한 null 불가 메시지를 검증할 수 있다")
    void assert_non_null_message_for_field() {
        assertThatDomainThrownBy(() -> {
            throw new NullPointerException("value must not be null.");
        }).hasNonNullMessageFor("value");
    }

    @Test
    @DisplayName("공백 불가 메시지를 검증할 수 있다")
    void assert_non_blank_message() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must not be blank.");
        }).hasNonBlankMessage();
    }

    @Test
    @DisplayName("필드 이름을 포함한 공백 불가 메시지를 검증할 수 있다")
    void assert_non_blank_message_for_field() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must not be blank.");
        }).hasNonBlankMessageFor("value");
    }

    @Test
    @DisplayName("음수 메시지를 검증할 수 있다")
    void assert_negative_message() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must be negative.");
        }).hasNegativeMessageFor("value");
    }

    @Test
    @DisplayName("0 이상 메시지를 검증할 수 있다")
    void assert_non_negative_message() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must be non-negative.");
        }).hasNonNegativeMessageFor("value");
    }

    @Test
    @DisplayName("양수 메시지를 검증할 수 있다")
    void assert_positive_message() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must be positive.");
        }).hasPositiveMessageFor("value");
    }

    @Test
    @DisplayName("0 이하 메시지를 검증할 수 있다")
    void assert_non_positive_message() {
        assertThatDomainThrownBy(() -> {
            throw new IllegalArgumentException("value must be non-positive.");
        }).hasNonPositiveMessageFor("value");
    }
}

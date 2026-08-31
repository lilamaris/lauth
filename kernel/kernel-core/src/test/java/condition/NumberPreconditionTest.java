package condition;

import com.lilamaris.cozyr.kernel.core.condition.NumberPrecondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Number Precondition 테스트")
public class NumberPreconditionTest {
    @Test
    @DisplayName("음수 Integer를 요구할 수 있다")
    void require_negative_integer() {
        assertThat(NumberPrecondition.requireNegative(-1, "value")).isEqualTo(-1);

        assertThatThrownBy(() -> NumberPrecondition.requireNegative(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
        assertThatThrownBy(() -> NumberPrecondition.requireNegative(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Integer를 요구할 수 있다")
    void require_non_negative_integer() {
        assertThat(NumberPrecondition.requireNonNegative(0, "value")).isEqualTo(0);
        assertThat(NumberPrecondition.requireNonNegative(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> NumberPrecondition.requireNonNegative(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative.");
    }

    @Test
    @DisplayName("양수 Integer를 요구할 수 있다")
    void require_positive_integer() {
        assertThat(NumberPrecondition.requirePositive(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> NumberPrecondition.requirePositive(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
        assertThatThrownBy(() -> NumberPrecondition.requirePositive(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Integer를 요구할 수 있다")
    void require_non_positive_integer() {
        assertThat(NumberPrecondition.requireNonPositive(-1, "value")).isEqualTo(-1);
        assertThat(NumberPrecondition.requireNonPositive(0, "value")).isEqualTo(0);

        assertThatThrownBy(() -> NumberPrecondition.requireNonPositive(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-positive.");
    }

    @Test
    @DisplayName("Integer 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_integer_is_null() {
        assertThatThrownBy(() -> NumberPrecondition.requireNegative((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireNonNegative((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requirePositive((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireNonPositive((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }

    @Test
    @DisplayName("최소 Integer를 요구할 수 있다")
    void require_integer_at_least() {
        assertThat(NumberPrecondition.requireAtLeast(1, 1, "value")).isEqualTo(1);
        assertThat(NumberPrecondition.requireAtLeast(2, 1, "value")).isEqualTo(2);

        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(0, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at least 1.");
    }

    @Test
    @DisplayName("최대 Integer를 요구할 수 있다")
    void require_integer_at_most() {
        assertThat(NumberPrecondition.requireAtMost(1, 1, "value")).isEqualTo(1);
        assertThat(NumberPrecondition.requireAtMost(0, 1, "value")).isEqualTo(0);

        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(2, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at most 1.");
    }

    @Test
    @DisplayName("범위 안의 Integer를 요구할 수 있다")
    void require_integer_between() {
        assertThat(NumberPrecondition.requireBetween(1, 1, 3, "value")).isEqualTo(1);
        assertThat(NumberPrecondition.requireBetween(2, 1, 3, "value")).isEqualTo(2);
        assertThat(NumberPrecondition.requireBetween(3, 1, 3, "value")).isEqualTo(3);
        assertThat(NumberPrecondition.requireBetween(1, 1, 1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> NumberPrecondition.requireBetween(0, 1, 3, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(4, 1, 3, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1, 3, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Integer 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_integer_range_value_is_null() {
        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(null, 1, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(null, 1, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(null, 1, 3, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(1, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(1, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1, null, 3, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1, 1, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }

    @Test
    @DisplayName("음수 Long을 요구할 수 있다")
    void require_negative_long() {
        assertThat(NumberPrecondition.requireNegative(-1L, "value")).isEqualTo(-1L);

        assertThatThrownBy(() -> NumberPrecondition.requireNegative(0L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
        assertThatThrownBy(() -> NumberPrecondition.requireNegative(1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Long을 요구할 수 있다")
    void require_non_negative_long() {
        assertThat(NumberPrecondition.requireNonNegative(0L, "value")).isEqualTo(0L);
        assertThat(NumberPrecondition.requireNonNegative(1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> NumberPrecondition.requireNonNegative(-1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative.");
    }

    @Test
    @DisplayName("양수 Long을 요구할 수 있다")
    void require_positive_long() {
        assertThat(NumberPrecondition.requirePositive(1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> NumberPrecondition.requirePositive(0L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
        assertThatThrownBy(() -> NumberPrecondition.requirePositive(-1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Long을 요구할 수 있다")
    void require_non_positive_long() {
        assertThat(NumberPrecondition.requireNonPositive(-1L, "value")).isEqualTo(-1L);
        assertThat(NumberPrecondition.requireNonPositive(0L, "value")).isEqualTo(0L);

        assertThatThrownBy(() -> NumberPrecondition.requireNonPositive(1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-positive.");
    }

    @Test
    @DisplayName("Long 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_long_is_null() {
        assertThatThrownBy(() -> NumberPrecondition.requireNegative((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireNonNegative((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requirePositive((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireNonPositive((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }

    @Test
    @DisplayName("최소 Long을 요구할 수 있다")
    void require_long_at_least() {
        assertThat(NumberPrecondition.requireAtLeast(1L, 1L, "value")).isEqualTo(1L);
        assertThat(NumberPrecondition.requireAtLeast(2L, 1L, "value")).isEqualTo(2L);

        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(0L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at least 1.");
    }

    @Test
    @DisplayName("최대 Long을 요구할 수 있다")
    void require_long_at_most() {
        assertThat(NumberPrecondition.requireAtMost(1L, 1L, "value")).isEqualTo(1L);
        assertThat(NumberPrecondition.requireAtMost(0L, 1L, "value")).isEqualTo(0L);

        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(2L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at most 1.");
    }

    @Test
    @DisplayName("범위 안의 Long을 요구할 수 있다")
    void require_long_between() {
        assertThat(NumberPrecondition.requireBetween(1L, 1L, 3L, "value")).isEqualTo(1L);
        assertThat(NumberPrecondition.requireBetween(2L, 1L, 3L, "value")).isEqualTo(2L);
        assertThat(NumberPrecondition.requireBetween(3L, 1L, 3L, "value")).isEqualTo(3L);
        assertThat(NumberPrecondition.requireBetween(1L, 1L, 1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> NumberPrecondition.requireBetween(0L, 1L, 3L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(4L, 1L, 3L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1L, 3L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Long 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_long_range_value_is_null() {
        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(null, 1L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(null, 1L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(null, 1L, 3L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtLeast(1L, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireAtMost(1L, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1L, null, 3L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> NumberPrecondition.requireBetween(1L, 1L, null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }
}

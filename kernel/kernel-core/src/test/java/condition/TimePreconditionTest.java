package condition;

import com.lilamaris.cozyr.kernel.core.condition.TimePrecondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Time Precondition 테스트")
public class TimePreconditionTest {
    @Test
    @DisplayName("음수 Duration을 요구할 수 있다")
    void require_negative_duration() {
        var negative = Duration.ofSeconds(-1);

        assertThat(TimePrecondition.requireNegative(negative, "duration")).isEqualTo(negative);

        assertThatThrownBy(() -> TimePrecondition.requireNegative(Duration.ZERO, "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be negative.");
        assertThatThrownBy(() -> TimePrecondition.requireNegative(Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Duration을 요구할 수 있다")
    void require_non_negative_duration() {
        assertThat(TimePrecondition.requireNonNegative(Duration.ZERO, "duration")).isEqualTo(Duration.ZERO);
        assertThat(TimePrecondition.requireNonNegative(Duration.ofSeconds(1), "duration")).isEqualTo(Duration.ofSeconds(1));

        assertThatThrownBy(() -> TimePrecondition.requireNonNegative(Duration.ofSeconds(-1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be non-negative.");
    }

    @Test
    @DisplayName("양수 Duration을 요구할 수 있다")
    void require_positive_duration() {
        var positive = Duration.ofSeconds(1);

        assertThat(TimePrecondition.requirePositive(positive, "duration")).isEqualTo(positive);

        assertThatThrownBy(() -> TimePrecondition.requirePositive(Duration.ZERO, "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be positive.");
        assertThatThrownBy(() -> TimePrecondition.requirePositive(Duration.ofSeconds(-1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Duration을 요구할 수 있다")
    void require_non_positive_duration() {
        assertThat(TimePrecondition.requireNonPositive(Duration.ofSeconds(-1), "duration")).isEqualTo(Duration.ofSeconds(-1));
        assertThat(TimePrecondition.requireNonPositive(Duration.ZERO, "duration")).isEqualTo(Duration.ZERO);

        assertThatThrownBy(() -> TimePrecondition.requireNonPositive(Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be non-positive.");
    }

    @Test
    @DisplayName("Duration 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_duration_is_null() {
        assertThatThrownBy(() -> TimePrecondition.requireNegative(null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireNonNegative(null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requirePositive(null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireNonPositive(null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
    }

    @Test
    @DisplayName("최소 Duration을 요구할 수 있다")
    void require_duration_at_least() {
        assertThat(TimePrecondition.requireAtLeast(Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(TimePrecondition.requireAtLeast(Duration.ofSeconds(2), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(2));

        assertThatThrownBy(() -> TimePrecondition.requireAtLeast(Duration.ZERO, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be at least PT1S.");
    }

    @Test
    @DisplayName("최대 Duration을 요구할 수 있다")
    void require_duration_at_most() {
        assertThat(TimePrecondition.requireAtMost(Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(TimePrecondition.requireAtMost(Duration.ZERO, Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ZERO);

        assertThatThrownBy(() -> TimePrecondition.requireAtMost(Duration.ofSeconds(2), Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be at most PT1S.");
    }

    @Test
    @DisplayName("범위 안의 Duration을 요구할 수 있다")
    void require_duration_between() {
        assertThat(TimePrecondition.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(TimePrecondition.requireBetween(Duration.ofSeconds(2), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(2));
        assertThat(TimePrecondition.requireBetween(Duration.ofSeconds(3), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(3));
        assertThat(TimePrecondition.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));

        assertThatThrownBy(() -> TimePrecondition.requireBetween(Duration.ZERO, Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be between PT1S and PT3S.");
        assertThatThrownBy(() -> TimePrecondition.requireBetween(Duration.ofSeconds(4), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be between PT1S and PT3S.");
        assertThatThrownBy(() -> TimePrecondition.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Duration 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_duration_range_value_is_null() {
        assertThatThrownBy(() -> TimePrecondition.requireAtLeast(null, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireAtMost(null, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireBetween(null, Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireAtLeast(Duration.ofSeconds(1), null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireAtMost(Duration.ofSeconds(1), null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireBetween(Duration.ofSeconds(1), null, Duration.ofSeconds(3), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> TimePrecondition.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }
}

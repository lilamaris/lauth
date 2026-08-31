package condition;

import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("String Precondition 테스트")
public class StringPreconditionTest {
    @Test
    @DisplayName("빈 문자열이 아닌 값을 요구할 수 있다")
    void require_non_blank() {
        assertThat(StringPrecondition.requireNonBlank("value", "value")).isEqualTo("value");

        assertThatThrownBy(() -> StringPrecondition.requireNonBlank(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> StringPrecondition.requireNonBlank(" ", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not be blank.");
    }

    @Test
    @DisplayName("특정 문자열을 포함하지 않는 값을 요구할 수 있다")
    void require_not_contain() {
        assertThat(StringPrecondition.requireNotContain("text", "z", "text")).isEqualTo("text");

        assertThatThrownBy(() -> StringPrecondition.requireNotContain("text", "e", "text"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("text must not contain 'e'.");
        assertThatThrownBy(() -> StringPrecondition.requireNotContain(null, "x", "text"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("text must not be null.");
        assertThatThrownBy(() -> StringPrecondition.requireNotContain(" ", "x", "text"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("text must not be blank.");
        assertThatThrownBy(() -> StringPrecondition.requireNotContain("text", null, "text"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("s must not be null.");
        assertThatThrownBy(() -> StringPrecondition.requireNotContain("text", " ", "text"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("s must not be blank.");
    }
}

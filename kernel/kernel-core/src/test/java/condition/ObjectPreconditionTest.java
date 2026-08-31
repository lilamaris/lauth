package condition;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Object Precondition 테스트")
class ObjectPreconditionTest {
    @Test
    @DisplayName("null이 아닌 값을 요구할 수 있다")
    void require_non_null() {
        var value = new Object();

        assertThat(ObjectPrecondition.requireNonNull(value, "value")).isEqualTo(value);

        assertThatThrownBy(() -> ObjectPrecondition.requireNonNull(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }
}

package condition;

import com.lilamaris.cozyr.kernel.core.condition.CollectionPrecondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Collection Precondition 테스트")
class CollectionPreconditionTest {
    @Test
    @DisplayName("null 요소가 없는 컬렉션을 요구할 수 있다")
    void require_non_null_elements() {
        var value = List.of("a", "b");

        assertThat(CollectionPrecondition.requireNonNullElements(value, "value")).isEqualTo(value);
        assertThat(CollectionPrecondition.requireNonNullElements(List.of(), "value")).isEqualTo(List.of());

        assertThatThrownBy(() -> CollectionPrecondition.requireNonNullElements(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> CollectionPrecondition.requireNonNullElements(Arrays.asList("a", null), "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not contain null elements.");
    }
}

package academy.infra.render;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsciiHangmanBuilderTest {
    private final AsciiHangmanBuilder builder = new AsciiHangmanBuilder();

    @Test
    @DisplayName("Тест на границы (errors ≤ 0). Ожидание – самый первый шаг")
    void frame_errors_le_zero_returns_first_frame() {
        assertTrue(builder.frame(0, 6).contains
            ("""
          ➕---➕
          |     ┃
                ┃
                ┃
                ┃
                ┃
        ========="""
        ));
        assertTrue(builder.frame(-1, 6).contains
            ("""
          ➕---➕
          |     ┃
                ┃
                ┃
                ┃
                ┃
        ========="""
        ));
    }

    @Test
    @DisplayName("Тест на границы (errors ≥ maxAttempts) Ожидание – финальный шаг")
    void frame_errors_ge_max_returns_last_frame() {
        assertTrue(builder.frame(6, 6).contains("💀"));
        assertTrue(builder.frame(100, 6).contains("💀"));
    }

    @Test
    @DisplayName("Тест на проверку середины. Ожидание - вывод серединного шага")
    void frame_scaling_has_sane_midpoint() {
        assertTrue(builder.frame(3, 6).contains("😰"));
    }
}

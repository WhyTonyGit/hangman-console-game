package academy.core.eval;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import academy.core.eval.NonInteractiveEvaluatorRealize;
import static org.junit.jupiter.api.Assertions.*;

class NonInteractiveEvaluatorRealizeTest {

    private final NonInteractiveEvaluatorRealize eval = new NonInteractiveEvaluatorRealize();

    // -- Негативные тесты --

    @Test
    @DisplayName("пропущена одна буква. Ожидание – на её месте '*'")
    void neg_when_one_letter_missing() {
        assertEquals("*оре;NEG", eval.evaluate("море", "горе"));
    }

    @Test
    @DisplayName("пропущено много букв. Ожидание – '*' на месте не угаданных")
    void neg_with_extra_letters_in_guess() {
        assertEquals("*оло**о;NEG", eval.evaluate("волокно", "барахло"));
    }

    @Test
    @DisplayName("тест на дефис и цифры. Ожидание – вывод только угаданных")
    void digits_and_hyphen_respected() {
        assertEquals("ip*one*4-pro;NEG",
            eval.evaluate("iphone14-pro", "ipone4r-"));
    }

    @Test
    @DisplayName("тест на пропуски и пустоту в secret и guess. Ожидание – вывод ошибок об этом")
    void empty_or_blank_inputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate(null, "abc"));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("word", null));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("word", "   "));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("   ", "word"));
    }

    @Test
    @DisplayName("тест на недопустимые символы в secret и guess. Ожидание – вывод ошибок об этом")
    void secret_with_forbidden_chars_throws() {
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("окно!", "окно"));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("телефон", "теле фон"));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("окно_", "окно"));

        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("волокно", "толокно!!!"));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("шевроле тахо", "шевроле"));
        assertThrows(IllegalArgumentException.class, () -> eval.evaluate("окно", "окно_"));
    }

    // -- Позитивные тесты --

    @Test
    @DisplayName("слово угадано. Ожидание – вывод с позитивом")
    void pos_case_insensitive() {
        assertEquals("окно;POS", eval.evaluate("окно", "ОКНО"));
    }

    @Test
    @DisplayName("слово угадано, но прикол с большими и маленькими буквами. Ожидание – вывод с позитивом")
    void camel_case_insensitive() {
        assertEquals("ОкНо;POS", eval.evaluate("ОкНо", "ОКНО"));
    }
}

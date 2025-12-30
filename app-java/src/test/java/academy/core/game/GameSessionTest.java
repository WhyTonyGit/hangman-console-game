package academy.core.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSessionTest {

    // -- Негативные тесты --

    @Test
    @DisplayName("Тест на null secret. Ожидание – ошибка об этом")
    void constructor_nullSecret() {
        assertThrows(NullPointerException.class, () -> new GameSession(null, 5));
    }

    @Test
    @DisplayName("Тест на пустой secret. Ожидание – ошибка об этом")
    void constructor_blankSecret() {
        assertThrows(IllegalArgumentException.class, () -> new GameSession("   ",5));
    }

    @Test
    @DisplayName("Тест на некорректное значение maxAttempts. Ожидание – ошибка об этом")
    void constructor_maxAttempts() {
        assertThrows(IllegalArgumentException.class, () -> new GameSession("мышь",100));
        assertThrows(IllegalArgumentException.class, () -> new GameSession("мышь",-100));
    }

    @Test
    @DisplayName("Тест на ввод некорректных значений: null/пусто/длина != 1/недопустимый символ. Ожидание – " +
        "сообщение об ошибке(выводиться пользователю в консоль) и статус не меняется")
    void invalid_input() {
        GameSession s = new GameSession("кот", 3);
        var r1 = s.guessResult(null);
        assertEquals(Status.IN_PROGRESS, r1.status());
        assertTrue(r1.message().toLowerCase().contains("введите ровно одну букву/цифру/дефис"));

        var r2 = s.guessResult("");
        assertEquals(Status.IN_PROGRESS, r2.status());
        assertTrue(r2.message().toLowerCase().contains("введите ровно одну букву/цифру/дефис"));

        var r3 = s.guessResult("опа");
        assertEquals(Status.IN_PROGRESS, r3.status());
        assertTrue(r3.message().toLowerCase().contains("введите ровно одну букву/цифру/дефис"));

        var r4 = s.guessResult("~");
        assertEquals(Status.IN_PROGRESS, r4.status());
        assertTrue(r4.message().toLowerCase().contains("допустимы буквы/цифры/дефис"));
    }

    @Test
    @DisplayName("Тест на повторный ввод той же буквы Ожидание – не увеличивает счётчик ошибок, сообщает о повторе")
    void repeat_guess() {
        GameSession s = new GameSession("кошка", 4);
        s.guessResult("к");
        int e1 = s.getErrors();
        var r2 = s.guessResult("к");
        int e2 = s.getErrors();
        assertEquals(e1, e2, "Повтор ввода не должен увеличивать ошибки");
        assertTrue(r2.message().toLowerCase().contains("уже был"));
    }

    // -- Тесты на логику игры (Позитивные)--

    @Test
    @DisplayName("Тест: верная буква раскрывает все вхождения, неверная увеличивает ошибки")
    void correct_reveals_all_occurrences_wrong() {
        GameSession s = new GameSession("мАрк-2", 5);
        var r1 = s.guessResult("А");
        assertEquals("*А****", r1.mask());
        int e1 = s.getErrors();
        var r2 = s.guessResult("x");
        int e2 = s.getErrors();
        assertEquals("*А****", r2.mask());
        assertEquals(e1 + 1, e2, "Неверная буква увеличивает ошибки");
        assertEquals(Status.IN_PROGRESS, r2.status());
    }

    @Test
    @DisplayName("Тест: доигрываем до победы, статус WIN, попытки считаются корректно")
    void win_flow() {
        GameSession s = new GameSession("кот", 5);
        s.guessResult("к");
        s.guessResult("о");
        var r = s.guessResult("т");
        assertEquals(Status.WIN, r.status());
        assertTrue(r.message().toLowerCase().contains("победа! ты отгадал слово"), "Должно быть победное сообщение");
        assertEquals(0, r.attemptsFuture() - 5, "Подсчёт попыток");
    }

    @Test
    @DisplayName("Тест: проигрыш при превышении числа попыток, статус LOSE и сообщение со словом")
    void lose_flow() {
        GameSession s = new GameSession("кот", 2);
        var r1 = s.guessResult("x");
        assertEquals(Status.IN_PROGRESS, r1.status());
        var r2 = s.guessResult("y");
        assertEquals(Status.LOSE, r2.status());
        assertTrue(r2.message().toLowerCase().contains("ты проиграл. попытки закончились. слово было: ")
            , "В сообщении должно быть открыто секретное слово");
        assertEquals(0, r2.attemptsFuture());
    }
}

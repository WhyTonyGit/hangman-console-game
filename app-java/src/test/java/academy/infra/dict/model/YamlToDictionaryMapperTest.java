package academy.infra.dict.model;

import academy.infra.dict.mapper.YamlToDictionaryMapper;
import academy.ports.dict.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlToDictionaryMapperTest {

    private YamlToDictionaryMapper mapper;
    private Difficulty difficulty;

    @BeforeEach
    void setUp() {
        mapper = new YamlToDictionaryMapper();
        difficulty = Difficulty.EASY;
    }

    private RawWords rawWords(Map<String, Map<Difficulty, List<RawWord>>> category) {
        return new RawWords(category);
    }

    private RawWord word(String world, String hint) {
        return new RawWord(world, hint);
    }

    // -- Позитивные тесты --

    @Test
    @DisplayName("Классический ввод. Ожидание – нормальный парсинг без проблем")
    void mapsGoodTest() {
        var category = Map.of ("домашние животные", Map.of(difficulty, List.of(word("кот",
            "домашнее животное, как правило мяукает"))));

        var index = mapper.map(rawWords(category));
        assertTrue(index.containsKey("домашние животные"));
        var entry = index.get("домашние животные").get(difficulty);
        assertEquals(1, entry.size());

        var elem = entry.get(0);
        assertEquals("кот", elem.word());
        assertEquals("домашние животные", elem.category());
        assertEquals(difficulty, elem.difficulty());
        assertEquals("домашнее животное, как правило мяукает", elem.hint());

    }

    @Test
    @DisplayName("Тест на нормирование. Ожидание – нормирование и нормальный парсинг без проблем")
    void mapsGoodTestAndNormalize() {
        var category = Map.of (" домаШниЕ живОтнЫе  ", Map.of(difficulty, List.of(word(" КоТ",
            " домашнее животное, как правило мяукает "))));

        var index = mapper.map(rawWords(category));
        assertTrue(index.containsKey("домашние животные"));
        var entry = index.get("домашние животные").get(difficulty);
        assertEquals(1, entry.size());

        var elem = entry.get(0);
        assertEquals("кот", elem.word());
        assertEquals("домашние животные", elem.category());
        assertEquals(difficulty, elem.difficulty());
        assertEquals("домашнее животное, как правило мяукает", elem.hint());
    }

    //  -- Негативные тесты --

    // На тест с пустым List.of() ругается сам IDE на то, что мы передаём List.of(Object), а не List.of(RawWorld)
    // Поэтому этого теста тут нет, но я проверял, честно-честно :)

    @Test
    @DisplayName("Тест на пустой secret. Ожидание – ошибка об этом")
    void blankWordThrows() {
        var category = Map.of("животное", Map.of(difficulty, List.of(word("   ", "подсказка"))));
        var ex = assertThrows(IllegalArgumentException.class, () -> mapper.map(rawWords(category)));
        assertTrue(ex.getMessage().toLowerCase().contains("пустой secret"));
    }

    @Test
    @DisplayName("Тест на пустой hint. Ожидание – ошибка об этом")
    void blankHintThrows() {
        var category = Map.of("категория", Map.of(difficulty, List.of(word("слово", "   "))));
        var ex = assertThrows(IllegalArgumentException.class, () -> mapper.map(rawWords(category)));
        assertTrue(ex.getMessage().toLowerCase().contains("подсказка не может быть пустой"));
    }

    @Test
    @DisplayName("Тест на неправильный символ в secret. Ожидание – ошибка об этом")
    void invalidCharThrows() {
        var category = Map.of("животное", Map.of(difficulty, List.of(word("ёж!", "подсказка"))));
        var ex = assertThrows(IllegalArgumentException.class, () -> mapper.map(rawWords(category)));
        assertTrue(ex.getMessage().toLowerCase().contains("некорректный символ"));
    }

    @Test
    @DisplayName("Тест на дубликат слова secret в категории. Ожидание – ошибка об этом")
    void duplicateWordThrows() {
        var category = Map.of("животное", Map.of(difficulty, List.of(word("слово", "да")
            , word(" слово", "ага"))));
        var ex = assertThrows(IllegalStateException.class, () -> mapper.map(rawWords(category)));
        assertTrue(ex.getMessage().toLowerCase().contains("дубликат"));
    }
}

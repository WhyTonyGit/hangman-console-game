package academy.infra.dict;

import academy.ports.dict.Difficulty;
import academy.ports.dict.WordEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryDictionaryTest {
    private Map<String, EnumMap<Difficulty, List<WordEntry>>> index() {
        EnumMap<Difficulty, List<WordEntry>> animals = new EnumMap<>(Difficulty.class);
        animals.put(Difficulty.EASY, List.of(
            new WordEntry("кот", "животные", Difficulty.EASY, "мяу"),
            new WordEntry("собака", "животные", Difficulty.EASY, "гав")
        ));
        animals.put(Difficulty.MEDIUM, List.of(
            new WordEntry("барсук", "животные", Difficulty.MEDIUM, "имеет классный лечебный жир")
        ));
        EnumMap<Difficulty, List<WordEntry>> it = new EnumMap<>(Difficulty.class);
        it.put(Difficulty.EASY, List.of(
            new WordEntry("код", "айти", Difficulty.EASY, "ты это пишешь")
        ));
        return Map.of(
            "животные", animals,
            "айти", it
        );
    }


    // -- Негативные тесты--

    @Test
    @DisplayName("Пустой индекс. Ожидание – ошибка об этом")
    void emptyIndex(){
        assertThrows(IllegalArgumentException.class, () -> new InMemoryDictionary(Map.of()));
    }

    @Test
    @DisplayName("Несуществующая категория. Ожидание – ошибка об этом")
    void unknownCategory() {
        var dict = new InMemoryDictionary(index());
        assertThrows(IllegalArgumentException.class, () -> dict.select("птицы", Difficulty.EASY));
    }

    @Test
    @DisplayName("Пустой список слов для сложности. Ожидание – ошибка об этом")
    void emptyBucket() {
        EnumMap<Difficulty, List<WordEntry>> map = new EnumMap<>(Difficulty.class);
        map.put(Difficulty.HARD, List.of());
        var index = Map.of("животные", map);
        var dict = new InMemoryDictionary(index);
        assertThrows(IllegalStateException.class, () -> dict.select("животные", Difficulty.HARD));
    }

    // -- Позитивные тесты--

    @Test
    @DisplayName("Выбор по существующей категории и сложности. Ожидание – возврат одного из элементов")
    void selectReturnsOneOf() {
        var dict = new InMemoryDictionary(index());
        var res = dict.select("животные", Difficulty.EASY);
        assertEquals("животные", res.category());
        assertEquals(Difficulty.EASY, res.difficulty());
        assertTrue(List.of("кот", "собака").contains(res.word()));
    }

    @Test
    @DisplayName("Сложность = null. Ожидание – выбирается случайная сложность из доступных")
    void nullDifficulty() {
        var dict = new InMemoryDictionary(index());
        var res = dict.select("животные", null);
        assertEquals("животные", res.category());
        assertTrue(res.difficulty() == Difficulty.EASY || res.difficulty() == Difficulty.MEDIUM);
    }

    @Test
    @DisplayName("Категория = null. Ожидание – выбирается случайная категория из доступных при фиксированной сложности")
    void nullCategory() {
        var dict = new InMemoryDictionary(index());
        var res = dict.select(null, Difficulty.EASY);
        Set<String> allowedCategories = Set.of("животные", "айти");
        assertTrue(allowedCategories.contains(res.category()), "Категория должна быть из набора " + allowedCategories);
        assertEquals(Difficulty.EASY, res.difficulty());
    }

    @Test
    @DisplayName("Категория = null и сложность = null. Ожидание – выбирается произвольная валидная пара (категория, сложность)")
    void nullCategoryAndNullDifficulty() {
        var dict = new InMemoryDictionary(index());
        var res = dict.select(null, null);
        Set<String> allowedCategories = Set.of("животные", "айти");
        assertTrue(allowedCategories.contains(res.category()));

        if (res.category().equals("животные")) {
            assertTrue(res.difficulty() == Difficulty.EASY || res.difficulty() == Difficulty.MEDIUM);
        } else if (res.category().equals("айти")) {
            assertEquals(Difficulty.EASY, res.difficulty());
        }
    }
}

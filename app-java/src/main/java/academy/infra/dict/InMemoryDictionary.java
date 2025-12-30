package academy.infra.dict;

import academy.ports.dict.Dictionary;
import academy.ports.dict.Difficulty;
import academy.ports.dict.WordEntry;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InMemoryDictionary implements Dictionary {
    private final Map<String, EnumMap<Difficulty, List<WordEntry>>> index;

    public InMemoryDictionary(Map<String, EnumMap<Difficulty, List<WordEntry>>> index) {
        if (index == null || index.isEmpty()) {
            throw new IllegalArgumentException("Пустой словарь");
        }
        this.index = index;
    }

    @Override
    public WordEntry select(String category, Difficulty difficulty) {
        Random rand = new Random();
        if (category == null) {
            var cats = new ArrayList<>(index.keySet());
            category = cats.get(rand.nextInt(cats.size()));
        }
        category = category.toLowerCase();

        if (!index.containsKey(category)) {
            throw new IllegalArgumentException("Нет такой категории: " + category + ". Давай по-новой, Миша,...");
        }

        EnumMap<Difficulty, List<WordEntry>> internalMap = index.get(category);
        if (difficulty == null) {
            var diffs = new ArrayList<>(internalMap.keySet());
            difficulty = diffs.get(rand.nextInt(diffs.size()));
        }

        var entry = internalMap.get(difficulty);
        if (entry == null || entry.isEmpty()) {
            throw new IllegalStateException("Нет слов для этой сложности");
        }
        return entry.get(rand.nextInt(entry.size()));
    }
}

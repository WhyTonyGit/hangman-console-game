package academy.infra.dict.mapper;

import academy.infra.dict.model.RawWord;
import academy.infra.dict.model.RawWords;
import academy.ports.dict.Difficulty;
import academy.ports.dict.WordEntry;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlToDictionaryMapper implements DictionaryMapper {
    public Map<String, EnumMap<Difficulty, List<WordEntry>>> map(RawWords src) {
        var index = new HashMap<String, EnumMap<Difficulty, List<WordEntry>>>();
        Map<String, Map<Difficulty, List<RawWord>>> categories = src.categories();

        for (Map.Entry<String, Map<Difficulty, List<RawWord>>> cat : categories.entrySet()) {
            String category = normalizeCategory(cat.getKey());
            EnumMap<Difficulty, List<WordEntry>> perCategory = mapDifficultyBucket(category, cat.getValue());
            index.put(category, perCategory);
        }
        return index;
    }

    private String normalizeCategory(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Категория не должна быть null");
        }
        String category = word.toLowerCase().trim();
        if (category.isBlank()) {
            throw new IllegalArgumentException("Категория не должна быть пустой");
        }
        return category;
    }

    private String normalizeWord(String raw, String category, Difficulty difficulty) {
        String word = (raw == null) ? "" : raw.toLowerCase().trim();
        if (word.isBlank()) {
            throw new IllegalArgumentException("Пустой secret в категории " + category + " / " + difficulty);
        }
        return word;
    }

    private String normalizeHint(String raw, String category, Difficulty difficulty) {
        String hint = (raw == null) ? "" : raw.trim();
        if (hint.isBlank()) {
            throw new IllegalArgumentException("Подсказка не может быть пустой ("
                + category + " / " + difficulty + ")");
        }
        return hint;
    }

    private void validateChars(String word, String category, Difficulty difficulty) {
        for (char c : word.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-') {
                throw new IllegalArgumentException("Некорректный символ '" + c + "' "+
                    "в слове '" + word + "' в категории " + category + " / " + difficulty +
                    ". Разрешены только буквы, цифры и дефис");
            }
        }
    }

    private void checkUniqueWord(Set<String> seen, String word, String category, Difficulty difficulty) {
        if (!seen.add(word)) {
            throw new IllegalStateException(
                "Дубликат слова '" + word + "' в категории " + category + " / " + difficulty
            );
        }
    }

    private List<WordEntry> toEntries(String category, Difficulty difficulty, List<RawWord> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Пустой список слов в категории " + category + " / " + difficulty);
        }
        List<WordEntry> bucket = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (RawWord rawWord : words) {
            String word = normalizeWord(rawWord.word(), category, difficulty);
            validateChars(word, category, difficulty);
            checkUniqueWord(seen, word, category, difficulty);

            String hint = normalizeHint(rawWord.hint(), category, difficulty);

            bucket.add(new WordEntry(word, category, difficulty, hint));
        }
        return bucket;
    }

    private EnumMap<Difficulty, List<WordEntry>> mapDifficultyBucket(String category,
                                                                     Map<Difficulty, List<RawWord>> diffMap) {
        EnumMap<Difficulty, List<WordEntry>> result = new EnumMap<>(Difficulty.class);
        for (Map.Entry<Difficulty, List<RawWord>> e : diffMap.entrySet()) {
            Difficulty difficulty = e.getKey();
            List<WordEntry> entries = toEntries(category, difficulty, e.getValue());
            result.put(difficulty, entries);
        }
        return result;
    }
}

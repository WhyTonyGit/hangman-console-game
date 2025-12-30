package academy.infra.dict.mapper;

import academy.infra.dict.model.RawWords;
import academy.ports.dict.Difficulty;
import academy.ports.dict.WordEntry;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface DictionaryMapper {
    Map<String, EnumMap<Difficulty, List<WordEntry>>> map(RawWords src);
}

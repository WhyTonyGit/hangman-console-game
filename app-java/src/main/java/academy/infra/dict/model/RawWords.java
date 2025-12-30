package academy.infra.dict.model;

import academy.ports.dict.Difficulty;
import java.util.List;
import java.util.Map;

public record RawWords(Map<String, Map<Difficulty, List<RawWord>>> categories) {}

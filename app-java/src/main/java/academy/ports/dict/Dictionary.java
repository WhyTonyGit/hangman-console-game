package academy.ports.dict;

public interface Dictionary {
    WordEntry select(String category, Difficulty difficulty);
}

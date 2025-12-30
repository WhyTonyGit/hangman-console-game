package academy.ports.dict;

public enum Difficulty {
    EASY(6),
    MEDIUM(4),
    HARD(3);

    private final int maxAttempts;

    Difficulty(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }
}

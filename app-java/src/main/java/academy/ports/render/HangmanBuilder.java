package academy.ports.render;

public interface HangmanBuilder {
    String frame(int errors, int maxAttempted);
    String word(String mask);
}

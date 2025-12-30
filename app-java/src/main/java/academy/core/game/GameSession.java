package academy.core.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private static final char MASK_CHAR = '*';

    private final int attemptCount;
    private final char[] secret;
    private final char[] secretLower;
    private final char[] mask;
    private int hidden;
    private int errors = 0;
    private final Set<Character> guess = new HashSet<>();

    private Status status = Status.IN_PROGRESS;
    private String message = "Игра началась, введи одну букву";

    public GameSession(String secret, Integer maxAttempts) {
        if (secret == null) {
            throw new NullPointerException("secret не должен быть null");
        }
        if (secret.isBlank()) {
            throw new IllegalArgumentException("Пустое слово загадывать нельзя");
        }
        if (maxAttempts < GameRules.MIN_ATTEMPTS || maxAttempts > GameRules.MAX_ATTEMPTS) {
            throw new IllegalArgumentException(
                "Допустимое количество попыток: " + GameRules.MIN_ATTEMPTS + "–" + GameRules.MAX_ATTEMPTS
            );
        }

        this.secret = secret.toCharArray();
        this.secretLower = secret.toLowerCase().toCharArray();
        this.attemptCount = maxAttempts;

        this.mask = new char[secret.length()];
        Arrays.fill(mask, MASK_CHAR);
        this.hidden = mask.length;
    }

    public Status getStatus() {
        return status;
    }
    public int getMaxAttempt() {
        return attemptCount;
    }
    public int getErrors() {
        return errors;
    }

    private String buildMask() {
        return new String(mask);
    }

    public GuessResult write(String message) {
        return new GuessResult(buildMask(), errors, Math.max(0, attemptCount - errors), status, message);
    }

    public GuessResult guessResult(String input) {
        if (status != Status.IN_PROGRESS) {
            return write("Игра завершена " + status);
        }

        if (input == null || input.isBlank() || input.length() != 1) {
            return write("Введите ровно ОДНУ букву/цифру/дефис");
        }

        char let = Character.toLowerCase(input.charAt(0));
        if (!Character.isLetterOrDigit(let) && let != '-') {
            return write("Допустимы буквы/цифры/дефис");
        }

        if (guess.contains(let)) {
            return write("Символ " + let + " уже был");
        }

        guess.add(let);

        boolean hit = false;
        for (int i = 0; i < secretLower.length; i++) {
            if (secretLower[i] == let && mask[i] == MASK_CHAR) {
                mask[i] = secret[i];
                hidden--;
                hit = true;
            }
        }

        if (!hit)  {
            errors++;
            message = "Буквы '" + let + "' в загаданном слове не оказалось";
        } else {
            message = "Есть такая буква '" + let + "', откройте!";
        }

        if (hidden == 0) {
            status = Status.WIN;
            message = "Победа! Ты отгадал слово";
        } else if (errors >= attemptCount) {
            status = Status.LOSE;
            message = "Ты проиграл. Попытки закончились. Слово было: " + new String(secret);
        }

        return new GuessResult(buildMask(), errors, attemptCount - errors, status, message);
    }
}

package academy.core.eval;

import java.util.Set;
import java.util.stream.Collectors;

public class NonInteractiveEvaluatorRealize implements NonInteractiveEvaluator {

    private static final char MASK_CHAR = '*';

    @Override
    public String evaluate(String secret, String guess) {
        isCorrectInput(secret, "secret");
        isCorrectInput(guess, "guess");

        String secretLower = secret.toLowerCase();
        Set<Character> guessSet = guess.toLowerCase()
            .chars()
            .mapToObj(ch -> (char) ch)
            .collect(Collectors.toSet());

        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < secret.length(); i++) {
            char ch = secretLower.charAt(i);
            if (!guessSet.contains(ch)) {
                mask.append(MASK_CHAR);
            } else {
                mask.append(secret.charAt(i));
            }
        }
        String masked = mask.toString();
        Result res = masked.equals(secret) ? Result.POS : Result.NEG;

        return masked + ";" + res.name();
    }

    private void isCorrectInput(String input, String nameOfInput) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(nameOfInput + " не должен быть пустым");
        }
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-') {
                throw new IllegalArgumentException(nameOfInput + " не может содержать ничего, кроме букв" +
                    ", цифр и дефисов");
            }
        }
    }

    private enum Result { POS, NEG }
}

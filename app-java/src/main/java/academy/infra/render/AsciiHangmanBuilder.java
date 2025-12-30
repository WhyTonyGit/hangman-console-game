package academy.infra.render;

import academy.core.game.GameRules;
import academy.ports.render.HangmanBuilder;
import java.util.List;

public class AsciiHangmanBuilder implements HangmanBuilder {
    private static final List<String> STEPS = List.of(
        """
          ➕---➕
          |     ┃
                ┃
                ┃
                ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          😐    ┃
                ┃
                ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          😟    ┃
          ┃     ┃
                ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          😰    ┃
         /┃     ┃
                ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          🫡    ┃
         /┃\\   ┃
                ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          😵    ┃
         /┃\\   ┃
         /      ┃
                ┃
        =========""",

        """
          ➕---➕
          |     ┃
          💀    ┃
         /┃\\   ┃
         / \\   ┃
                ┃
        ========="""
    );

    @Override
    public String frame(int errors, int maxAttempts) {
        if (maxAttempts <= 0) {
            maxAttempts = GameRules.MIN_ATTEMPTS;
        }
        if (errors <= 0) {
            return STEPS.getFirst();
        }
        if (errors >= maxAttempts) {
            return STEPS.getLast();
        }

        int index = (int) Math.round((double) errors / maxAttempts * (STEPS.size() - 1));
        index = Math.max(0, Math.min(STEPS.size() - 1, index));

        return STEPS.get(index);
    }

    @Override
    public String word(String mask) {
        return "Слово: " + mask;
    }
}

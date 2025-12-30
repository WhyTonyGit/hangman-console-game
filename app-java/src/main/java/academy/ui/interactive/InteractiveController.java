package academy.ui.interactive;

import academy.core.game.GameRules;
import academy.core.game.GameSession;
import academy.core.game.GuessResult;
import academy.core.game.Status;
import academy.ports.dict.Dictionary;
import academy.ports.dict.Difficulty;
import academy.ports.dict.WordEntry;
import academy.ports.io.ConsoleIO;
import academy.ports.render.HangmanBuilder;
import java.util.Objects;

public class InteractiveController {
    private final ConsoleIO io;
    private final Dictionary dictionary;
    private final HangmanBuilder hangmanBuilder;

    public InteractiveController(ConsoleIO io, Dictionary dictionary, HangmanBuilder hangmanBuilder) {
        this.io = Objects.requireNonNull(io);
        this.dictionary = Objects.requireNonNull(dictionary);
        this.hangmanBuilder = Objects.requireNonNull(hangmanBuilder);
    }

    private void workBuild(GuessResult gRes) {
        io.clean();
        int maxAttempts = gRes.attemptsNow() + gRes.attemptsFuture();
        io.writeLn(hangmanBuilder.frame(gRes.attemptsNow(), maxAttempts));
        io.writeLn(hangmanBuilder.word(gRes.mask()));
        io.writeLn("Ошибок: " + gRes.attemptsNow() + ", осталось попыток: " + gRes.attemptsFuture());
        io.writeLn(gRes.message());
    }

    public void run(String category, Difficulty difficulty, Integer maxAttempts) {
        WordEntry wordEntry = dictionary.select(category, difficulty);

        int countAttempts = normalizeAttempts(maxAttempts, wordEntry);
        maybeReportChanges(maxAttempts, countAttempts);

        GameSession session = new GameSession(wordEntry.word(), countAttempts);

        io.clean();
        io.writeLn("Категория: " + wordEntry.category() + ", сложность: " + wordEntry.difficulty());
        io.writeLn("Попыток " + session.getMaxAttempt());
        io.writeLn("Ты можешь попросить подсказку, для этого введи: '?'");
        workBuild(session.write("Игра стартовала"));

        while (session.getStatus() == Status.IN_PROGRESS) {
            io.writeLn("Введите 1 символ для проверки (или '?' для подсказки)");
            String str = io.readLine();
            if (str == null) {
                str = "";
            }
            else {
                str = str.trim();
            }

            if (str.equals("?")) {
                if (wordEntry.hint() != null && !wordEntry.hint().isBlank()) {
                    io.writeLn("Подсказка: " + wordEntry.hint());
                } else {
                    // никогда не должно срабатывать
                    io.writeLn("Подсказка недоступна для этого слова");
                }
                io.readLine();
                workBuild(session.write("Подсказка была показана"));
                continue;
            }
            GuessResult gRes = session.guessResult(str);
            workBuild(gRes);
        }
        io.writeLn("Игра окончена: " + session.getStatus());
    }

    private int normalizeAttempts(Integer attempts, WordEntry wordEntry) {
        int base = (attempts != null) ? attempts : wordEntry.difficulty().getMaxAttempts();
        return Math.max(GameRules.MIN_ATTEMPTS, Math.min(GameRules.MAX_ATTEMPTS, base));
    }

    private void maybeReportChanges(Integer initial, int normalized) {
        if (initial != null && !initial.equals(normalized)) {
            io.writeLn("Число попыток скорректировано с " + initial + " до " + normalized +
                " (допустимый диапазон: " + GameRules.MIN_ATTEMPTS + "–" + GameRules.MAX_ATTEMPTS + ")");
        }
    }
}

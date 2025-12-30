package academy.core.game;

public record GuessResult (
    String mask,
    int attemptsNow,
    int attemptsFuture,
    Status status,
    String message
    ) {}

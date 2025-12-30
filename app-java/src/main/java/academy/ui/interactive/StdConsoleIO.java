package academy.ui.interactive;

import academy.ports.io.ConsoleIO;
import java.io.PrintStream;
import java.util.Scanner;

public class StdConsoleIO implements ConsoleIO {
    private final Scanner input = new Scanner(System.in);
    private final PrintStream output = System.out;

    public void writeLn(String str) {
        output.println(str);
    }

    public String readLine() {
        return input.nextLine();
    }

    public void clean() {
        output.println("\n");
    }
}

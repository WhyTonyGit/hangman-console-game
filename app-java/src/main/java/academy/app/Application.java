package academy.app;

import academy.config.AppConfig;
import academy.core.eval.NonInteractiveEvaluator;
import academy.core.eval.NonInteractiveEvaluatorRealize;
import academy.infra.dict.InMemoryDictionary;
import academy.infra.dict.loader.DictionaryLoader;
import academy.infra.dict.loader.YamlDictionaryLoader;
import academy.infra.dict.mapper.YamlToDictionaryMapper;
import academy.infra.dict.model.RawWords;
import academy.infra.render.AsciiHangmanBuilder;
import academy.ports.dict.Dictionary;
import academy.ports.io.ConsoleIO;
import academy.ports.render.HangmanBuilder;
import academy.ui.interactive.InteractiveController;
import academy.ui.interactive.StdConsoleIO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import static java.util.Objects.nonNull;

@Command(name = "Application Example", version = "Example 1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final ObjectReader YAML_READER =
        new ObjectMapper(new YAMLFactory()).findAndRegisterModules().reader();
    private static final Predicate<String[]> IS_TESTING_MODE = words -> nonNull(words) && words.length == 2;

    @Option(
        names = {"-s", "--font-size"},
        description = "Font size")
    int fontSize;

    @Parameters(
        paramLabel = "<word>",
        description = "Words pair for testing mode")
    private String[] words;

    @Option(
        names = {"-c", "--config"},
        description = "Path to YAML config file")
    private File configPath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        AppConfig config = loadConfig();
        LOGGER.atInfo().addKeyValue("config", config).log("Config content");

        // ... logic
        if (IS_TESTING_MODE.test(config.words())) {
            LOGGER.atInfo().log("Non-interactive testing mode enabled");

            var word = config.words()[0];
            var userInput = config.words()[1];

            if (word == null || userInput == null) {
                System.out.println("Invalid input: null");
                return;
            }

            NonInteractiveEvaluator nonInter = new NonInteractiveEvaluatorRealize();
            String out = nonInter.evaluate(word, userInput);
            System.out.println(out);

        } else {
            LOGGER.atInfo().log("Interactive mode enabled");
            ConsoleIO io = new StdConsoleIO();
            HangmanBuilder builder = new AsciiHangmanBuilder();

            ObjectReader reader = new ObjectMapper(new YAMLFactory()).findAndRegisterModules().reader();
            DictionaryLoader loader = new YamlDictionaryLoader(reader);

            try (InputStream is = Application.class.getResourceAsStream("/words.yml")) {
                RawWords rawWords = loader.load(is);
                var mapper = new YamlToDictionaryMapper().map(rawWords);
                Dictionary dict = new InMemoryDictionary(mapper);
                InteractiveController controller = new InteractiveController(io, dict, builder);
                controller.run(null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AppConfig loadConfig() {
        // fill with cli options
        if (configPath == null) {
            return new AppConfig(fontSize, words);
        }

        // use config file if provided
        try {
            return YAML_READER.readValue(configPath, AppConfig.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

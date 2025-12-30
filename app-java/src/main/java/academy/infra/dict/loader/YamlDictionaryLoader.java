package academy.infra.dict.loader;

import academy.infra.dict.model.RawWords;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;


public final class YamlDictionaryLoader implements DictionaryLoader {
    private final ObjectReader objectReader;

    public YamlDictionaryLoader(ObjectReader objectReader) {
        this.objectReader = Objects.requireNonNull(objectReader);
    }

    public RawWords load(InputStream in) {
        Objects.requireNonNull(in, "InputStream не может быть пустым");

        try {
            var yaml = objectReader.readValue(in, RawWords.class);
            if (yaml == null || yaml.categories() == null || yaml.categories().isEmpty()) {
                throw new IllegalStateException("Пустые или отсутствующие категории в файле-словаре");
            }
            return yaml;
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка при парсинге", e);
        }
    }
}

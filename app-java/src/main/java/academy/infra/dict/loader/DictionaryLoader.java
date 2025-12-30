package academy.infra.dict.loader;

import academy.infra.dict.model.RawWords;
import java.io.InputStream;

public interface DictionaryLoader {
    RawWords load(InputStream in);
}

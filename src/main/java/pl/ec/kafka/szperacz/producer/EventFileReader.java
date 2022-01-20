package pl.ec.kafka.szperacz.producer;

import io.micronaut.core.io.file.DefaultFileSystemResourceLoader;
import io.micronaut.core.io.file.FileSystemResourceLoader;
import java.io.FileNotFoundException;
import java.net.URL;
import javax.inject.Singleton;
import lombok.SneakyThrows;

@Singleton
public class EventFileReader {

    FileSystemResourceLoader resourceLoader = new DefaultFileSystemResourceLoader();

    @SneakyThrows
    URL read(String fileName) {
        String path = "test/data/" + fileName;
        return resourceLoader.getResource(path)
            .orElseThrow(FileNotFoundException::new);
    }

}

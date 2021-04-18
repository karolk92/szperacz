package pl.ec.kafka.szperacz.catalog;

import com.google.common.base.Preconditions;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.ec.kafka.szperacz.catalog.model.CatalogContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogKafkaContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogPreprocessingContent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FileNameAssignor {

    static String assignFileName(CatalogContent content, boolean gzipped) {
        if (content instanceof CatalogPreprocessingContent) {
            var typedContent = (CatalogPreprocessingContent) content;
            return preprocessingContentFileName(List.of(
                typedContent.getInputTopic(),
                typedContent.getBufferTopic(),
                typedContent.getOutputTopic()
            ), gzipped);
        } else if (content instanceof CatalogKafkaContent) {
            var typedContent = (CatalogKafkaContent) content;
            return kafkaContentFileName(typedContent.getTopic(), gzipped);
        }

        throw new IllegalArgumentException("Unable to assign file name: unknown type");
    }

    static String preprocessingContentFileName(List<String> topics, boolean gzipped) {
        Preconditions.checkArgument(topics.size() == 3);
        return topics.get(0) + "." + topics.get(1) + "." + topics.get(2) + getSuffix(gzipped);
    }

    static String kafkaContentFileName(String topic, boolean gzipped) {
        return topic + getSuffix(gzipped);
    }

    static String getSuffix(boolean gzipped) {
        return gzipped ? ".zip" : ".json";
    }

}

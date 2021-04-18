package pl.ec.kafka.szperacz.catalog.model;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntrySizeEstimator {

    public static int kafkaEventsNumber(String content) {
        return countOfOccurrences(content, "dataId");
    }

    public static int preprocessingInputTopicEventsNumber(CreatePreprocessingEntryRequest request) {
        return -1;
    }

    public static int preprocessingBufferedTopicEventsNumber(CreatePreprocessingEntryRequest request) {
        return -1;
    }

    public static int preprocessingOutputTopicEventsNumber(CreatePreprocessingEntryRequest request) {
        return -1;
    }

    private static int countOfOccurrences(String str, String subStr) {
        return (str.length() - str.replaceAll(Pattern.quote(subStr), "").length()) / subStr.length();
    }
}

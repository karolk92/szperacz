package pl.ec.kafka.szperacz.search.preprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import pl.ec.kafka.szperacz.search.kafka.Event;
import pl.ec.kafka.szperacz.search.kafka.KafkaSearchingRequestScopeFacade;
import pl.ec.kafka.szperacz.search.preprocessing.model.MapCluster;
import pl.ec.kafka.szperacz.search.preprocessing.search.BufferedEvent;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingRequest;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingResponse;

@RequiredArgsConstructor
@Singleton
public class PreprocessingFacade {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final KafkaSearchingRequestScopeFacade searchingFacade;
    private final MapRepository mapRepository;

    private Map<String, MapCluster> clusters;
    private String mapVersion;

    public MapCluster getMapByGantry(String gantryName) {
        tryToInitialize();
        return clusters.get(gantryName);
    }

    public SearchPreprocessingResponse search(SearchPreprocessingRequest request) {
        var inputSearchResult = searchingFacade.search(request.getInputTopic(), request.getFrom(), request.getTo(), request.getKey());

        if (CollectionUtils.isEmpty(inputSearchResult.getEvents())) {
            return new SearchPreprocessingResponse(List.of());
        }

        var broadenedTo = request.getTo().plusMinutes(request.getSearchBroadeningLimitInMinutes());
        var buffersSearchResult = searchingFacade.search(request.getBufferTopic(), request.getFrom(), broadenedTo, request.getKey());
        var outputSearchResult = searchingFacade.search(request.getOutputTopic(), request.getFrom(), broadenedTo, request.getKey());

        var bufferedEvents = Lists.<BufferedEvent>newArrayList();

        // Correlate with buffers
        if (CollectionUtils.isNotEmpty(buffersSearchResult.getEvents())) {
            var bufferIterator = buffersSearchResult.getEvents().iterator();
            var currentBuffer = bufferIterator.next();
            var bufferDataIds = extractDataIds(currentBuffer.getBody());
            var builder = BufferedEvent.aBufferedEvent().bufferEvent(currentBuffer);

            for (Event inputEvent : inputSearchResult.getEvents()) {
                var inputEventDataId = extractDataId(inputEvent.getBody());
                if (currentBuffer == null) {
                    builder.inputEvent(inputEvent);
                } else if (bufferDataIds.contains(inputEventDataId)) {
                    inputEvent.setBufferIndex(bufferDataIds.indexOf(inputEventDataId) + 1);
                    builder.inputEvent(inputEvent);
                } else {

                    // Either new buffer or no related buffer exists
                    bufferedEvents.add(builder
                        .bufferPartition(buffersSearchResult.getPartition())
                        .inputPartition(inputSearchResult.getPartition())
                        .outputPartition(outputSearchResult.getPartition())
                        .build());

                    // Still some buffers left to be analyzed
                    if (bufferIterator.hasNext()) {
                        currentBuffer = bufferIterator.next();
                        bufferDataIds = extractDataIds(currentBuffer.getBody());
                        if (!bufferDataIds.contains(inputEventDataId)) {
                            throw new IllegalStateException("Something went really wrong!");
                        }
                        inputEvent.setBufferIndex(1);
                        builder = BufferedEvent.aBufferedEvent()
                            .bufferEvent(currentBuffer)
                            .inputEvent(inputEvent);

                        // No buffers left yet some input events left
                    } else {
                        currentBuffer = null;
                        builder = BufferedEvent.aBufferedEvent();
                    }
                }
            }

            bufferedEvents.add(builder
                .bufferPartition(buffersSearchResult.getPartition())
                .inputPartition(inputSearchResult.getPartition())
                .outputPartition(outputSearchResult.getPartition())
                .build());
        }

        addOutputEventsToBufferedEvents(bufferedEvents, outputSearchResult.getEvents());
        removeLeadingAndTrailingOutputEvents(bufferedEvents);

        var filtered = bufferedEvents.stream()
            .filter(x -> x.getOutputEvents() != null && !x.getOutputEvents().isEmpty())
            .collect(Collectors.toList());

        return new SearchPreprocessingResponse(filtered);
    }

    private void removeLeadingAndTrailingOutputEvents(List<BufferedEvent> bufferedEvents) {
        for (var bufferedEvent : bufferedEvents) {
            var inputDataIds = extractDataIds(bufferedEvent.getInputEvents());
            var outputDataIds = extractDataIds(bufferedEvent.getOutputEvents());

            if (outputDataIds.isEmpty()) {
                continue;
            }

            bufferedEvent.getOutputEvents().removeAll(
                outputDataIds.stream()
                    .filter(outputDataId -> !inputDataIds.contains(outputDataId))
                    .map(outputDataId -> bufferedEvent.getOutputEvents().get(outputDataIds.indexOf(outputDataId)))
                    .collect(Collectors.toList()));
        }
    }

    private List<String> extractDataIds(List<Event> events) {
        return events == null ? List.of() : events.stream().map(e -> extractDataId(e.getBody())).collect(Collectors.toList());
    }

    @SneakyThrows
    private List<String> extractDataIds(String body) {
        return MAPPER.readTree(body).get("elements").findValuesAsText("dataId");
    }

    @SneakyThrows
    private String extractDataId(String body) {
        return MAPPER.readTree(body).get("dataId").asText();
    }

    private void addOutputEventsToBufferedEvents(List<BufferedEvent> bufferedEvents, List<Event> outputEvents) {
        List<String> dataIds = outputEvents.stream().map(e -> extractDataId(e.getBody())).collect(Collectors.toList());

        for (var bufferedEvent : bufferedEvents) {
            if (bufferedEvent.getBufferEvent() == null) {
                continue;
            }
            var bufferDataIds = extractDataIds(bufferedEvent.getBufferEvent().getBody());
            for (var bufferDataId : bufferDataIds) {
                var index = dataIds.indexOf(bufferDataId);
                if (index != -1) {
                    var outputEvent = outputEvents.get(index);
                    outputEvent.setBufferIndex(bufferDataIds.indexOf(bufferDataId) + 1);
                    bufferedEvent.addOutputEvents(outputEvent);
                }
            }
        }
    }

    private void tryToInitialize() {
        var currentVersion = mapRepository.getVersion();
        if (mapVersion == null || !mapVersion.equals(currentVersion)) {
            var maps = mapRepository.getMapClusters();
            this.clusters = maps.stream()
                .flatMap(this::toPerGantryEntryStream)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            this.mapVersion = currentVersion;
        }
    }

    private Stream<Entry<String, MapCluster>> toPerGantryEntryStream(MapCluster map) {
        return map.getGantries().stream().map(gantry -> Map.entry(gantry.getName(), map));
    }
}

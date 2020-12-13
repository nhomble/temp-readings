package io.github.nhomble.storm.temp.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nhomble.storm.temp.model.TempReading;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

@Slf4j
public class MapperBolt extends BaseRichBolt {

    private final ObjectMapper objectMapper;

    private OutputCollector collector;

    public MapperBolt() {
        super();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @SneakyThrows
    @Override
    public void execute(Tuple input) {
        log.info("Executing tuple={} length={} num4={}", input, input.size(), input.getString(4));
        String json = input.getString(4);
        TempReading reading = objectMapper.readValue(json, TempReading.class);
        log.info("Parsed json={}", reading);
        collector.emit(input, new Values(reading.getTemperature(), reading.getDirection()));
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("temperature", "location"));
    }
}

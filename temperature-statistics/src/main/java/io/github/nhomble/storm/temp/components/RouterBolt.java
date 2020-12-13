package io.github.nhomble.storm.temp.components;

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
public class RouterBolt extends BaseRichBolt {

    public static final String STREAM_IN = "in";
    public static final String STREAM_OUT = "out";

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {
        int temp = input.getInteger(0);
        String direction = input.getString(1);
        log.info("Executing temp={} direction={}", temp, direction);
        if ("in".equalsIgnoreCase(direction)) {
            outputCollector.emit(STREAM_IN, input, new Values(temp, direction));
        } else if ("out".equalsIgnoreCase(input.getString(1))) {
            outputCollector.emit(STREAM_OUT, input, new Values(temp, direction));
        } else {
            log.error("Unknown direction! direction={}", direction);
        }
        outputCollector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(STREAM_IN, new Fields("temperature", "location"));
        declarer.declareStream(STREAM_OUT, new Fields("temperature", "location"));
    }
}

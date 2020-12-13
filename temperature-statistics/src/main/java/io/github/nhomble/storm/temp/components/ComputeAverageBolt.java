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
public class ComputeAverageBolt extends BaseRichBolt {

    private long count = 0;
    private long sum = 0;
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        int val = input.getInteger(0);
        sum += val;
        count += 1;
        collector.emit(input, new Values(sum / count, count));
        log.info("New average={}", sum / count);
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("average", "count"));
    }
}

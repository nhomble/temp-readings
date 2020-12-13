package io.github.nhomble.storm.example.logger;

import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.testing.TestWordSpout;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

@Slf4j
public class LoggingTopology extends ConfigurableTopology {

    public static void main(String[] args) {
        ConfigurableTopology.start(new LoggingTopology(), args);
    }

    protected int run(String[] args) throws Exception {
        String topologyName = "logging";
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("word", new TestWordSpout(), 1);
        builder.setBolt("exclaim1", new ExclamationBolt(), 1).shuffleGrouping("word");
        builder.setBolt("exclaim2", new ExclamationBolt(), 1).shuffleGrouping("exclaim1");

        conf.put("storm.log.dir", "/logs");
        conf.setDebug(true);
        conf.setNumWorkers(3);

        return submit(topologyName, conf, builder);
    }

    public static class ExclamationBolt extends BaseRichBolt {
        OutputCollector collector;

        @Override
        public void prepare(Map<String, Object> conf, TopologyContext context, OutputCollector collector) {
            log.info("Prepare!");
            this.collector = collector;
        }

        @Override
        public void execute(Tuple tuple) {
            log.info("Executing {}", tuple);
            collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
            collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));
        }

    }
}

package io.github.nhomble.storm.temp;

import io.github.nhomble.storm.temp.components.ComputeAverageBolt;
import io.github.nhomble.storm.temp.components.MapperBolt;
import io.github.nhomble.storm.temp.components.RouterBolt;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;

import java.util.Properties;

@Slf4j
public class TemperatureTopology extends ConfigurableTopology {

    public static void main(String[] args) {
        ConfigurableTopology.start(new TemperatureTopology(), args);
    }

    private void usage() {
        System.err.println("[bootstrap servers] [topic] [out topic] [in topic]");
    }

    protected int run(String[] args) throws Exception {
        String topologyName = "temperature";
        if (args.length != 4) {
            usage();
            return -1;
        }
        TopologyBuilder builder = new TopologyBuilder();
        KafkaSpoutConfig<String, String> config = KafkaSpoutConfig
                .builder(args[0], args[1])
                .build();
        log.info("Kafka properties={}", config.getKafkaProps());
        builder.setSpout("data", new KafkaSpout<>(config), 1);
        builder.setBolt("readings", new MapperBolt(), 1).shuffleGrouping("data");
        builder.setBolt("router", new RouterBolt(), 1).shuffleGrouping("readings");
        builder.setBolt("avgIn", new ComputeAverageBolt(), 1).shuffleGrouping("router", RouterBolt.STREAM_IN);
        builder.setBolt("avgOut", new ComputeAverageBolt(), 1).shuffleGrouping("router", RouterBolt.STREAM_OUT);

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", args[0]);
        producerProps.put("request.required.acks", "1");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        builder.setBolt("avgInPublish", new KafkaBolt<>()
                        .withProducerProperties(producerProps)
                        .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>())
                        .withTopicSelector(args[2]),
                1).shuffleGrouping("avgIn");
        builder.setBolt("avgOutPublish", new KafkaBolt<>()
                        .withProducerProperties(producerProps)
                        .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<>())
                        .withTopicSelector(args[3]),
                1).shuffleGrouping("avgOut");
        conf.setNumWorkers(10);

        return submit(topologyName, conf, builder);
    }
}

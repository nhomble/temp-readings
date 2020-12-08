package io.github.nhomble.storm.temp;

import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;

public class WrappedKafkaSpout extends KafkaSpout<String, String> {
    public WrappedKafkaSpout() {
        super(KafkaSpoutConfig
                .builder("bootstrap", "topic3")
                .build()
        );
    }
}

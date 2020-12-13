package io.github.nhomble.storm.temp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TempReading {
    private String description;

    // mm-dd-yyyy hh:mm:ss
    private String timestamp;

    private int temperature;

    private String direction;
}

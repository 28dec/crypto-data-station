package me.longnh.cryptodatastation.fear_greed_index.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FearAndGreedProviderRecord {
    private Integer value;
    @JsonProperty("value_classification")
    private String valueClassification;
    @JsonProperty("timestamp")
    private String date;
}

package me.longnh.cryptodatastation.fear_greed_index.model;

import lombok.Data;

import java.util.List;

@Data
public class FearAndGreedIndexProviderResponse {
    private String name;
    private List<FearAndGreedProviderRecord> data;
}

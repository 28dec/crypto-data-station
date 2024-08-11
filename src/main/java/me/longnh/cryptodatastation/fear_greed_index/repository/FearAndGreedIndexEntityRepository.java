package me.longnh.cryptodatastation.fear_greed_index.repository;

import me.longnh.cryptodatastation.fear_greed_index.entity.FearAndGreedIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FearAndGreedIndexEntityRepository extends JpaRepository<FearAndGreedIndexEntity, String> {
    FearAndGreedIndexEntity findByDate(String date);
}
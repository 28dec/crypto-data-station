package me.longnh.cryptodatastation.fear_greed_index.repository;

import me.longnh.cryptodatastation.fear_greed_index.entity.CoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CoinPriceRepository extends JpaRepository<CoinPrice, String> {

  @Query(value = """
          SELECT cp
          FROM CoinPrice cp
          WHERE cp.ticker = :ticker AND cp.candleEndDatetime < :utcDateTime
          ORDER BY cp.priceHigh DESC
          LIMIT 1
          """)
  public Optional<CoinPrice> findAth(@Param(value = "ticker") String ticker, @Param(value = "utcDateTime") String utcDateTime);

  Optional<CoinPrice> findFirstByTickerAndCandleBeginDatetime(String ticker, String utcDateTime);
}
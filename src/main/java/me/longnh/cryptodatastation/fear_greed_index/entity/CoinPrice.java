package me.longnh.cryptodatastation.fear_greed_index.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import me.longnh.cryptodatastation.common.model.CdsConstant;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "coin_price")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPrice {
    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "ticker", length = Integer.MAX_VALUE)
    private String ticker;

    @Column(name = "timeframe", length = Integer.MAX_VALUE)
    private String timeframe;

    @Column(name = "candle_begin_datetime", length = Integer.MAX_VALUE)
    private String candleBeginDatetime;

    @Column(name = "candle_end_datetime", length = Integer.MAX_VALUE)
    private String candleEndDatetime;

    @Column(name = "price_open")
    private BigDecimal priceOpen;

    @Column(name = "price_high")
    private BigDecimal priceHigh;

    @Column(name = "price_low")
    private BigDecimal priceLow;

    @Column(name = "price_close")
    private BigDecimal priceClose;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "version")
    private Integer version;

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }
}
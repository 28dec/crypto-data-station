package me.longnh.cryptodatastation.fear_greed_index.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.longnh.cryptodatastation.fear_greed_index.entity.CoinPrice;
import me.longnh.cryptodatastation.fear_greed_index.repository.CoinPriceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class CoinDataController {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final CoinPriceRepository coinPriceRepository;

    @GetMapping(value = "/coin/{ticker}/{utcDateTime}")
    public ResponseEntity<?> getCoinData(@PathVariable(name = "ticker") String ticker, @PathVariable(name = "utcDateTime") String utcDateTime, @RequestParam(name = "limit", defaultValue = "1") String limit) throws IOException, InterruptedException {
        OffsetDateTime givenUtcDateTime = OffsetDateTime.parse(utcDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Binance returns the next candle for the given date time, hence we have to minus 1 here
        OffsetDateTime requestUtcDateTime = givenUtcDateTime.minus(Duration.ofDays(1));

        String url = "https://api.binance.com/api/v3/klines";
        Map<String, String> params = Map.of(
                "symbol", ticker,
                "interval", "1d",
                "limit", limit,
                "startTime", String.valueOf(requestUtcDateTime.toInstant().toEpochMilli())
        );

        String paramString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url + "?" + paramString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug(response.body());
        String responseBody = response.body().substring(1, response.body().length() - 1);

        String[] priceDatas = StringUtils.substringsBetween(responseBody, "[", "]");
        Arrays.asList(priceDatas).forEach(log::debug);

        CoinPrice result = null;

        for (String priceData : priceDatas) {
            String[] dataElement = priceData.split(",");
            OffsetDateTime candleOpen = Instant.ofEpochMilli(Long.parseLong(dataElement[0])).atOffset(ZoneOffset.UTC);
            OffsetDateTime candleClose = Instant.ofEpochMilli(Long.parseLong(dataElement[6])).atOffset(ZoneOffset.UTC);

            if (candleOpen.getDayOfYear() != candleClose.getDayOfYear()) {
                throw new RuntimeException("Unsupported candle, the candle must be the same day currently");
            }

            String candleOpenTime = candleOpen.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String candleCloseTime = candleClose.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            CoinPrice coinPrice = CoinPrice.builder()
                    .ticker(ticker)
                    .candleBeginDatetime(candleOpenTime)
                    .candleEndDatetime(candleCloseTime)
                    .priceOpen(new BigDecimal(StringUtils.substringBetween(dataElement[1], "\"")))
                    .priceHigh(new BigDecimal(StringUtils.substringBetween(dataElement[2], "\"")))
                    .priceLow(new BigDecimal(StringUtils.substringBetween(dataElement[3], "\"")))
                    .priceClose(new BigDecimal(StringUtils.substringBetween(dataElement[4], "\"")))
                    .volume(new BigDecimal(StringUtils.substringBetween(dataElement[5], "\"")))
                    .timeframe("1d")
                    .build();
            coinPriceRepository.save(coinPrice);
            if (result == null || coinPrice.getPriceHigh().compareTo(result.getPriceHigh()) > 0) result = coinPrice;
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/coin/ath/{ticker}")
    public ResponseEntity<?> getAth(@PathVariable(value = "ticker") String ticker,
                                    @RequestParam(value = "utcDateTime", required = false) String utcDateTime)
            throws IOException, InterruptedException {
        if (utcDateTime == null) utcDateTime = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Optional<CoinPrice> optGivenCoinPrice = coinPriceRepository.findFirstByTickerAndCandleBeginDatetime(ticker, utcDateTime);
        if (optGivenCoinPrice.isEmpty()) {
            getCoinData(ticker, OffsetDateTime.parse(utcDateTime)
                    .minus(Duration.ofDays(2999))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), "1000");
            getCoinData(ticker, OffsetDateTime.parse(utcDateTime)
                    .minus(Duration.ofDays(1999))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), "1000");
            getCoinData(ticker, OffsetDateTime.parse(utcDateTime)
                    .minus(Duration.ofDays(999))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), "1000");
        }
        Optional<CoinPrice> optCoinPrice = coinPriceRepository.findAth(ticker, utcDateTime);
        if (optCoinPrice.isPresent()) return ResponseEntity.ok(optCoinPrice.get());
        else return ResponseEntity.badRequest().body("No ATH found");
    }
}

package me.longnh.cryptodatastation.fear_greed_index.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.longnh.cryptodatastation.common.model.BaseResponse;
import me.longnh.cryptodatastation.fear_greed_index.entity.FearAndGreedIndexEntity;
import me.longnh.cryptodatastation.fear_greed_index.model.FearAndGreedIndexProviderResponse;
import me.longnh.cryptodatastation.fear_greed_index.repository.FearAndGreedIndexEntityRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/fear-and-greed-index/")
@RequiredArgsConstructor
public class FearAndGreedIndexController {
    private final FearAndGreedIndexEntityRepository fearAndGreedIndexEntityRepository;

    @GetMapping("/")
    public ResponseEntity<BaseResponse> getFearAndGreedIndex() {
        return ResponseEntity.ok(BaseResponse.builder().data(fearAndGreedIndexEntityRepository.findAll()).build());
    }

    @GetMapping("/{date}")
    public ResponseEntity<BaseResponse> getFearAndGreedIndexByDate(@PathVariable(name="date") String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            log.error("Invalid date param.", e);
            return ResponseEntity.badRequest().body(BaseResponse.builder().errorMessage("Bad param").build());
        }
        FearAndGreedIndexEntity result = fearAndGreedIndexEntityRepository.findFirstByDate(date);
        return ResponseEntity.ok(BaseResponse.builder().data(result).build());
    }

    @Scheduled(cron = "0 2 */1 * * *")
    public void cron() {
        fetchAllFGIFromProvider(100);
    }

    @RequestMapping("/fetch")
    public ResponseEntity<?> fetchAllFGIFromProvider(@RequestParam(name = "limit") Integer limit) {
        if (limit == null) limit = 10;
        log.debug("start to fetch data from provider: limit = {}", limit);
        String url = String.format("https://api.alternative.me/fng/?limit=%s&date_format=cn", limit);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FearAndGreedIndexProviderResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, FearAndGreedIndexProviderResponse.class);
        log.debug(response.toString());
        List<FearAndGreedIndexEntity> fgiEntityList = Objects.requireNonNull(response.getBody()).getData().stream()
                .map(e -> FearAndGreedIndexEntity.builder()
                        .fgiNumber(e.getValue())
                        .fgiClassification(e.getValueClassification())
                        .date(e.getDate())
                        .build())
                .toList();
        fearAndGreedIndexEntityRepository.saveAll(fgiEntityList);
        log.debug("donee");
        return ResponseEntity.ok(response.getBody());
    }
}

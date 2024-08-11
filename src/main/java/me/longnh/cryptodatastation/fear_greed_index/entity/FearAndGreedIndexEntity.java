package me.longnh.cryptodatastation.fear_greed_index.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import me.longnh.cryptodatastation.common.model.CdsConstant;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "fear_and_greed_index")
public class FearAndGreedIndexEntity {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "fgi_number")
    private Integer fgiNumber;

    @Column(name = "fgi_classification", length = Integer.MAX_VALUE)
    private String fgiClassification;

    @Column(name = "date", length = Integer.MAX_VALUE)
    private String date;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_by", length = Integer.MAX_VALUE)
    private String createdBy;

    @Column(name = "created_at", length = Integer.MAX_VALUE)
    private String createdAt;

    @Column(name = "updated_by", length = Integer.MAX_VALUE)
    private String updatedBy;

    @Column(name = "updated_at", length = Integer.MAX_VALUE)
    private String updatedAt;

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch().toString();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now().format(CdsConstant.FORMATTER_UTC_DATETIME);
        }
        if (updatedAt != null) {
            updatedAt = OffsetDateTime.now().format(CdsConstant.FORMATTER_UTC_DATETIME);
        }
    }
}
package com.hana8.hanaro.entity;

import com.hana8.hanaro.common.enums.ProductType;
import com.hana8.hanaro.common.enums.SavingsCycle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SavingsCycle savingsCycle;

    @Column(nullable = false)
    private Integer periodMonths;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maturityRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal terminationRate;

    @Column
    private String imagePath;

    public void update(String name, ProductType type, BigDecimal paymentAmount, SavingsCycle savingsCycle,
                       Integer periodMonths, BigDecimal maturityRate, BigDecimal terminationRate, String imagePath) {
        this.name = name;
        this.type = type;
        this.paymentAmount = paymentAmount;
        this.savingsCycle = savingsCycle;
        this.periodMonths = periodMonths;
        this.maturityRate = maturityRate;
        this.terminationRate = terminationRate;
        this.imagePath = imagePath;
    }
}

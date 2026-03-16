package com.hana8.hanaro.repository;

import com.hana8.hanaro.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

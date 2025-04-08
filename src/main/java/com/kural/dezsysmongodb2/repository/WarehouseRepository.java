package com.kural.dezsysmongodb2.repository;

import com.kural.dezsysmongodb2.model.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    List<Warehouse> findByProductsProductId(String productId);
}


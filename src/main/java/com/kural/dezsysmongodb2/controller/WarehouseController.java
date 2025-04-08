package com.kural.dezsysmongodb2.controller;

import com.kural.dezsysmongodb2.model.Product;
import com.kural.dezsysmongodb2.model.Warehouse;
import com.kural.dezsysmongodb2.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // POST /warehouse: Lagerstandort hinzufügen
    @PostMapping("/warehouse")
    public ResponseEntity<Warehouse> addWarehouse(@RequestBody Warehouse warehouse) {
        return ResponseEntity.ok(warehouseService.addWarehouse(warehouse));
    }

    // GET /warehouse: Alle Lagerstandorte abrufen
    @GetMapping("/warehouse")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    // GET /warehouse/{id}: Lagerstandort nach ID abrufen
    @GetMapping("/warehouse/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable String id) {
        return warehouseService.getWarehouseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /warehouse/{id}: Lagerstandort löschen
    @DeleteMapping("/warehouse/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    // POST /product: Produkt zu Lagerstandort hinzufügen
    @PostMapping("/product/{warehouseId}")
    public ResponseEntity<Warehouse> addProductToWarehouse(
            @PathVariable String warehouseId,
            @RequestBody Product product) {
        return ResponseEntity.ok(warehouseService.addProductToWarehouse(warehouseId, product));
    }

    // GET /product: Alle Produkte abrufen
    @GetMapping("/product")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        return ResponseEntity.ok(warehouseService.getAllProductsWithLocations());
    }

    // GET /product/{id} → Einzelnes Produkt mit Lagerstandorten
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(warehouseService.getProductById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /product/{warehouseId}/{productId}: Produkt aus Lagerstandort löschen
    @DeleteMapping("/product/{warehouseId}/{productId}")
    public ResponseEntity<Void> deleteProductFromWarehouse(
            @PathVariable String warehouseId,
            @PathVariable String productId) {
        warehouseService.deleteProductFromWarehouse(warehouseId, productId);
        return ResponseEntity.noContent().build();
    }
}



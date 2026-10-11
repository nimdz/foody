package com.freshsupply.fms.domain;

import com.freshsupply.fms.exception.InsufficientStockException;
import jakarta.persistence.*;

import java.math.BigDecimal;

/** Rich domain entity: stock rules live here, not in the service layer. */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private int stockQuantity;
    private int reorderLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    protected Product() { }

    private Product(Builder b) {
        this.sku = b.sku;
        this.name = b.name;
        this.category = b.category;
        this.unit = b.unit;
        this.unitPrice = b.unitPrice;
        this.stockQuantity = b.stockQuantity;
        this.reorderLevel = b.reorderLevel;
        this.supplier = b.supplier;
    }

    // ---- behaviour ----

    public void decreaseStock(int quantity) {
        requirePositive(quantity);
        if (quantity > stockQuantity) {
            throw new InsufficientStockException(name, stockQuantity, quantity);
        }
        stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        requirePositive(quantity);
        stockQuantity += quantity;
    }

    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }

    public void update(Builder b) {
        this.sku = b.sku;
        this.name = b.name;
        this.category = b.category;
        this.unit = b.unit;
        this.unitPrice = b.unitPrice;
        this.stockQuantity = b.stockQuantity;
        this.reorderLevel = b.reorderLevel;
        this.supplier = b.supplier;
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    // ---- getters ----

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public String getUnit() { return unit; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public int getReorderLevel() { return reorderLevel; }
    public Supplier getSupplier() { return supplier; }

    // ---- Builder pattern ----

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sku;
        private String name;
        private Category category;
        private String unit;
        private BigDecimal unitPrice;
        private int stockQuantity;
        private int reorderLevel;
        private Supplier supplier;

        private Builder() { }

        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder stockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public Builder reorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }

        public Product build() { return new Product(this); }
    }
}

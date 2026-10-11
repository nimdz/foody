package com.freshsupply.fms.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

/** A line on an order. Name and price are snapshotted so later catalog edits do not alter history. */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    private int quantity;

    protected OrderItem() { }

    OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.productName = product.getName();
        this.unitPrice = product.getUnitPrice();
        this.quantity = quantity;
    }

    void increaseQuantity(int extra) {
        this.quantity += extra;
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}

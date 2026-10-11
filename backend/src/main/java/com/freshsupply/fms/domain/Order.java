package com.freshsupply.fms.domain;

import com.freshsupply.fms.exception.InvalidStatusTransitionException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Order() { }

    private Order(Customer customer) {
        this.customer = customer;
        this.createdAt = LocalDateTime.now();
    }

    /** Static factory: the only way to start a new order. */
    public static Order createFor(Customer customer) {
        return new Order(customer);
    }

    /** Reserves stock and adds a line (merging with an existing line for the same product). */
    public void addItem(Product product, int quantity) {
        product.decreaseStock(quantity);
        items.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.increaseQuantity(quantity),
                        () -> items.add(new OrderItem(this, product, quantity)));
    }

    public BigDecimal calculateSubtotal() {
        return items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void applyDiscount(BigDecimal discount) {
        this.subtotal = calculateSubtotal();
        this.discountAmount = discount;
        this.total = subtotal.subtract(discount);
    }

    /** Moves the order along its lifecycle; cancelling returns reserved stock to inventory. */
    public void transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(status, next);
        }
        if (next == OrderStatus.CANCELLED) {
            items.forEach(i -> i.getProduct().increaseStock(i.getQuantity()));
        }
        this.status = next;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

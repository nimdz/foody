package com.freshsupply.fms.config;

import com.freshsupply.fms.domain.*;
import com.freshsupply.fms.dto.OrderItemRequest;
import com.freshsupply.fms.dto.OrderRequest;
import com.freshsupply.fms.repository.CustomerRepository;
import com.freshsupply.fms.repository.ProductRepository;
import com.freshsupply.fms.repository.SupplierRepository;
import com.freshsupply.fms.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Loads demo data on first start (when the database is empty). Disabled in the "test" profile. */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SupplierRepository suppliers;
    private final ProductRepository products;
    private final CustomerRepository customers;
    private final OrderService orderService;

    public DataSeeder(SupplierRepository suppliers, ProductRepository products,
                      CustomerRepository customers, OrderService orderService) {
        this.suppliers = suppliers;
        this.products = products;
        this.customers = customers;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (products.count() > 0) {
            return;
        }
        Supplier fresh = suppliers.save(new Supplier("Green Valley Farms", "sales@greenvalley.example", "+1 555 0101", "12 Orchard Rd"));
        Supplier ocean = suppliers.save(new Supplier("Blue Ocean Seafood", "orders@blueocean.example", "+1 555 0102", "4 Harbour St"));
        Supplier dairy = suppliers.save(new Supplier("Sunrise Dairy Co.", "hello@sunrisedairy.example", "+1 555 0103", "88 Meadow Ln"));
        Supplier pantry = suppliers.save(new Supplier("Pantry Wholesale", "info@pantrywholesale.example", "+1 555 0104", "200 Industrial Ave"));

        List<Product> catalog = List.of(
                product("PRD-001", "Roma Tomatoes", Category.PRODUCE, "kg", "3.20", 120, 30, fresh),
                product("PRD-002", "Baby Spinach", Category.PRODUCE, "kg", "6.50", 60, 20, fresh),
                product("PRD-003", "Atlantic Salmon Fillet", Category.SEAFOOD, "kg", "18.90", 40, 15, ocean),
                product("PRD-004", "Tiger Prawns", Category.SEAFOOD, "kg", "24.00", 25, 10, ocean),
                product("PRD-005", "Whole Milk", Category.DAIRY, "case", "21.50", 80, 25, dairy),
                product("PRD-006", "Cheddar Cheese Block", Category.DAIRY, "kg", "11.75", 35, 12, dairy),
                product("PRD-007", "Basmati Rice 25kg", Category.DRY_GOODS, "bag", "32.00", 50, 15, pantry),
                product("PRD-008", "Olive Oil 5L", Category.DRY_GOODS, "can", "44.90", 18, 20, pantry), // starts low on stock
                product("PRD-009", "Sourdough Loaves", Category.BAKERY, "dozen", "28.00", 30, 10, pantry),
                product("PRD-010", "Orange Juice 1L", Category.BEVERAGES, "case", "19.60", 45, 15, pantry));
        products.saveAll(catalog);

        Customer bistro = customers.save(new Customer("Harbour Bistro", "chef@harbourbistro.example", "+1 555 0201", "1 Pier Rd", CustomerTier.GOLD));
        customers.save(new Customer("City Cafe", "owner@citycafe.example", "+1 555 0202", "45 Main St", CustomerTier.SILVER));
        customers.save(new Customer("Joe's Diner", "joe@joesdiner.example", "+1 555 0203", "9 Elm St", CustomerTier.STANDARD));

        orderService.placeOrder(new OrderRequest(bistro.getId(), List.of(
                new OrderItemRequest(catalog.get(2).getId(), 5),
                new OrderItemRequest(catalog.get(0).getId(), 10))));

        log.info("Demo data loaded");
    }

    private Product product(String sku, String name, Category category, String unit, String price,
                            int stock, int reorder, Supplier supplier) {
        return Product.builder().sku(sku).name(name).category(category).unit(unit)
                .unitPrice(new BigDecimal(price)).stockQuantity(stock).reorderLevel(reorder)
                .supplier(supplier).build();
    }
}

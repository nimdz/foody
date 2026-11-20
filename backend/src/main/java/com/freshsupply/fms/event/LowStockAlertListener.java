package com.freshsupply.fms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Observer pattern: reacts to low-stock events without the order service knowing about it. */
@Component
public class LowStockAlertListener {

    private static final Logger log = LoggerFactory.getLogger(LowStockAlertListener.class);

    @EventListener
    public void onLowStock(LowStockEvent event) {
        log.warn("LOW STOCK: '{}' has {} left (reorder level {}). Contact the supplier!",
                event.productName(), event.stockQuantity(), event.reorderLevel());
    }
}

package Flash.Sale.Handler.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale")
public class FlashSaleController {

    private final InventoryService inventoryService;

    public FlashSaleController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Attempts a purchase for a specific product.
     * Updated to include userId so we can track who is making the purchase.
     */
    @PostMapping("/purchase/{productId}")
    public String purchase(
            @PathVariable String productId,
            @RequestParam String userId) {
        return inventoryService.attemptPurchase(productId, userId);
    }
}
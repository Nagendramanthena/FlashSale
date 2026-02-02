package Flash.Sale.Handler.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class LoadTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final InventoryService inventoryService;


    public LoadTestController(KafkaTemplate<String, String> kafkaTemplate,InventoryService inventoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/test-load")
    public String triggerLoad(@RequestParam(defaultValue = "100") int count) {
        String productId = "P100";

        for (int i = 0; i < count; i++) {
            String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
            String payload = String.format("{\"productId\":\"%s\", \"userId\":\"%s\", \"timestamp\":%d}",
                    productId, userId, System.currentTimeMillis());

            // Sending to Kafka
            String result = inventoryService.attemptPurchase(productId, userId);
            if(result.equals("OUT_OF_STOCK")){
                return i + "Orders Processed";
            }
            else{
                kafkaTemplate.send("order-topic", userId, payload);
            }
        }

        return "Sent " + count + " orders to Kafka!";
    }
}
package Flash.Sale.Handler.demo;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class InventoryService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // The Lua Script:
    // KEYS[1] is the product key (inventory:P100)
    // Returns 1 if successful, 0 if out of stock, -1 if key doesn't exist
    private static final String LUA_SCRIPT =
            "if redis.call('exists', KEYS[1]) == 1 then " +
                    "  local stock = tonumber(redis.call('get', KEYS[1])) " +
                    "  if stock > 0 then " +
                    "    redis.call('decr', KEYS[1]) " +
                    "    return 1 " +
                    "  else " +
                    "    return 0 " +
                    "  end " +
                    "else " +
                    "  return -1 " +
                    "end";

    public InventoryService(StringRedisTemplate redisTemplate,KafkaTemplate kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;

    }

    public String attemptPurchase(String productId, String userId) {
        String key = "inventory:" + productId;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

        // Execute the script atomically in Redis
        Long result = redisTemplate.execute(script, Collections.singletonList(key));

        if (result != null && result == 1) {
            // Success! Send event to Kafka for async database persistence
            // We send the productId as the key to ensure all orders for the same product
            // go to the same Kafka partition (maintaining order).
            String payload = String.format("{\"productId\":\"%s\", \"userId\":\"%s\", \"timestamp\":%d}",
                    productId, userId, System.currentTimeMillis());

            kafkaTemplate.send("order-topic", productId, payload);

            return "ORDER_PLACED";
        }

        if (result != null && result == 0) return "OUT_OF_STOCK";
        return "PRODUCT_NOT_FOUND";
    }
}
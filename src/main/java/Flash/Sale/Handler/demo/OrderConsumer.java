package Flash.Sale.Handler.demo;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

// 1. THE ENTITY (Database Table Structure)
// Using @Table and @Column to ensure compatibility with Postgres naming conventions
@Entity
@Table(name = "flash_orders")
class OrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

// 2. THE REPOSITORY (Interface to talk to the DB)
@Repository
interface OrderRepository extends JpaRepository<OrderRecord, Long> {}

// 3. THE UPDATED CONSUMER (Writes to DB)
@Service
public class OrderConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void consumeOrder(String message) {
        try {
            // Parse the JSON message from Kafka
            var jsonNode = objectMapper.readTree(message);

            // Create a new database record
            OrderRecord record = new OrderRecord();
            record.setProductId(jsonNode.get("productId").asText());
            record.setUserId(jsonNode.get("userId").asText());
            record.setCreatedAt(LocalDateTime.now());

            // SAVE TO POSTGRES DATABASE
            orderRepository.save(record);

            System.out.println("✅ DATABASE SUCCESS: Order saved to Postgres for User: " + record.getUserId());

        } catch (Exception e) {
            System.err.println("❌ DATABASE ERROR: Failed to save order: " + e.getMessage());

        }
    }
}
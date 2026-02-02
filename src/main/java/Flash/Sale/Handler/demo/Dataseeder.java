package Flash.Sale.Handler.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Dataseeder implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    public Dataseeder(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed product inventory: ProductID "P100" has 50 items
        redisTemplate.opsForValue().set("inventory:P100", "10");
        System.out.println(">>> Redis Seeded: P100 set to 10 items.");
    }
}
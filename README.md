# High-Concurrency Flash Sale Handler

A robust, event-driven flash sale system built with **Java Spring Boot**, **Apache Kafka**, and **Redis**. This project is designed to handle extreme traffic spikes, ensuring data consistency and sub-millisecond response times during high-velocity sales events.

## 🚀 Key Features

* **Distributed Throttling:** Utilizes **Redis** for atomic inventory management and rate limiting to prevent database bottlenecks and system crashes.
* **Event-Driven Architecture:** Leverages **Kafka** for asynchronous order processing, decoupling high-speed request ingestion from backend fulfillment.
* **Concurrency Control:** Implements Lua scripting and distributed locking to guarantee "exactly-once" processing and prevent inventory over-selling.

## 🛠 Tech Stack

* **Backend:** Java, Spring Boot
* **Messaging:** Apache Kafka
* **Caching/Data:** Redis
* **Database:** (Add your DB here, e.g., MySQL/PostgreSQL)

## 🏗 System Architecture

1.  **Request Ingestion:** Users hit the Spring Boot API.
2.  **Stock Validation:** Redis checks and decrements stock atomically.
3.  **Buffering:** Validated orders are pushed to a Kafka topic.
4.  **Persistence:** A consumer service processes the Kafka stream to update the orders

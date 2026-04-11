# Event-Driven Notification System

A scalable backend system built using Spring Boot and Kafka to handle asynchronous notification processing with retry and failure handling mechanisms.

---

## Features

- Event-driven architecture using Apache Kafka
- Retry mechanism using multiple retry topics
- Dead Letter Queue (DLQ) for failed events
- Channel selection with fallback handling
- Idempotency support to prevent duplicate processing
- PostgreSQL integration for persistence
- Asynchronous processing using Kafka consumers

---

## Architecture

Client → REST API → Kafka Producer → Kafka Topic → Consumers → Processing Service → Database

Topics used:
- notifications
- notifications-retry-1
- notifications-retry-2
- notifications-retry-3
- notifications-dlq

---

## Workflow

1. Client sends a notification request via REST API
2. Request is validated and stored in the database
3. Event is published to Kafka
4. Consumer processes the event asynchronously
5. Failed messages are retried via retry topics
6. Messages are sent to DLQ after exhausting retries
7. Final status is updated in the database

---

## Tech Stack

- Java 21
- Spring Boot
- Apache Kafka
- PostgreSQL
- Docker

---

## Running the Application

### Start Kafka

docker run -d
--name kafka
-p 9092:9092
-e KAFKA_PROCESS_ROLES=broker,controller
-e KAFKA_NODE_ID=1
-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093
-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT
-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER
-e CLUSTER_ID=<generate-uuid>
confluentinc/cp-kafka:7.6.1

### Run Application

./mvnw spring-boot:run

---

## Key Learnings

- Designed an event-driven system using Kafka
- Implemented retry and DLQ patterns for fault tolerance
- Handled serialization and deserialization issues in Kafka
- Built idempotent APIs to avoid duplicate processing

---

## Author

Abhilash Jena
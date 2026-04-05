#AI-Powered Notification Service 🚀

## Overview
A scalable backend system that generates dynamic notification messages using AI and processes them asynchronously.

## Features
- AI-generated notification messages (LLM integration)
- Asynchronous processing using @Async
- Retry mechanism with exponential backoff
- Idempotent API handling
- PostgreSQL persistence
- Status lifecycle: PENDING → SENT → FAILED
- Redis-based rate limiting (optional)
- Redis-based rate limiting to prevent API abuse

## Tech Stack
- Java, Spring Boot
- PostgreSQL
- Redis
- OpenAI API

## API Example

POST /api/v1/notifications

```json
{
  "userId": "abhilash",
  "type": "PUSH",
  "payload": "Order shipped"
}
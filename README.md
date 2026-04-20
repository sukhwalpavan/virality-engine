# Virality Engine 🚀

## 📌 Overview
A Spring Boot backend project that simulates a social media virality system using Redis.

## ⚙️ Tech Stack
- Java (Spring Boot)
- PostgreSQL
- Redis
- Maven

## 🔥 Features
- Create Post API
- Add Comment API
- Like Post (Virality Score using Redis)
- Notification System with cooldown
- Scheduled processing using Spring Scheduler

## 🧪 APIs

### Create Post
POST /api/posts

### Add Comment
POST /api/posts/{postId}/comments

### Like Post
POST /api/posts/{postId}/like

## ▶️ How to Run
1. Start PostgreSQL
2. Start Redis (redis-server.exe)
3. Run Spring Boot Application
4. Test APIs using Postman

## 👨‍💻 Author
Pavan Sukhwal

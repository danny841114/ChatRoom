# ChatRoom

使用 Spring Boot + Vue 3 + WebSocket(STOMP) 實作的即時聊天室系統。

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring WebSocket
- Spring Data JPA
- JWT
- H2 Database

### Frontend
- Vue 3
- Pinia
- Axios
- STOMP.js
- SockJS

---

## Features

- JWT Login Authentication
- Real-time Chat
- Private / Group Chat Rooms
- Real-time Room List Update
- Unread Message Count
- Soft Delete Messages
- RESTful API
- WebSocket(STOMP)

---

## WebSocket Topics

### Send Message
```txt
/app/chat.sendMessage/{roomId}
```

### Subscribe Room Messages
```txt
/topic/rooms/{roomId}
```

### Subscribe User Room List
```txt
/topic/users/{userId}/rooms
```

---

## Project Structure

```txt
Frontend(Vue3)
    ↓
REST API + WebSocket
    ↓
Backend(Spring Boot)
    ↓
H2 Database
```

---

## Run Project

### Backend
```bash
./mvnw spring-boot:run
```

### Frontend
```bash
npm install
npm run dev
```

---

## Future Improvements

- Message Pagination
- Image Messages
- Online Status
- Docker Deployment
- Redis Pub/Sub
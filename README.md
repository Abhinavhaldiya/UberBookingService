Uber Booking Service
A Spring Boot microservice responsible for orchestrating the core ride-booking lifecycle.

🚀 Key Features
Ride Requests: Creates passenger bookings and provides real-time status tracking.

Async Driver Matching: Uses Retrofit2 to asynchronously communicate with external Location Services to find nearby drivers without blocking the main thread.

Real-time Notifications: Triggers socket events via UberSocketApi to dispatch ride requests to available drivers.

Thread-Safe Assignment: Utilizes atomic database operations and @Transactional to securely assign a single driver to a booking, preventing race conditions and double-booking.

🛠️ Tech Stack
Language/Framework: Java, Spring Boot

Networking: Retrofit2 (Async REST client)

Data Handling: Spring Data JPA (Passenger, Booking, and Driver repositories)

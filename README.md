# Accommodation Booking Service

## Project Description

This project is a web application for managing accommodation bookings. It provides functionality for managing properties, booking accommodations, handling payments, and user authentication. The backend is powered by Spring Boot and follows RESTful principles to ensure scalable and maintainable architecture.

The main purpose of this project is to showcase my skills in backend development, including the design and implementation of modern web applications using Spring Boot and related technologies.

The Accommodation Booking Service is a platform where users can:
- Browse, search, and view accommodations
- Book accommodations
- Manage their bookings and view booking history

Administrators can:
- Manage the inventory (accommodations)
- Update the status of bookings

JWT-based authentication is implemented for secure user management.
---

## Technologies Used

- **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA
- **Database**: PostgreSQL with Liquibase for migrations
- **Authentication**: JWT tokens
- **Payment Processing**: Stripe API
- **Notifications**: Telegram Bot API
- **Build Tool**: Maven
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, Docker Compose
- **Documentation**: Swagger/OpenAPI

## API Endpoints

### Authentication
- `POST /auth/registration` - Register a new user account
- `POST /auth/login` - Authenticate and receive JWT token

### User Management
- `GET /users/me` - Get current user profile
- `PATCH /users/me` - Update current user profile
- `PUT /users/{id}/roles` - Update user roles (super admin only)

### Accommodation Management
- `GET /accommodations/` - List all accommodations (paginated)
- `GET /accommodations/{id}` - Get specific accommodation details
- `POST /accommodations` - Create new accommodation (admin only)
- `PATCH /accommodations/{id}` - Update accommodation (admin only)
- `DELETE /accommodations/{id}` - Remove accommodation (admin only)

### Booking Management
- `POST /bookings` - Create a new booking
- `GET /bookings/my` - Get current user's bookings
- `GET /bookings/{id}` - Get specific booking details
- `GET /bookings?userId=...&status=...` - Filter bookings by user and status (admin only)
- `PUT /bookings/{id}` - Update booking
- `DELETE /bookings/{id}` - Cancel booking

### Payment Processing
- `POST /payments?bookingId=...` - Create payment session for booking
- `GET /payments/my` - Get current user's payments
- `GET /payments?userId=...` - Get payments by user ID (admin only)
- `GET /payments/all` - Get all payments (admin only)
- `GET /payments/success?sessionId=...` - Handle successful payment
- `GET /payments/cancel?sessionId=...` - Handle canceled payment

### Health Check
- `GET /health` - Check service health status

## Payment Processing

The application integrates with Stripe for secure and reliable payment processing, providing a comprehensive solution for handling financial transactions.

### Stripe Integration

- **Payment Gateway**: Secure processing of credit card and other payment methods
- **Session-Based Checkout**: Redirects users to Stripe Checkout for secure payment information collection
- **Webhook Support**: Processes payment events and status updates in real-time
- **Dashboard Analytics**: All payment information is accessible through the Stripe Dashboard

## Scheduling

The application includes several automated schedulers to handle time-dependent operations:

### Booking Completion Scheduler
- **Schedule**: Daily at 5:00 PM (`0 0 17 * * ?`)
- **Function**: Automatically marks bookings as COMPLETED when their checkout date is today
- **Actions**:
  - Updates booking status to COMPLETED
  - Increments user's completed bookings count
  - Sends notification via Telegram

### Booking Expiration Scheduler
- **Schedule**: Every minute (`0 * * * * ?`)
- **Function**: Expires pending bookings that haven't been paid within 15 minutes
- **Actions**:
  - Updates booking status to EXPIRED
  - Sends expiration notification via Telegram

### Payment Expiration Scheduler
- **Schedule**: Every minute (`0 * * * * ?`)
- **Function**: Handles payment sessions that have been pending for more than 15 minutes
- **Actions**:
  - Expires the Stripe payment session
  - Updates payment status to EXPIRED
  - Updates associated booking status to EXPIRED if still pending
  - Sends notifications for both expired payment and booking

## Loyalty Discount System

The application implements a tiered loyalty discount system using the Strategy pattern to reward returning customers based on their booking history.

### Discount Tiers

| Tier | Completed Bookings | Discount Rate |
|------|-------------------|--------------|
| Bronze | 0-1 | 0% |
| Silver | 2-4 | 5% |
| Gold | 5-9 | 10% |
| Platinum | 10+ | 15% |

### How It Works

1. When creating a booking, the system checks the user's completed booking count
2. The factory selects the appropriate discount strategy based on the tier
3. The strategy calculates the discount amount based on the booking total
4. The final price is adjusted by applying the calculated discount

This system encourages customer loyalty and repeat bookings by providing increasing benefits for frequent users.

## Notifications

The application implements a robust notification system using Telegram to keep administrators informed about important system events in real-time.

### Telegram Channel Integration

- **Admin Channel**: Dedicated Telegram channel "Booking App Notifications" for administrators
- **Bot Integration**: Custom "Booking App Notification Bot" that forwards messages to appropriate topics
- **Topic-Based Organization**: Messages are categorized into specific topics for better organization and filtering

### Notification Topics

- **Bookings**: Alerts about booking status changes (created, confirmed, expired, completed)
- **Payments**: Updates on payment processing (successful, failed, expired)
- **Accommodations**: Notifications about accommodation management (created, updated, deleted)

## Installation and Setup

### Prerequisites
Make sure you have the following technologies installed:
- Java 21
- Maven
- Docker
- Docker Compose
- PostgreSQL

### Cloning the Project
```bash
git clone https://github.com/PavloMelnyk10/booking-app.git
cd booking-app
```

## Environment Configuration

To run the application, you'll need to configure the following environment variables or properties. Create an `application.properties` file with these settings:

### Setup Instructions

1. **Database Setup**

- Create a database named `booking-app` in your PostgreSQL instance.
- Update the `application.properties` and `liquibase.properties` files with your PostgreSQL credentials.

2. **Configure Environment**
- Copy the environment configuration above to `src/main/resources/application.properties`
- Replace sensitive values with your actual credentials:
   - Stripe API keys
   - Telegram bot token and chat ID

### Telegram Notification Setup

1. Create a Telegram bot using BotFather
2. Create a channel and add the bot with admin privileges
3. Create topics in the channel with the following names:
- Bookings
- Payments
- Accommodations
4. Update the configuration with your bot token, channel ID, and topic IDs

### Stripe Setup

1. Create a Stripe account at https://stripe.com
2. Obtain your API keys from the Stripe Dashboard
3. Update the configuration with your Stripe secret and public keys

### Running the Project
1. Build the project using Maven:
   ```bash
   mvn clean install
   ```
2. Start the application:
   ```bash
   mvn spring-boot:run
   ```

### Running with Docker
To run the application using Docker:
1. Copy the sample environment file and update the values as needed:
   ```bash
   cp .env.sample .env
   ```
2. Build the Docker images and start the containers:
   ```bash
   docker-compose up --build
   ```

### Accessing the Application
- The application will be accessible at `http://localhost:8080/api`.
- Swagger UI will be available at `http://localhost:8080/api/swagger-ui.html`.


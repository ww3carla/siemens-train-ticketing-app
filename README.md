# Siemens Java Internship Assignment 2026

This repository contains a Java Spring Boot solution for the Siemens Java Developer Trainee internship assignment.

The project contains two parts:

1. **Problem 1 - Train Ticketing Application**
2. **Problem 2 - Smart Route Optimizer**

The first part implements a train ticketing REST API with predefined stations, trains, routes, schedules, booking functionality, overbooking prevention, route search, administration operations, delay reporting and customer notifications.

The second part implements an optional route optimization module based on Dijkstra's algorithm. It finds the best route between two cities using a selected optimization criterion: duration, cost or distance.

---

## Technology Stack

- Java 21
- Spring Boot
- Gradle
- Spring Web
- Spring Data JPA
- Jakarta Validation
- H2 in-memory database
- JUnit 5
- Mockito
- Swagger / OpenAPI

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.siemens.internship
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       │   ├── request
│   │       │   └── response
│   │       ├── exception
│   │       ├── model
│   │       ├── optimizer
│   │       │   ├── controller
│   │       │   ├── dto
│   │       │   ├── model
│   │       │   └── service
│   │       ├── repository
│   │       └── service
│   │           └── impl
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com.siemens.internship
            ├── optimizer
            └── service
```

---

## Local Application Details

Application URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

H2 Console:

```text
http://localhost:8080/h2-console
```

H2 database connection:

```text
JDBC URL: jdbc:h2:mem:train_ticketing_db
Username: sa
Password:
```

The password is empty.

---

## Build and Test Commands

Build the project:

```bash
./gradlew build
```

Run tests:

```bash
./gradlew test
```

Start the application:

```bash
./gradlew bootRun
```

Windows PowerShell equivalents:

```powershell
.\gradlew build
```

```powershell
.\gradlew test
```

```powershell
.\gradlew bootRun
```

---

# Problem 1 - Train Ticketing Application

## Overview

The train ticketing application supports:

- Predefined train schedules and train routes
- Booking one or multiple tickets
- Preventing overbooking
- Sending booking confirmation notifications
- Finding possible departure and arrival times between two stations
- Direct connections and one-changeover connections
- Admin operations for stations, trains, routes and schedules
- Viewing bookings made for a given train
- Reporting train delays
- Notifying affected customers when a delay is reported

The application uses an H2 in-memory database. Demo data is inserted automatically at startup.

---

## Seed Data

The application starts with predefined stations, trains, routes and schedules.

### Stations

```json
[
  {
    "id": 1,
    "name": "Cluj-Napoca"
  },
  {
    "id": 2,
    "name": "Alba Iulia"
  },
  {
    "id": 3,
    "name": "Sibiu"
  },
  {
    "id": 4,
    "name": "Brasov"
  },
  {
    "id": 5,
    "name": "Bucharest"
  },
  {
    "id": 6,
    "name": "Iasi"
  }
]
```

### Trains

```json
[
  {
    "id": 1,
    "trainNumber": "IR-101",
    "name": "Transylvania Express",
    "capacity": 120
  },
  {
    "id": 2,
    "trainNumber": "R-202",
    "name": "Carpathian Connector",
    "capacity": 80
  },
  {
    "id": 3,
    "trainNumber": "IR-303",
    "name": "Moldova Express",
    "capacity": 100
  }
]
```

### Routes

```json
[
  {
    "id": 1,
    "name": "Cluj-Napoca to Bucharest",
    "stops": [
      {
        "stationId": 1,
        "stationName": "Cluj-Napoca",
        "stopOrder": 1
      },
      {
        "stationId": 2,
        "stationName": "Alba Iulia",
        "stopOrder": 2
      },
      {
        "stationId": 3,
        "stationName": "Sibiu",
        "stopOrder": 3
      },
      {
        "stationId": 4,
        "stationName": "Brasov",
        "stopOrder": 4
      },
      {
        "stationId": 5,
        "stationName": "Bucharest",
        "stopOrder": 5
      }
    ]
  },
  {
    "id": 2,
    "name": "Brasov to Iasi",
    "stops": [
      {
        "stationId": 4,
        "stationName": "Brasov",
        "stopOrder": 1
      },
      {
        "stationId": 5,
        "stationName": "Bucharest",
        "stopOrder": 2
      },
      {
        "stationId": 6,
        "stationName": "Iasi",
        "stopOrder": 3
      }
    ]
  },
  {
    "id": 3,
    "name": "Sibiu to Brasov",
    "stops": [
      {
        "stationId": 3,
        "stationName": "Sibiu",
        "stopOrder": 1
      },
      {
        "stationId": 4,
        "stationName": "Brasov",
        "stopOrder": 2
      }
    ]
  }
]
```

### Schedules

```json
[
  {
    "id": 1,
    "trainId": 1,
    "trainNumber": "IR-101",
    "trainName": "Transylvania Express",
    "routeId": 1,
    "routeName": "Cluj-Napoca to Bucharest",
    "departureTime": "2026-05-01T08:00:00",
    "arrivalTime": "2026-05-01T16:30:00",
    "delayMinutes": 0,
    "effectiveDepartureTime": "2026-05-01T08:00:00",
    "effectiveArrivalTime": "2026-05-01T16:30:00"
  },
  {
    "id": 2,
    "trainId": 2,
    "trainNumber": "R-202",
    "trainName": "Carpathian Connector",
    "routeId": 3,
    "routeName": "Sibiu to Brasov",
    "departureTime": "2026-05-01T12:00:00",
    "arrivalTime": "2026-05-01T14:15:00",
    "delayMinutes": 0,
    "effectiveDepartureTime": "2026-05-01T12:00:00",
    "effectiveArrivalTime": "2026-05-01T14:15:00"
  },
  {
    "id": 3,
    "trainId": 3,
    "trainNumber": "IR-303",
    "trainName": "Moldova Express",
    "routeId": 2,
    "routeName": "Brasov to Iasi",
    "departureTime": "2026-05-01T17:00:00",
    "arrivalTime": "2026-05-01T23:30:00",
    "delayMinutes": 0,
    "effectiveDepartureTime": "2026-05-01T17:00:00",
    "effectiveArrivalTime": "2026-05-01T23:30:00"
  }
]
```

---

## Domain Model

The main domain entities are:

### Station

Represents a train station.

Main fields:

- `id`
- `name`

### Train

Represents a train.

Main fields:

- `id`
- `trainNumber`
- `name`
- `capacity`

### Route

Represents an ordered sequence of stations.

Main fields:

- `id`
- `name`
- `stops`

The `Route` entity contains domain behavior for adding, replacing and validating ordered route stops.

### RouteStop

Represents a station inside a route with a specific order.

Main fields:

- `route`
- `station`
- `stopOrder`

### TrainSchedule

Represents a train assigned to a route for a specific time interval.

Main fields:

- `train`
- `route`
- `departureTime`
- `arrivalTime`
- `delayMinutes`

### Booking

Represents a confirmed customer booking.

Main fields:

- `customerName`
- `customerEmail`
- `schedule`
- `fromStation`
- `toStation`
- `numberOfTickets`
- `status`
- `createdAt`

---

## Booking Flow

The booking flow allows a customer to book one or multiple tickets for a train schedule between two stations.

The application validates that:

- The selected schedule exists
- The departure station exists
- The arrival station exists
- The departure and arrival stations are different
- The selected stations are part of the route
- The departure station appears before the arrival station on the route
- Enough seats are available on the requested travel segment

After a successful booking, the application creates a confirmed booking and sends a simulated confirmation email.

### Endpoint

```http
POST /api/bookings
```

### Input Example

```json
{
  "customerName": "Carla Bozintan",
  "customerEmail": "carla@example.com",
  "scheduleId": 1,
  "fromStationId": 1,
  "toStationId": 5,
  "numberOfTickets": 2
}
```

### Output Example

```json
{
  "bookingId": 1,
  "status": "CONFIRMED",
  "message": "Booking confirmed successfully. A confirmation email has been sent."
}
```

---

## Get Booking by ID

### Endpoint

```http
GET /api/bookings/{bookingId}
```

### Input Example

```text
GET /api/bookings/1
```

### Output Example

```json
{
  "bookingId": 1,
  "customerName": "Carla Bozintan",
  "customerEmail": "carla@example.com",
  "trainNumber": "IR-101",
  "trainName": "Transylvania Express",
  "fromStation": "Cluj-Napoca",
  "toStation": "Bucharest",
  "numberOfTickets": 2,
  "status": "CONFIRMED",
  "departureTime": "2026-05-01T08:00:00",
  "arrivalTime": "2026-05-01T16:30:00",
  "createdAt": "2026-05-01T12:30:00"
}
```

---

## Overbooking Prevention

The application prevents overbooking at route segment level.

This is more accurate than checking only the total number of tickets booked on the train.

For example, given the route:

```text
A -> B -> C -> D
```

A booking from `A` to `C` occupies:

```text
A-B
B-C
```

A booking from `C` to `D` does not overlap with the previous booking and can still be accepted.

Two bookings overlap when:

```text
requestedStart < existingEnd && existingStart < requestedEnd
```

If the requested ticket count exceeds the available seats on the overlapping segment, the booking is rejected.

### Overbooking Input Example

If train capacity is already fully used on a segment, a request like this is rejected:

```json
{
  "customerName": "Second Customer",
  "customerEmail": "second@example.com",
  "scheduleId": 1,
  "fromStationId": 1,
  "toStationId": 5,
  "numberOfTickets": 200
}
```

### Overbooking Output Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Not enough seats available. Requested: 200, available: 120.",
  "details": []
}
```

---

## Route Search

The application can search for possible connections between two stations.

Supported connection types:

- Direct route
- Route with one train changeover

If no valid connection exists, the API returns an appropriate error response.

### Direct Route Endpoint

```http
GET /api/routes/search?fromStationId=1&toStationId=5
```

### Direct Route Output Example

```json
[
  {
    "connectionType": "DIRECT",
    "legs": [
      {
        "trainNumber": "IR-101",
        "trainName": "Transylvania Express",
        "fromStation": "Cluj-Napoca",
        "toStation": "Bucharest",
        "departureTime": "2026-05-01T08:00:00",
        "arrivalTime": "2026-05-01T16:30:00"
      }
    ],
    "departureTime": "2026-05-01T08:00:00",
    "arrivalTime": "2026-05-01T16:30:00",
    "totalTravelMinutes": 510
  }
]
```

### Changeover Route Endpoint

```http
GET /api/routes/search?fromStationId=1&toStationId=6
```

### Changeover Route Output Example

```json
[
  {
    "connectionType": "CHANGEOVER",
    "legs": [
      {
        "trainNumber": "IR-101",
        "trainName": "Transylvania Express",
        "fromStation": "Cluj-Napoca",
        "toStation": "Brasov",
        "departureTime": "2026-05-01T08:00:00",
        "arrivalTime": "2026-05-01T16:30:00"
      },
      {
        "trainNumber": "IR-303",
        "trainName": "Moldova Express",
        "fromStation": "Brasov",
        "toStation": "Iasi",
        "departureTime": "2026-05-01T17:00:00",
        "arrivalTime": "2026-05-01T23:30:00"
      }
    ],
    "departureTime": "2026-05-01T08:00:00",
    "arrivalTime": "2026-05-01T23:30:00",
    "totalTravelMinutes": 930
  }
]
```

### No Route Output Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No possible connection was found between the selected stations.",
  "details": []
}
```

---

# Admin Functionalities

## Admin Stations

### Get All Stations

```http
GET /api/admin/stations
```

### Output Example

```json
[
  {
    "id": 1,
    "name": "Cluj-Napoca"
  },
  {
    "id": 2,
    "name": "Alba Iulia"
  }
]
```

### Get Station by ID

```http
GET /api/admin/stations/1
```

### Output Example

```json
{
  "id": 1,
  "name": "Cluj-Napoca"
}
```

### Create Station

```http
POST /api/admin/stations
```

### Input Example

```json
{
  "name": "Timisoara"
}
```

### Output Example

```json
{
  "id": 7,
  "name": "Timisoara"
}
```

### Update Station

```http
PUT /api/admin/stations/7
```

### Input Example

```json
{
  "name": "Timisoara Nord"
}
```

### Output Example

```json
{
  "id": 7,
  "name": "Timisoara Nord"
}
```

### Delete Station

```http
DELETE /api/admin/stations/7
```

### Output Example

```text
204 No Content
```

---

## Admin Trains

### Get All Trains

```http
GET /api/admin/trains
```

### Output Example

```json
[
  {
    "id": 1,
    "trainNumber": "IR-101",
    "name": "Transylvania Express",
    "capacity": 120
  },
  {
    "id": 2,
    "trainNumber": "R-202",
    "name": "Carpathian Connector",
    "capacity": 80
  }
]
```

### Get Train by ID

```http
GET /api/admin/trains/1
```

### Output Example

```json
{
  "id": 1,
  "trainNumber": "IR-101",
  "name": "Transylvania Express",
  "capacity": 120
}
```

### Create Train

```http
POST /api/admin/trains
```

### Input Example

```json
{
  "trainNumber": "IR-999",
  "name": "Test Express",
  "capacity": 90
}
```

### Output Example

```json
{
  "id": 4,
  "trainNumber": "IR-999",
  "name": "Test Express",
  "capacity": 90
}
```

### Update Train

```http
PUT /api/admin/trains/4
```

### Input Example

```json
{
  "trainNumber": "IR-999",
  "name": "Updated Test Express",
  "capacity": 100
}
```

### Output Example

```json
{
  "id": 4,
  "trainNumber": "IR-999",
  "name": "Updated Test Express",
  "capacity": 100
}
```

### Delete Train

```http
DELETE /api/admin/trains/4
```

### Output Example

```text
204 No Content
```

---

## Admin Routes

### Get All Routes

```http
GET /api/admin/routes
```

### Output Example

```json
[
  {
    "id": 1,
    "name": "Cluj-Napoca to Bucharest",
    "stops": [
      {
        "stationId": 1,
        "stationName": "Cluj-Napoca",
        "stopOrder": 1
      },
      {
        "stationId": 2,
        "stationName": "Alba Iulia",
        "stopOrder": 2
      }
    ]
  }
]
```

### Get Route by ID

```http
GET /api/admin/routes/1
```

### Output Example

```json
{
  "id": 1,
  "name": "Cluj-Napoca to Bucharest",
  "stops": [
    {
      "stationId": 1,
      "stationName": "Cluj-Napoca",
      "stopOrder": 1
    },
    {
      "stationId": 2,
      "stationName": "Alba Iulia",
      "stopOrder": 2
    },
    {
      "stationId": 3,
      "stationName": "Sibiu",
      "stopOrder": 3
    },
    {
      "stationId": 4,
      "stationName": "Brasov",
      "stopOrder": 4
    },
    {
      "stationId": 5,
      "stationName": "Bucharest",
      "stopOrder": 5
    }
  ]
}
```

### Create Route

```http
POST /api/admin/routes
```

### Input Example

```json
{
  "name": "Cluj-Napoca to Iasi",
  "stationIds": [1, 4, 6]
}
```

### Output Example

```json
{
  "id": 4,
  "name": "Cluj-Napoca to Iasi",
  "stops": [
    {
      "stationId": 1,
      "stationName": "Cluj-Napoca",
      "stopOrder": 1
    },
    {
      "stationId": 4,
      "stationName": "Brasov",
      "stopOrder": 2
    },
    {
      "stationId": 6,
      "stationName": "Iasi",
      "stopOrder": 3
    }
  ]
}
```

### Update Route

```http
PUT /api/admin/routes/4
```

### Input Example

```json
{
  "name": "Updated Sibiu to Iasi",
  "stationIds": [3, 4, 6]
}
```

### Output Example

```json
{
  "id": 4,
  "name": "Updated Sibiu to Iasi",
  "stops": [
    {
      "stationId": 3,
      "stationName": "Sibiu",
      "stopOrder": 1
    },
    {
      "stationId": 4,
      "stationName": "Brasov",
      "stopOrder": 2
    },
    {
      "stationId": 6,
      "stationName": "Iasi",
      "stopOrder": 3
    }
  ]
}
```

### Delete Route

```http
DELETE /api/admin/routes/4
```

### Output Example

```text
204 No Content
```

### Invalid Route Example

```json
{
  "name": "Invalid Route",
  "stationIds": [1, 1]
}
```

### Invalid Route Output Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "A route cannot contain duplicate stations.",
  "details": []
}
```

---

## Admin Schedules

### Get All Schedules

```http
GET /api/admin/schedules
```

### Output Example

```json
[
  {
    "id": 1,
    "trainId": 1,
    "trainNumber": "IR-101",
    "trainName": "Transylvania Express",
    "routeId": 1,
    "routeName": "Cluj-Napoca to Bucharest",
    "departureTime": "2026-05-01T08:00:00",
    "arrivalTime": "2026-05-01T16:30:00",
    "delayMinutes": 0,
    "effectiveDepartureTime": "2026-05-01T08:00:00",
    "effectiveArrivalTime": "2026-05-01T16:30:00"
  }
]
```

### Get Schedule by ID

```http
GET /api/admin/schedules/1
```

### Output Example

```json
{
  "id": 1,
  "trainId": 1,
  "trainNumber": "IR-101",
  "trainName": "Transylvania Express",
  "routeId": 1,
  "routeName": "Cluj-Napoca to Bucharest",
  "departureTime": "2026-05-01T08:00:00",
  "arrivalTime": "2026-05-01T16:30:00",
  "delayMinutes": 0,
  "effectiveDepartureTime": "2026-05-01T08:00:00",
  "effectiveArrivalTime": "2026-05-01T16:30:00"
}
```

### Create Schedule

```http
POST /api/admin/schedules
```

### Input Example

```json
{
  "trainId": 1,
  "routeId": 1,
  "departureTime": "2026-05-02T08:00:00",
  "arrivalTime": "2026-05-02T16:30:00"
}
```

### Output Example

```json
{
  "id": 4,
  "trainId": 1,
  "trainNumber": "IR-101",
  "trainName": "Transylvania Express",
  "routeId": 1,
  "routeName": "Cluj-Napoca to Bucharest",
  "departureTime": "2026-05-02T08:00:00",
  "arrivalTime": "2026-05-02T16:30:00",
  "delayMinutes": 0,
  "effectiveDepartureTime": "2026-05-02T08:00:00",
  "effectiveArrivalTime": "2026-05-02T16:30:00"
}
```

### Update Schedule

```http
PUT /api/admin/schedules/4
```

### Input Example

```json
{
  "trainId": 1,
  "routeId": 1,
  "departureTime": "2026-05-02T09:00:00",
  "arrivalTime": "2026-05-02T17:30:00"
}
```

### Output Example

```json
{
  "id": 4,
  "trainId": 1,
  "trainNumber": "IR-101",
  "trainName": "Transylvania Express",
  "routeId": 1,
  "routeName": "Cluj-Napoca to Bucharest",
  "departureTime": "2026-05-02T09:00:00",
  "arrivalTime": "2026-05-02T17:30:00",
  "delayMinutes": 0,
  "effectiveDepartureTime": "2026-05-02T09:00:00",
  "effectiveArrivalTime": "2026-05-02T17:30:00"
}
```

### Delete Schedule

```http
DELETE /api/admin/schedules/4
```

### Output Example

```text
204 No Content
```

### Invalid Schedule Example

```json
{
  "trainId": 1,
  "routeId": 1,
  "departureTime": "2026-05-02T17:30:00",
  "arrivalTime": "2026-05-02T09:00:00"
}
```

### Invalid Schedule Output Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Arrival time must be after departure time.",
  "details": []
}
```

---

## View Bookings for a Train

Admins can view confirmed bookings for a specific train.

### Endpoint

```http
GET /api/admin/trains/{trainId}/bookings
```

### Input Example

```text
GET /api/admin/trains/1/bookings
```

### Output Example

```json
[
  {
    "bookingId": 1,
    "customerName": "Carla Bozintan",
    "customerEmail": "carla@example.com",
    "trainNumber": "IR-101",
    "trainName": "Transylvania Express",
    "fromStation": "Cluj-Napoca",
    "toStation": "Bucharest",
    "numberOfTickets": 2,
    "status": "CONFIRMED",
    "departureTime": "2026-05-01T08:00:00",
    "arrivalTime": "2026-05-01T16:30:00",
    "createdAt": "2026-05-01T12:30:00"
  }
]
```

---

## Delay Reporting

Delays are reported at schedule level.

This avoids incorrectly marking all journeys of the same train as delayed.

### Endpoint

```http
POST /api/admin/schedules/{scheduleId}/delay
```

### Input Example

```json
{
  "delayMinutes": 35,
  "reason": "Technical issue"
}
```

### Output Example

```json
{
  "scheduleId": 1,
  "trainNumber": "IR-101",
  "delayMinutes": 35,
  "reason": "Technical issue",
  "notifiedCustomers": 1,
  "message": "Delay registered successfully. Affected customers have been notified."
}
```

After reporting the delay, the schedule effective times are updated:

```json
{
  "id": 1,
  "trainId": 1,
  "trainNumber": "IR-101",
  "trainName": "Transylvania Express",
  "routeId": 1,
  "routeName": "Cluj-Napoca to Bucharest",
  "departureTime": "2026-05-01T08:00:00",
  "arrivalTime": "2026-05-01T16:30:00",
  "delayMinutes": 35,
  "effectiveDepartureTime": "2026-05-01T08:35:00",
  "effectiveArrivalTime": "2026-05-01T17:05:00"
}
```

---

## Email Notifications

The application contains an `EmailService` abstraction.

For this assignment, email sending is simulated through logging. This avoids requiring real SMTP credentials while keeping the design extensible.

Implemented notification scenarios:

- Booking confirmation
- Delay notification

The current logging implementation can be replaced with a real SMTP implementation without changing the booking or delay business logic.

---

## Error Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

Error responses contain:

- Timestamp
- HTTP status code
- Error name
- Message
- Optional validation details

### Validation Error Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed.",
  "details": [
    "customerEmail: Customer email must be valid.",
    "numberOfTickets: At least one ticket must be booked."
  ]
}
```

### Resource Not Found Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Schedule with id 999 was not found.",
  "details": []
}
```

---

# Problem 2 - Smart Route Optimizer

## Overview

The optional second problem implements a Smart Route Optimizer.

The optimizer solves the following problem:

Given a predefined network of cities connected by travel segments, find the best route between two cities based on a selected optimization criterion.

Supported optimization criteria:

- Shortest duration
- Lowest cost
- Shortest distance

The optimizer is implemented in a separate package:

```text
com.siemens.internship.optimizer
```

---

## Algorithm

The optimizer uses Dijkstra's algorithm.

Each city is represented as a graph node.

Each travel connection is represented as a weighted graph edge.

The edge weight depends on the selected optimization criterion:

- `DURATION` uses travel duration in minutes
- `COST` uses travel cost
- `DISTANCE` uses distance in kilometers

The graph is built from predefined in-memory travel connections.

The graph supports bidirectional travel.

---

## Optimizer Endpoint

### Endpoint

```http
POST /api/optimizer/routes
```

### Input Example - Optimize by Duration

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "DURATION"
}
```

### Output Example - Optimize by Duration

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "DURATION",
  "path": [
    "Cluj-Napoca",
    "Brasov",
    "Iasi"
  ],
  "totalDurationMinutes": 570,
  "totalCost": 230,
  "totalDistanceKm": 590
}
```

### Input Example - Optimize by Cost

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "COST"
}
```

### Output Example - Optimize by Cost

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "COST",
  "path": [
    "Cluj-Napoca",
    "Alba Iulia",
    "Brasov",
    "Iasi"
  ],
  "totalDurationMinutes": 625,
  "totalCost": 228,
  "totalDistanceKm": 630
}
```

### Input Example - Optimize by Distance

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "DISTANCE"
}
```

### Output Example - Optimize by Distance

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "DISTANCE",
  "path": [
    "Cluj-Napoca",
    "Brasov",
    "Iasi"
  ],
  "totalDurationMinutes": 570,
  "totalCost": 230,
  "totalDistanceKm": 590
}
```

### Case-Insensitive Input Example

```json
{
  "from": "cluj-napoca",
  "to": "iasi",
  "criterion": "DURATION"
}
```

### Output Example

```json
{
  "from": "Cluj-Napoca",
  "to": "Iasi",
  "criterion": "DURATION",
  "path": [
    "Cluj-Napoca",
    "Brasov",
    "Iasi"
  ],
  "totalDurationMinutes": 570,
  "totalCost": 230,
  "totalDistanceKm": 590
}
```

### Invalid City Input Example

```json
{
  "from": "Cluj-Napoca",
  "to": "Unknown City",
  "criterion": "DURATION"
}
```

### Invalid City Output Example

```json
{
  "timestamp": "2026-05-01T12:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "City Unknown City does not exist in the optimizer network.",
  "details": []
}
```

---

# Testing

The project includes unit tests for the most important business logic.

Implemented test areas:

- Booking availability validation
- Overbooking prevention
- Booking creation flow
- Email confirmation trigger
- Direct route search
- Changeover route search
- Delay reporting
- Delay customer notification
- Smart route optimization by duration
- Smart route optimization by cost
- Smart route optimization by distance
- Optimizer validation scenarios

Test classes:

```text
AvailabilityServiceTest
BookingServiceTest
RouteSearchServiceTest
AdminScheduleServiceTest
RouteOptimizerServiceTest
```

---

## Test Coverage Details

### AvailabilityServiceTest

Covers:

- Booking succeeds when enough seats are available
- Booking fails when requested tickets exceed available seats
- Non-overlapping bookings are allowed
- Invalid travel direction is rejected

### BookingServiceTest

Covers:

- Booking is saved successfully
- Availability validation is called
- Confirmation email is sent
- Missing schedule is handled correctly

### RouteSearchServiceTest

Covers:

- Direct route is found
- Changeover route is found
- Missing connection is rejected
- Same departure and arrival station is rejected

### AdminScheduleServiceTest

Covers:

- Delay is applied to the schedule
- Affected customers are retrieved
- Delay notifications are sent
- Delay reporting works when no customers are affected

### RouteOptimizerServiceTest

Covers:

- Route optimized by duration
- Route optimized by cost
- Route optimized by distance
- Case-insensitive city matching
- Unknown city validation
- Same origin and destination validation

---

# Design Decisions

## H2 In-Memory Database

The train ticketing application uses an H2 in-memory database.

This allows the application to run without external database setup.

Demo data is inserted automatically at startup.

## DTO-Based API

The API does not expose JPA entities directly.

Request and response DTOs are used to keep the external API contract separated from the persistence model.

## Domain Behavior Inside Entities

Some entities contain domain behavior.

For example, `Route` manages its own stops and validates route consistency.

This avoids an anemic domain model and keeps route-specific rules close to the route entity.

## Centralized Error Handling

The application uses a global exception handler to keep controllers focused on HTTP request handling.

Business and validation errors are converted into consistent API responses.

## Segment-Based Seat Availability

Seat availability is calculated using route segment overlap.

This allows more accurate capacity management than simply counting all bookings on a train.

## Email Service Abstraction

Email sending is abstracted behind an email service.

The current implementation logs email messages, but the design can be extended to use a real email provider.

## Schedule-Level Delay Reporting

Delays are reported at schedule level instead of train level.

This avoids incorrectly marking all occurrences of the same train as delayed.

## Separate Optimizer Module

The optional second problem is implemented under a separate `optimizer` package.

This keeps the optimizer independent from the ticketing domain while still allowing both problems to be delivered as one runnable application.

---

# Current Limitations

- Authentication and authorization are not implemented.
- Email notifications are simulated through logging.
- The H2 database is in-memory, so data is reset after application restart.
- The optimizer uses a predefined in-memory graph.
- Route search supports direct connections and one changeover.
- Advanced seat assignment is not implemented.
- The system validates ticket capacity but does not allocate specific seat numbers.
- The application is designed for assignment evaluation, not production deployment.

---

# Conclusion

This project implements the required train ticketing functionality and includes an optional smart route optimization module.

The solution focuses on:

- Clean package structure
- Domain modeling
- DTO-based API design
- Validation
- Centralized error handling
- Segment-based overbooking prevention
- Admin operations
- Delay notifications
- Algorithmic problem solving
- Unit test coverage

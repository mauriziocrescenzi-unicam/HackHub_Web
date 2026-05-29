# HackHub 🚀

> A full-stack web platform for managing the complete lifecycle of hackathons — from team registration to winner declaration.

---

## 📖 Project Overview

**HackHub** is a web application designed to fully digitalize the management of hackathons — time-limited competitions where teams develop and deliver a project to achieve a common goal. The platform centralizes every phase of the event, from participant registration to winner proclamation.

Built with a **Java Spring Boot** backend and an **Angular 21** frontend with PWA support, HackHub provides a role-based environment where organizers can create and manage events, teams can register and submit their projects, judges can evaluate submissions, and mentors can support participants throughout the competition.

---

## ✨ Features

- 🏆 Create and manage hackathons with a full 4-state lifecycle
- 👥 Team creation, invitations, and membership management
- 📁 Project submission via GitHub repository link, updatable until deadline
- ⭐ Judge evaluation system with numerical score (0–10) and written feedback
- 🚩 Mentor flagging of teams for rule violations
- 🔒 Role-based access control (Visitor, User, Staff, Admin)
- 🔐 JWT-based authentication with role claims
- 🛡️ Angular route guards for frontend protection
- 📱 Progressive Web App (PWA) support — installable on any device
- 🔍 Advanced filtering, sorting, and pagination for hackathon and team discovery

---

## 🛠️ Tech Stack

### Backend
| Technology | Details |
|---|---|
| Java | 21 |
| Spring Boot | 4 |
| Spring Web | REST API with `@RestController` |
| Spring Security | JWT authentication + RBAC |
| Spring Data JPA | Repository-based persistence (Hibernate) |
| H2 Database | In-memory relational database |
| Apache Maven | Build tool and dependency management |

### Frontend
| Technology | Details |
|---|---|
| Angular | 21 |
| TypeScript | Statically typed JavaScript |
| SCSS | Advanced CSS with variables, mixins, nesting |
| Bootstrap 5 | Responsive grid layout and UI components |
| @angular/pwa | Service Worker + Web App Manifest |

---

## 👤 Roles & Actors

| Role | Description |
|---|---|
| **Visitor** | Unauthenticated user, can browse public hackathon info |
| **User** | Registered user, can create or join a team |
| **Staff** | Personnel assigned to specific hackathons as Organizers, Judges, or Mentors |
| **Team Leader** |	Team creator who manages membership, event registration, and invites|
| **Team Member** | Can register their team in a hackathon and submit a project |
| **Mentor** | Staff member who supports teams, can flag rule violations |
| **Judge** | Staff member who evaluates submissions with a score (0–10) and feedback |
| **Organizer** | Staff member who creates hackathons and declares the winner |

---


## 📁 Project Structure

```
HackHubWeb/
│
├── hackhub_backend/                          # Spring Boot REST API
│   ├── pom.xml
│   └── src/main/java/it/unicam/cs/hackhub/
│       ├── HackhubApplication.java           # Entry point
│       ├── client/                           # External integrations
│       ├── controller/                       # REST endpoints
│       ├── exception/                        # Exception handler
│       ├── DTO/                              # Data Transfer Objects
│       ├── model/                            # JPA Entities
│       ├── repository/                       # Spring Data repositories
│       ├── service/                          # Business logic
│       └── security/                         # JWT & Spring Security config
│
└── hackhub_frontend/                         # Angular PWA
    └── src/
        ├── app/
        │   ├── core/                         # core
        │   │   ├── guard/                    # Route guards
        │   │   └── interceptor/              # HTTP interceptors
        │   │
        │   └── features/                     # Feature 
        │      ├── auth/                      # Authentication
        │      ├── dashboard/                 # Staff dashboard
        │      ├── hackathons/                # Hackathon management
        │      ├── reports/                   # Report management
        │      ├── submissions/               # Submission management
        │      ├── teams/                     # Team management
        │      └── users/                     # User profile
        ├── assets/
        │   └── img/                          # Icons and images
        └── environments/
            └── environment.ts

```

## 🚀 Getting Started

### Prerequisites

- **Java** 21+
- **Maven** 3.8+
- **Node.js** 18+
- **npm** 9+
- **Angular CLI** 21+

---

### Backend Setup

```bash
# Navigate to the backend folder
cd hackhub_backend

# Build the project
.\mvnw clean install

# Run the application
.\mvnw spring-boot:run
```

The backend starts at: `http://localhost:8080`

> Default datasource credentials are configured in `src/main/resources/application.properties`.

---

### Frontend Setup

```bash
# Navigate to the frontend folder
cd hackhub_frontend

# Install dependencies
npm install

# Start the development server
ng serve --open
```

The app will be available at: `http://localhost:4200`

#### Build & Run as PWA (Production)

```bash
# Navigate to the frontend folder
cd hackhub_frontend

# Build
ng build

# Serve the production build
ng serve --configuration=production
```

> The Service Worker (PWA) is only active in the production build. Use `ng serve` during development.

---

## 🔐 Security

### JWT Authentication

On login, the backend issues a signed JWT containing the user's identity and static role as a custom claim. This token is attached to every subsequent HTTP request in the `Authorization` header (`Bearer <token>`), enabling stateless authentication without server-side session management.

### Role-Based Access Control (RBAC)

Every REST endpoint is protected by Spring Security annotations that define which roles are authorized to access it:

- `USER` — basic team and participation features
- `STAFF` — hackathon management for assigned events
- `ADMIN` — full platform access

### Frontend Protection

Angular route guards prevent navigation to restricted pages for unauthenticated users or users with insufficient roles. Backend authorization remains the authoritative security layer — the API rejects any request with an invalid token or insufficient permissions with `401 Unauthorized` or `403 Forbidden`.

---

## 📝 License

This project is licensed under the [MIT License](LICENSE).

---

## 👨‍💻 Authors

- **Coacci Victoria**: [@vichy1004](https://github.com/vichy1004)
- **Fattori Filippo**: [@Fil-24](https://github.com/Fil-24)
- **Pigliapoco Simone**: [@simonepigliapoco-gif](https://github.com/simonepigliapoco-gif)

Developed as a university project at **Università di Camerino (UNICAM)**.

---

> HackHub — Where ideas compete.

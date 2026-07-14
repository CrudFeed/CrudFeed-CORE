<table align="center">
  <tr>
    <td align="center">
      <img width="100" height="100" alt="Image" src="https://github.com/user-attachments/assets/e40a109d-1ff5-488f-a6f1-423762d0e0bc" /><br>
      <sub><b>CrudFeed Core</b></sub>
    </td>
  </tr>
</table>

<br>

<p align="center">
  Shared domain layer and foundation for all CrudFeed versions.<br>
  Entities, repositories, and services — the backbone of the ecosystem.
</p>

<br>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Active_Development-blue?style=for-the-badge&logo=git&logoColor=white" alt="Status">
  <img src="https://img.shields.io/badge/Type-Core_Library-brightgreen?style=for-the-badge&logo=git&logoColor=white" alt="Type">
</p>
<p align="center">
  <sub>🔗 Used by <a href="https://github.com/CrudFeed/CrudFeed-CLI">CLI</a> and <a href="https://github.com/CrudFeed/CrudFeed-WEB">Web</a> versions.</sub>
</p>

<br>

<p align="center">
  <font size="7"><b>Powered by</b></font>
</p>

<p align="center">
  <img width="40" src="https://img.shields.io/badge/%20-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java">
  <img width="40" src="https://img.shields.io/badge/%20-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img width="40" src="https://img.shields.io/badge/%20-59666C?style=flat-square&logo=hibernate&logoColor=white" alt="Hibernate">
  <img width="40" src="https://img.shields.io/badge/%20-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven">
  <img width="40" src="https://img.shields.io/badge/%20-000000?style=flat-square&logo=intellijidea&logoColor=white" alt="IDEA">
  <img width="40" src="https://img.shields.io/badge/%20-FCC624?style=flat-square&logo=linux&logoColor=black" alt="Linux">
</p>

---

## 🧱 What's Inside

The core module provides:

- **Entities** — JPA-mapped domain objects (User, Post, Message, Session) with Lombok
- **Repositories** — Hibernate-backed data access layer
- **Services** — Business logic for authentication, users, posts, and messaging
- **Utilities** — Hibernate configuration and password hashing

All standardized, no shortcuts. Single source of truth for the domain.

```
 ┌────────────────────────────────┐
 │            Services            │ ← Business logic (Auth, Users, Posts)
 └───────────────┬────────────────┘
                 ▼
 ┌────────────────────────────────┐
 │          Repositories          │ ← Hibernate queries & sessions
 └───────────────┬────────────────┘     ▲
                 ▼                      │
           ┌────────────┐               │  ┌───────────────┐
           │ PostgreSQL │               └──┤   Utilities   │ (HibernateUtil, etc.)
           └────────────┘                  └───────────────┘
```

---

## 📦 Getting Started

### Add to your project

If you're building a CLI or Web client:

```bash
git clone https://github.com/CrudFeed/CrudFeed-CORE.git
cd CrudFeed-CORE
mvn clean install
```

Then in your client's `pom.xml`:

```xml
<dependency>
  <groupId>org.crudfeed</groupId>
  <artifactId>core</artifactId>
  <version>2.0</version>
</dependency>
```

### Configure Hibernate

Update `src/main/resources/hibernate.cfg.xml` with your PostgreSQL credentials:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/crudfeed</property>
<property name="hibernate.connection.username">your_user</property>
<property name="hibernate.connection.password">your_password</property>
<property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
```

Build and install:

```bash
mvn clean install
```

---

## 🏗️ Module Structure
```
src/main/java/com/crudfeed/
├── entity/           ← JPA entities
├── repository/       ← Repositories
├── service/          ← Business logic
└── util/             ← Validators, formatters, common helpers
```

---

## 💡 Design Principles

- **No Leaks** — Entities never escape the core directly; clients use services
- **Single Responsibility** — Each layer has one job
- **Testability** — Repositories are mockable, services are testable
- **Reusability** — One implementation, multiple consumers (CLI + Web + future versions)

---

## 🚧 Status

Active development. Used as the foundation for all CrudFeed clients.

---

## 📄 License

Same as parent CrudFeed organization.

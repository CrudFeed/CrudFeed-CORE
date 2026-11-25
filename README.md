<p align="center">
  <img width="120" height="120" alt="CrudFeed" src="https://github.com/user-attachments/assets/e40a109d-1ff5-488f-a6f1-423762d0e0bc">
</p>

<h1 align="center">CrudFeed CORE</h1>

<p align="center">
  Shared domain layer and foundation for all CrudFeed versions.<br>
</p>

<br>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Active_Development-blue?style=for-the-badge&logo=git&logoColor=white" alt="Status">
  <img src="https://img.shields.io/badge/Type-Core_Library-brightgreen?style=for-the-badge&logo=git&logoColor=white" alt="Type">
</p>

<br>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white" alt="Hibernate">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white" alt="IntelliJ IDEA">
  <img src="https://img.shields.io/badge/Linux-FCC624?style=flat-square&logo=linux&logoColor=black" alt="Linux">
</p>

---

## 🧱 What's Inside

The core module provides:

- **Entities & Models** — JPA-mapped entities and domain enums/flags with Lombok
- **DTOs** — Request and response data transfer objects across account, message, moderation, and post domains
- **Repositories** — Hibernate-backed data access layer (`Message`, `Mod`, `Post`, `User`)
- **Services** — Business logic for authentication, messages, moderation, posts, and users
- **Utilities** — Hibernate session helper (`HibernateUtil`) and password hashing (`PasswordUtil`)

All standardized, no shortcuts. Single source of truth for the domain.

```
 ┌──────────────────────────────────────────────┐
 │                   Services                   │ ← Auth, Message, Mod, Post, User
 └──────────────────────┬───────────────────────┘
                        ▼
 ┌──────────────────────────────────────────────┐
 │                 Repositories                 │ ← Message, Mod, Post, User
 └──────────────────────┬───────────────────────┘
                        ▼
 ┌──────────────────────────────────────────────┐
 │             Entities / Models / DTOs         │ ← Message, Post, Role, User, FriendFlags
 └──────────────────────┬───────────────────────┘     ▲
                        ▼                             │  ┌──────────────────────┐
                  ┌────────────┐                      └──┤      Utilities       │
                  │ PostgreSQL │                         │ (HibernateUtil,      │
                  └────────────┘                         │  PasswordUtil)       │
                                                         └──────────────────────┘
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
  <version>3.0</version>
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
src/main/java/org/crudfeed/core/
├── dto/              ← Request & response DTOs (account, message, mod, post)
├── entity/           ← JPA entities (Message, Post, Role, User)
├── model/            ← Domain models & flags (FriendFlags)
├── repository/       ← Data access repositories
├── service/          ← Business logic services
└── util/             ← Utilities (HibernateUtil, PasswordUtil)
```

---

## 💡 Design Principles

- **Single Responsibility** — Each layer has one job
- **Testability** — Repositories are mockable, services are testable
- **Reusability** — One implementation, multiple consumers

---

## 🚧 Status

Active development. Used as the foundation for all CrudFeed clients.

---

## 📄 License

This project is licensed under the GNU General Public License v3.0 — see the [LICENSE](LICENSE) file for details.

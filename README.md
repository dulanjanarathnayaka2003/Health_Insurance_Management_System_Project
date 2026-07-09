# Lanka Health Insurance - HealthInsureOnline
<img width="1916" height="926" alt="image" src="https://github.com/user-attachments/assets/347c8139-0abc-4779-abc5-117b6ba2eafd" />





Welcome to **Lanka Health Insurance Online** – a comprehensive web application for managing health insurance policies, claims, and customer services, built with modern Java technologies and a secure, user-friendly frontend.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Available Scripts & Useful Commands](#available-scripts--useful-commands)
- [Deployment](#deployment)
- [Configuration](#configuration)
- [Design Patterns](#design-patterns)
- [Security & Best Practices](#security--best-practices)
- [Contributing](#contributing)
- [Contact & Support](#contact--support)
- [License](#license)

---

## Project Overview

**Lanka Health Insurance Online** is a full-stack web system for insurance companies to:
- Digitally enroll customers
- Manage insurance policies
- Submit and process claims
- Interact with support staff and admins through role-based portals

This platform delivers seamless experience to both company personnel and policyholders, promoting operational efficiency, data integrity, and privacy.

---

## Features

- **Multi-role Access:** Policyholders, Admins, HR Managers, Marketing Managers, and Customer Support.
- **Customer Portal:** View policy, submit & track claims, make payments, update information, view documents.
- **Admin Dashboard:** User and policy management, advanced analytics, reporting.
- **Claims Processing:** Automated workflow for rapid validation and processing of insurance claims.
- **Customer Support:** Dedicated interface for customer inquiries and resolutions.
- **Secure Authentication & Authorization:** Role-based access controls.
- **Email Notifications:** For policy updates, claims, reminders.
- **Mobile-Friendly & Responsive UI:** Tailwind CSS powered, modern look-and-feel.
- **Comprehensive Privacy Policy:** In compliance with international data standards.

---

## Architecture

- **Backend:**  
  Java 21, Jakarta EE, Spring Boot (Data JPA, MVC)
- **Database:**  
  Relational database (schema in `health_ins_db.sql`)
- **Frontend:**  
  HTML5, Tailwind CSS, JavaScript, responsive static pages in `src/main/resources/static`
- **Persistence:**  
  JPA Entity models and repositories
- **Security:**  
  Spring Security, JWT, encrypted credentials
- **Utilities:**  
  Lombok for cleaner Java code, modular utility classes

---

## Technology Stack

- **Java 21**
- **Spring Boot** (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`)
- **Jakarta EE** (Servlet API, annotations via `jakarta` imports)
- **Lombok** (annotations for getters/setters/constructors)
- **H2/MySQL/etc.** (configure in `application.properties`)
- **Thymeleaf** (if any dynamic server-rendered HTML)
- **Tailwind CSS** and **FontAwesome** (for modern UI)
- **JUnit** (testing)
- **Maven** (build and dependency management)

---

## Project Structure

healthinsureonline/
├── .idea/
├── .mvn/
├── .vscode/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sliit/
│   │   │           └── healthins/
│   │   │               ├── config/
│   │   │               │   ├── ApplicationConfig
│   │   │               │   ├── CustomUserDetails
│   │   │               │   ├── DatabaseConfig
│   │   │               │   ├── DataInitializer
│   │   │               │   ├── EmailConfig
│   │   │               │   ├── SecurityConfig
│   │   │               │   ├── SystemConfig
│   │   │               │   └── WebConfig
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── exception/
│   │   │               ├── model/
│   │   │               ├── pattern/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               ├── util/
│   │   │               └── HealthinsureonlineApplication
│   │   └── resources/
│   │       ├── META-INF/
│   │       ├── static/
│   │       │   ├── images/
│   │       │   ├── admin-system-settings.html
│   │       │   ├── claims-processing.html
│   │       │   ├── contact.html
│   │       │   ├── customer-portal.html
│   │       │   ├── customer_support.html
│   │       │   ├── dashboard.html
│   │       │   ├── hr-manager.html
│   │       │   ├── Lanka.jpeg
│   │       │   ├── login.html
│   │       │   ├── logo.jpeg
│   │       │   ├── marketing-manager.html
│   │       │   └── privacy-policy.html
│   │       └── application.properties
│   └── test/
├── target/
├── uploads/
├── .gitattributes
├── .gitignore
├── DESIGN_PATTERNS_DOCUMENTATION.md
├── health_ins_db.sql
├── healthinsureonline.iml
├── hs_err_pid7584.log
├── hs_err_pid8740.log
├── hs_err_pid9024.log
├── hs_err_pid13168.log
├── hs_err_pid30892.log
├── hs_err_pid31388.log
├── mvnw
├── mvnw.cmd
├── pom.xml
├── replay_pid8740.log
├── replay_pid9024.log
├── replay_pid13168.log
├── replay_pid30892.log


---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Database (H2 for dev, MySQL/PostgreSQL for production)
- Git (for VCS)

### Setup Steps

1. **Clone the repository**
   ```sh
   git clone https://github.com/your-org/healthinsureonline.git
   cd healthinsureonline
   ```

2. **Configure database connection**
   - Edit `src/main/resources/application.properties` for your RDBMS credentials, JDBC URL, username, password, etc.

3. **Build the project**
   ```sh
   mvn clean install
   ```

4. **Run the application**
   ```sh
   mvn spring-boot:run
   ```
   The backend will launch on [http://localhost:8080](http://localhost:8080) by default.

5. **Access the frontend**
   - Open `http://localhost:8080/login.html` in your browser or navigate to another static HTML page as needed.

6. **(Optional) Initialize DB**
   - Use the script `health_ins_db.sql` to create and seed the initial schema/data if not auto-configured.

---

## Available Scripts & Useful Commands

| Purpose                | Command                                    |
|------------------------|--------------------------------------------|
| Build project          | `mvn clean install`                        |
| Run application        | `mvn spring-boot:run`                      |
| Run tests              | `mvn test`                                 |
| Package JAR            | `mvn package`                              |
| Generate DB schema     | Auto via JPA/Hibernate or use .sql script  |

---

## Deployment

- For production, package and deploy the JAR/WAR to your application server.
- Configure `application.properties` for production database and credentials.
- Serve the `static/` directory assets with appropriate caching and HTTPS.

---

## Configuration

- **application.properties**: Central config for DB, ports, mail, security, etc.
- **Email**: Outgoing email settings (optional, see `EmailConfig`)
- **Security**: User roles and JWT settings in `SecurityConfig`.

---

## Design Patterns

This project intentionally applies several design patterns for maintainability and scalability, documented in [DESIGN_PATTERNS_DOCUMENTATION.md](DESIGN_PATTERNS_DOCUMENTATION.md). Examples include Singleton, Factory, Builder, Strategy, and Observer.

---

## Security & Best Practices

- All sensitive data is encrypted in transit and at rest.
- Passwords are hashed (never stored in plain text).
- Role-based access via Spring Security.
- Input validation and CSRF protection.
- Privacy Policy provided at `/privacy-policy.html`.

---

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a pull request

Please read our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) and development standards before contributing.

---

## Contact & Support

- **General Support:** [contact.html](src/main/resources/static/contact.html)
- **Email:** privacy@lankahealthins.com
- **Data Protection Officer:** dpo@lankahealthins.com
- **Address:** 123, Wellness Road, Colombo 1, Sri Lanka

For any bug reports or queries, please open an [issue](https://github.com/your-org/healthinsureonline/issues).

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**© 2025 Lanka Health Insurance. All rights reserved.**



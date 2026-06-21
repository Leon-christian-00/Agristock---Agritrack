# 🌾 AgriStock & AgriTrack - Agricultural Stock Management System

![Java](https://img.shields.io/badge/Java-26-blue?logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue?logo=postgresql)
![RMI](https://img.shields.io/badge/RMI-Distributed-orange)
![License](https://img.shields.io/badge/License-MIT-green)

> A distributed Java-based agricultural stock management system for Rwandan cooperatives. Built with Java 26, Hibernate ORM, RMI, and modern Swing UI.

## ✨ Features

- 🔐 **OTP Authentication** - Secure login via email OTP (5-minute expiry)
- 👥 **Role-Based Access** - ADMIN approves users; OFFICER manages data
- 👨‍🌾 **Farmer Management** - CRUD operations with Rwandan phone validation (+2507XXXXXXXX)
- 🌾 **Harvest Logging** - Record crop yields with quality grading (Grade A/B/Standard)
- 💰 **Sales Management** - Track sales with **automatic stock validation** (no overselling!)
- 📊 **Reporting** - Export harvest/sales data to CSV & generate professional PDF reports
- 📧 **Email Notifications** - Brevo SMTP for OTP, approvals, alerts
- 🔔 **ActiveMQ Integration** - Async event notifications (optional)
- 🎨 **Modern UI** - Custom Swing theme with gradients, rounded corners, responsive layouts

## 🏗️ Architecture

### Tech Stack
| Layer | Technology |
|-------|-----------|
| **Language** | Java 26 |
| **ORM** | Hibernate 8.0.0.Alpha1 |
| **Database** | PostgreSQL 16+ |
| **Communication** | Java RMI (Remote Method Invocation) |
| **UI** | Java Swing + Custom UITheme |
| **Messaging** | ActiveMQ 6.1.2 (JMS) |
| **Email** | Brevo SMTP (JavaMail) |
| **Reporting** | OpenPDF 2.0.2, Apache Commons CSV |
| **Build** | Maven 3.8+ |

## 🚀 Quick Start

### Prerequisites
- ✅ Java 26 or later ([Download](https://jdk.java.net/))
- ✅ PostgreSQL 16+ ([Download](https://www.postgresql.org/download/))
- ✅ Maven 3.8+ ([Download](https://maven.apache.org/download.cgi))
- ✅ ActiveMQ 6.1.2 (optional, for notifications) ([Download](https://activemq.apache.org/))

## Step 2: Database Setup
**-- Run in PostgreSQL (psql or pgAdmin):**
```sql
CREATE DATABASE agristock_rw_db;
CREATE USER agristock_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE agristock_rw_db TO agristock_user;
```
## Step 3: Configure Database Connection
--- Edit **AgriStockServer/src/main/resources/hibernate.cfg.xml:**
```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/agristock_rw_db</property>
<property name="hibernate.connection.username">agristock_user</property>
<property name="hibernate.connection.password">your_secure_password</property>
```

## Step 4: Configure Email (Recommended)
**AgriStockServer/src/main/java/com/wastonix/notification/EmailService.java**
```java
private static final String SMTP_LOGIN = "your_brevo_login";
private static final String SMTP_KEY = "your_brevo_api_key";
```

## Step 5: Build the Project
```bash
# Build server
cd server
mvn clean install

# Build client (in new terminal)
cd ../client
mvn clean install
```

## Step 6: Run the Server
```bash
cd server
java -cp target/AgriStockServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap
```
✅ Wait for: ✅ AgriStockService bound and ready.

## Step 7: Run the Client
```java
java -cp target/AgriStockClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame
```
✅ Login window opens!

---

## Step 8: First Login (Admin)
Click Register tab
Enter:
Name: Waston Administrator
Email: wastonorganisation@gmail.com
Phone: +250790734995
Submit → Wait for approval (auto-approved in RmiServerBootstrap.java)
Switch to Login tab
Enter email → Click Send OTP → Check console/email for code
Enter OTP → Click Verify & Sign In
🎉 Dashboard opens with ADMIN privileges!

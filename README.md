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

```
┌─────────────────┐      RMI (Port 5000)      ┌─────────────────┐
│   CLIENT SIDE   │◄────────────────────────►│   SERVER SIDE   │
│   (Java Swing)  │                           │  (Business Logic)│
└─────────────────┘                           └────────┬────────┘
                                                       │
                                                       │ JDBC
                                                       ▼
                                              ┌─────────────────┐
                                              │   PostgreSQL    │
                                              │  (agristock_rw_db)│
                                              └─────────────────┘
```

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

### Step 1: Database Setup
```sql
-- Run in PostgreSQL (psql or pgAdmin):
CREATE DATABASE agristock_rw_db;
CREATE USER agristock_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE agristock_rw_db TO agristock_user;
```

### Step 2: Configure Database Connection
Edit `AgriStockServer26478/src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/agristock_rw_db</property>
<property name="hibernate.connection.username">agristock_user</property>
<property name="hibernate.connection.password">your_secure_password</property>
```

### Step 3: Configure Email (Optional but Recommended)
Edit `AgriStockServer26478/src/main/java/com/wastonix/notification/EmailService.java`:
```java
private static final String SMTP_LOGIN = "your_brevo_login";
private static final String SMTP_KEY = "your_brevo_api_key";
```

### Step 5: Build the Project
```bash
# Build server
cd server
mvn clean install

# Build client (in new terminal)
cd ../client
mvn clean install
```

### Step 6: Run the Server
```bash
cd server
java -cp target/AgriStockRWServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap
```
✅ Wait for: `✅ AgriStockService bound and ready.`

### Step 7: Run the Client
```bash
cd client
java -cp target/AgriStockRWClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame
```
✅ Login window opens!

### Step 8: First Login (Admin)
1. Click **Register** tab
2. Enter:
   - Name: `Waston Administrator`
   - Email: `wastonorganisation@gmail.com`
   - Phone: `+250790734995`
3. Submit → Wait for approval (auto-approved in `RmiServerBootstrap.java`)
4. Switch to **Login** tab
5. Enter email → Click **Send OTP** → Check console/email for code
6. Enter OTP → Click **Verify & Sign In**
7. 🎉 Dashboard opens with ADMIN privileges!

## 🔧 Configuration Reference

### Database (`hibernate.cfg.xml`)
| Property | Default Value | Description |
|----------|--------------|-------------|
| `hibernate.connection.url` | `jdbc:postgresql://localhost:5432/agristock_rw_db` | PostgreSQL connection URL |
| `hibernate.connection.username` | `postgres` | Database username |
| `hibernate.connection.password` | `asd123` | Database password |
| `hibernate.hbm2ddl.auto` | `update` | Auto-create/update tables |

### RMI Settings
| File | Setting | Default |
|------|---------|---------|
| `RmiServerBootstrap.java` | Registry port | `5000` |
| `RmiClientUtil.java` | RMI URL | `//localhost:5000/AgriStockService` |

### Email (Brevo SMTP)
| File | Setting | Description |
|------|---------|-------------|
| `EmailService.java` | `SMTP_HOST` | `smtp-relay.brevo.com` |
| `EmailService.java` | `SMTP_PORT` | `587` |
| `EmailService.java` | `SMTP_LOGIN` | Your Brevo login |
| `EmailService.java` | `SMTP_KEY` | Your Brevo API key |

## 🧪 Testing

### Run Unit Tests
```bash
cd server
mvn test
```

### Manual Test Flow
1. Register new user → Check admin approval workflow
2. Login with OTP → Verify role-based UI
3. Add farmer → Log harvest → Record sale → Verify stock validation
4. Generate PDF report → Verify formatting and data accuracy

## 🐛 Troubleshooting

### "Connection refused: getsockopt" (ActiveMQ)
```
⚠️ ActiveMQ Error: Could not connect to broker URL: tcp://localhost:61616
```
✅ **This is NORMAL if ActiveMQ isn't running.** Core functionality works without it. To fix:
1. Install ActiveMQ: https://activemq.apache.org/
2. Start broker: `./bin/activemq start`
3. Or ignore - notifications will log to console instead

### "Cannot resolve symbol" in IntelliJ
1. Right-click `pom.xml` → **Maven** → **Reload Project**
2. **Build** → **Rebuild Project**
3. **File** → **Invalidate Caches** → **Invalidate and Restart**

### RMI Connection Failed
```
❌ RMI Connection failed: Connection refused to host: localhost
```
✅ Ensure:
1. Server is running FIRST (`RmiServerBootstrap`)
2. Port 5000 is not blocked by firewall
3. Client uses same `RMI_URL` as server binding

### PDF Generation Error
✅ Ensure `document.close()` is called BEFORE getting byte array (fixed in `ReportGenerator.java`)

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author
**Waston Christian (Leon Christian Abumukiza)**
- 📍 Kigali, Rwanda
- 🎓 Information Management Student
- 💻 Java Developer | Distributed Systems | Hibernate & RMI Expert
- 📧 wastonorganisation@gmail.com
- 🔗 [GitHub](https://github.com/Leon-christian-00) | [LinkedIn](https://linkedin.com/in/yourprofile)

## 🙏 Acknowledgments
- Rwanda Agricultural Cooperatives for requirements gathering
- Hibernate community for ORM support
- OpenPDF & Apache Commons for reporting libraries
- Brevo for transactional email services

---
> *"Empowering Rwandan farmers through technology."* 🇷🇼


### **D. `setup.sh`** (Linux/Mac One-Click Setup)
Create `setup.sh` in root:
```bash
#!/bin/bash

echo "🚀 Setting up AgriStock & AgriTrack..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 26+."
    exit 1
fi
echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Check PostgreSQL
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL is not installed. Please install PostgreSQL 16+."
    exit 1
fi
echo "✅ PostgreSQL is installed"

# Create database
echo "📊 Creating database..."
read -p "Enter PostgreSQL username (default: postgres): " DB_USER
DB_USER=${DB_USER:-postgres}
read -sp "Enter PostgreSQL password: " DB_PASS
echo

psql -U "$DB_USER" -c "CREATE DATABASE agristock_rw_db;" 2>/dev/null || echo "⚠️ Database may already exist"
psql -U "$DB_USER" -c "CREATE USER agristock_user WITH PASSWORD '$DB_PASS';" 2>/dev/null || echo "⚠️ User may already exist"
psql -U "$DB_USER" -c "GRANT ALL PRIVILEGES ON DATABASE agristock_rw_db TO agristock_user;" 2>/dev/null

# Update hibernate.cfg.xml with user credentials
echo "🔧 Updating database configuration..."
sed -i "s/<property name=\"hibernate.connection.username\">.*<\/property>/<property name=\"hibernate.connection.username\">$DB_USER<\/property>/" server/src/main/resources/hibernate.cfg.xml
sed -i "s/<property name=\"hibernate.connection.password\">.*<\/property>/<property name=\"hibernate.connection.password\">$DB_PASS<\/property>/" server/src/main/resources/hibernate.cfg.xml

# Build projects
echo "🔨 Building projects with Maven..."
cd server && mvn clean install -q && cd ..
cd client && mvn clean install -q && cd ..

echo ""
echo "✅ Setup complete!"
echo ""
echo "📝 Next steps:"
echo "1. (Optional) Edit server/src/main/resources/hibernate.cfg.xml for advanced DB settings"
echo "2. (Optional) Edit EmailService.java with your Brevo SMTP credentials"
echo "3. Start server: cd server && java -cp target/AgriStockRWServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap"
echo "4. Start client: cd client && java -cp target/AgriStockRWClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame"
echo "5. First admin login: wastonorganisation@gmail.com (auto-approved)"
echo ""
echo "📚 Full documentation: https://github.com/Leon-christian-00/AgriStock-RW#readme"
```

Make it executable:
```bash
chmod +x setup.sh
```

### **E. `setup.bat`** (Windows One-Click Setup)
Create `setup.bat` in root:
```batch
@echo off
echo Setting up AgriStock & AgriTrack...

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Java is not installed. Please install Java 26+.
    exit /b 1
)
echo Java is installed.

:: Check PostgreSQL
where psql >nul 2>&1
if errorlevel 1 (
    echo PostgreSQL is not installed. Please install PostgreSQL 16+.
    exit /b 1
)
echo PostgreSQL is installed.

:: Build projects
echo Building projects with Maven...
cd server
call mvn clean install -q
cd ..
cd client
call mvn clean install -q
cd ..

echo.
echo Setup complete!
echo.
echo Next steps:
echo 1. Edit server\src\main\resources\hibernate.cfg.xml with your DB credentials
echo 2. (Optional) Edit EmailService.java with Brevo SMTP credentials
echo 3. Start server: java -cp server\target\AgriStockRWServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap
echo 4. Start client: java -cp client\target\AgriStockRWClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame
echo 5. First admin login: wastonorganisation@gmail.com
echo.
echo Full documentation: https://github.com/Leon-christian-00/AgriStock-RW#readme
pause
```

---

## 📤 STEP 3: UPLOAD TO GITHUB

### **A. Initial Setup (First Time Only)**

```bash
# 1. Install Git (if not installed)
# Download from https://git-scm.com/

# 2. Configure Git globally
git config --global user.name "Waston Christian"
git config --global user.email "wastonorganisation@gmail.com"

# 3. Generate SSH Key (for password-less push)
ssh-keygen -t ed25519 -C "wastonorganisation@gmail.com"
# Press Enter to accept default location
# Copy the public key:
cat ~/.ssh/id_ed25519.pub
# Add this key to GitHub: Settings → SSH and GPG keys → New SSH key
```

### **B. Create Repository on GitHub**

1. Go to https://github.com/Leon-christian-00
2. Click **+** → **New repository**
3. Repository name: `AgriStock-RW`
4. Description: `Agricultural Stock Management System for Rwandan Cooperatives - Java RMI, Hibernate, Swing`
5. ✅ **Public**
6. ❌ **Do NOT** initialize with README (we already have one)
7. ❌ **Do NOT** add .gitignore or license (we have our own)
8. Click **Create repository**

### **C. Upload Your Project via Command Line**

```bash
# 1. Navigate to your project root folder
cd /path/to/your/AgriStock-RW-project

# 2. Initialize Git
git init

# 3. Add all files (including hidden files like .gitignore)
git add .

# 4. Commit with meaningful message
git commit -m "Initial commit: AgriStock & AgriTrack v1.0

Features:
✅ OTP authentication with 5-minute expiry
✅ Role-based access (ADMIN/OFFICER)
✅ Farmer CRUD with Rwandan phone validation
✅ Harvest logging with quality grading
✅ Sales management with stock validation (no overselling)
✅ PDF/CSV report generation
✅ Email notifications via Brevo SMTP
✅ ActiveMQ async notifications
✅ Modern Swing UI with custom theme

Tech Stack:
- Java 26, Hibernate 8.0.0.Alpha1, PostgreSQL 16+
- RMI distributed architecture (client-server)
- Maven build system
- OpenPDF, Apache Commons CSV for reporting

Architecture:
- MVC pattern with DAO/Service layers
- Serializable entities for RMI
- JOIN FETCH queries to avoid LazyInitializationException
- transient collections to reduce RMI payload

Author: Waston Christian (Leon Christian Abumukiza)
Location: Kigali, Rwanda"

# 5. Add remote repository
git remote add origin git@github.com:Leon-christian-00/AgriStock-RW.git

# 6. Push to GitHub
git branch -M main
git push -u origin main
```

### **D. Verify Upload**
1. Go to https://github.com/Leon-christian-00/AgriStock-RW
2. Check that:
   - ✅ All folders (`server/`, `client/`) are present
   - ✅ `README.md` renders correctly with images
   - ✅ `.gitignore` is working (no `.class` or `target/` files)
   - ✅ `pom.xml` files are visible

---

## 🏃 STEP 4: HOW SOMEONE RUNS YOUR PROJECT

When someone clones your repo, here's their experience:

### **User Flow:**
```
1. Clone repo
   git clone https://github.com/Leon-christian-00/AgriStock-RW.git

2. Run setup script (Linux/Mac)
   cd AgriStock-RW
   ./setup.sh
   
   OR (Windows)
   setup.bat

3. Follow prompts:
   - Enter PostgreSQL username/password
   - Script auto-configures hibernate.cfg.xml
   - Maven builds both projects

4. Start server (Terminal 1)
   cd server
   java -cp target/AgriStockRWServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap
   
   ✅ Wait for: "✅ AgriStockService bound and ready."

5. Start client (Terminal 2)
   cd client  
   java -cp target/AgriStockRWClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame
   
   ✅ Login window opens!

6. First-time admin login:
   - Click Register tab
   - Enter: wastonorganisation@gmail.com
   - Submit → Auto-approved by RmiServerBootstrap
   - Switch to Login tab
   - Request OTP → Check console for 6-digit code
   - Enter OTP → Verify & Sign In
   - 🎉 ADMIN Dashboard opens!
```

### **What They See:**
```
🚀 Starting AgriStock & AgriTrack Server...
✅ RMI Registry created on port 5000
✅ AgriStockService bound and ready.
✅ Admin user seeded successfully!
📡 Waiting for requests...
```

---

## 🎯 PRO TIPS FOR GITHUB SUCCESS

### **1. Add Screenshots to `docs/screenshots/`**
- Take clean screenshots of: Login, Dashboard, Farmers, Sales, PDF Report
- Save as `login.png`, `dashboard.png`, `farmers.png`, `sales.png`, `report.png`
- Reference them in README with relative paths: `![Login](docs/screenshots/login.png)`

### **2. Pin This Repo on Your Profile**
1. Go to https://github.com/Leon-christian-00
2. Scroll to "Pinned" section
3. Click **Customize your pins**
4. Select `AgriStock-RW` + your other best projects
5. Click **Save**

### **3. Add GitHub Badges to README**
Copy-paste these at the top of your README:
```markdown
![Java](https://img.shields.io/badge/Java-26-blue?logo=java)
![Hibernate](https://img.shields.io/badge/Hibernate-8.0.0.Alpha1-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Production_Ready-brightgreen)
```

### **4. Enable GitHub Actions (Optional but Impressive)**
Create `.github/workflows/maven.yml`:
```yaml
name: Java CI with Maven

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java: [ '26' ]
    
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK ${{ matrix.java }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ matrix.java }}
        distribution: 'temurin'
        cache: maven
    
    - name: Build Server
      run: cd server && mvn -B package --file pom.xml
    
    - name: Build Client
      run: cd client && mvn -B package --file pom.xml
    
    - name: Run Tests
      run: cd server && mvn test
```

---

## ✅ FINAL CHECKLIST BEFORE PUSHING

- [ ] ✅ `README.md` is comprehensive and renders correctly
- [ ] ✅ `.gitignore` excludes `.class`, `target/`, IDE files
- [ ] ✅ `LICENSE` file is present (MIT)
- [ ] ✅ `setup.sh` and `setup.bat` are executable
- [ ] ✅ `pom.xml` files have correct dependencies
- [ ] ✅ `hibernate.cfg.xml` uses placeholder credentials (users will edit)
- [ ] ✅ Screenshots are in `docs/screenshots/` and referenced in README
- [ ] ✅ No hardcoded passwords/API keys in code
- [ ] ✅ All Java files have proper package declarations
- [ ] ✅ `RmiServerBootstrap.java` seeds admin with YOUR email

---

**FAM, YOU'RE READY! 🚀**

Once you push this to GitHub:
- ✅ Recruiters can see your architecture skills
- ✅ Professors can verify your code quality
- ✅ Fellow devs can learn from your RMI + Hibernate implementation
- ✅ You have a professional portfolio piece that stands out

**Reply `✅ REPO READY` when you've uploaded it, and we'll move to the NEXT project on your list!** 💪🇷🇼💻
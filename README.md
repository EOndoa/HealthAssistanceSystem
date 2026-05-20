# Health Assistance System

JavaFX and MySQL desktop application for managing patients, doctors, appointments, users, and patient health records. 
As an Online student, I was not able to have a group that is fixed. I tried to work  myself. This project is the final exam of Level 2 for the Course Java Programming II at the ICT UNIVERSITY.
NAME: ONDOA ONDOA ETIENNE MATERNE
MAT: ICTU20233886

## Features

- Role-based login for *PATIENT*, *DOCTOR*, and *ADMINISTRATOR*
- Patient registration, viewing, updating, and health record access
- Doctor registration, viewing, specialization, and schedule management
- Appointment booking, cancellation, upcoming appointment view, and conflict prevention
- MySQL persistence through JDBC
- JavaFX graphical interface
- Background appointment reminder process using a scheduled thread

## Role-Based Access

- ADMINISTRATOR: manages patients, doctors, appointments, and user accounts[admin create a user account and give login information to the patient].
- DOCTOR: sees only patients and appointments assigned to that doctor, manages assigned patient records, and can refer patients to another doctor for advanced consultation.
- PATIENT: sees only their own health record and appointments, can book appointments for themselves, and can cancel only their own appointments.

Doctor and patient accounts are linked by username. Use the doctor's or patient's email address as the login username when creating non-administrator accounts.

## Requirements Coverage

- User Management: supported through role-based login and account creation.
- Patient Management: supported through patient registration, viewing, updates, and health records.
- Doctor Management: supported through doctor registration, viewing, specialization management, and calendar-based consultation days.
- Appointment Management: supported through booking, cancellation, upcoming appointment views, referral appointments, and doctor-slot conflict prevention.
- Database Connectivity: implemented with MySQL and JDBC.
- Graphical User Interface: implemented with JavaFX.
- Multithreading: implemented with a scheduled background appointment reminder process.

## How To Run The Application

Follow these steps in order.

### 1. Install Required Tools

Make sure the following tools are installed:

- Java JDK 21 or later
- MySQL Server
- MySQL Workbench or MySQL command line
- Maven

This project also contains a bundled Maven folder in `.tools`, so you can run the application without installing Maven globally.

### 2. Start MySQL

Start your MySQL Server before running the application.

If you use MySQL Workbench:

1. Open MySQL Workbench.
2. Connect to your local MySQL server.
3. Keep the server running.

### 3. Create The Database

Run the SQL script located at:

```text
sql/schema.sql
```

Using MySQL Workbench:

1. Open MySQL Workbench.
2. Open the file `sql/schema.sql`.
3. Click the lightning/run button to execute the script.

Using the MySQL command line:

```powershell
mysql -u root -p < sql\schema.sql
```

This creates the `health_assistance` database, the required tables, and the default administrator account.

### 4. Check Database Credentials

Open this file:

```text
src/main/resources/com/healthassist/database.properties
```

Make sure the username and password match your MySQL setup.

Example:

```properties
db.url=jdbc:mysql://localhost:3306/health_assistance
db.user=root
db.password=your_mysql_password
```

### 5. Open The Project Folder In PowerShell

```powershell
cd "C:\Users\Administrator\Documents\HealthAssistanceSystem"
```

### 6. Compile The Project

Using bundled Maven:

```powershell
.\.tools\apache-maven-3.9.16\bin\mvn.cmd -q -DskipTests compile
```

If Maven is installed globally:

```powershell
mvn -q -DskipTests compile
```

### 7. Run The Application

Using bundled Maven:

```powershell
.\.tools\apache-maven-3.9.16\bin\mvn.cmd javafx:run
```

If Maven is installed globally:

```powershell
mvn javafx:run
```

### 8. Log In

Use the default administrator account:

```text
Username: admin
Password: admin123
```

### 9. Create Doctor And Patient Accounts

After logging in as administrator:

1. Go to the `Users` tab.
2. Create doctor and patient accounts.
3. For doctor accounts, use the doctor's email address as the username.
4. For patient accounts, use the patient's email address as the username.

This is important because the system uses the email username to connect each doctor or patient account to their correct record.

### 10. Common Problems

If the application cannot connect to the database:

- Make sure MySQL is running.
- Make sure `sql/schema.sql` was executed successfully.
- Check `database.properties`.
- Confirm that the database name is `health_assistance`.

If Maven is not recognized:

- Use the bundled Maven command shown above.
- Or install Maven and add it to your system PATH.

You can also open the folder in IntelliJ IDEA, Eclipse, or NetBeans as a Maven project and run:

```text
com.healthassist.Main
```

## Project Structure

- model: domain classes and enums
- dao: JDBC data access classes
- service: login and appointment reminder services
- ui: JavaFX screens
- db: database connection helper
- sql/schema.sql: database schema and seed admin user

## Downloading And Editing The Project From GitHub

Use this section if another user wants to download the project, make changes, and upload those changes back to GitHub.

### 1. Install Git

Download and install Git from:

```text
https://git-scm.com/downloads
```

After installation, confirm Git works:

```powershell
git --version
```

### 2. Clone The Project

Open PowerShell and choose where you want to save the project. For example:

```powershell
cd "C:\Users\Administrator\Documents"
```

Then clone the repository:

```powershell
git clone https://github.com/EOndoa/HealthAssistanceSystem.git
```

Enter the project folder:

```powershell
cd HealthAssistanceSystem
```

### 3. Open The Project For Editing

You can open the project in any Java IDE, such as:

- IntelliJ IDEA
- Eclipse
- NetBeans
- Visual Studio Code

Open the folder:

```text
HealthAssistanceSystem
```

Because the project uses Maven, the IDE should detect the `pom.xml` file and load the dependencies automatically.

### 4. Create A New Branch Before Editing

It is professional to work on a separate branch instead of editing `main` directly.

Example:

```powershell
git checkout -b feature/update-interface
```

Use a clear branch name based on the work you are doing, such as:

```text
feature/add-reporting
fix/login-validation
docs/update-readme
```

### 5. Make Your Changes

Edit the source code, database script, README, or screenshots as needed.

Important project folders:

```text
src/main/java/com/healthassist
src/main/resources/com/healthassist
sql
README.md
```

After editing, compile the project to make sure it still works:

```powershell
.\.tools\apache-maven-3.9.16\bin\mvn.cmd -q -DskipTests compile
```

Or, if Maven is installed globally:

```powershell
mvn -q -DskipTests compile
```

### 6. Check The Changed Files

Before committing, check what changed:

```powershell
git status
```

To review the exact code changes:

```powershell
git diff
```

### 7. Add The Changes

Add all modified files:

```powershell
git add .
```

Or add one file at a time:

```powershell
git add README.md
```

### 8. Commit The Changes

Create a commit with a clear message:

```powershell
git commit -m "Update README with GitHub workflow instructions"
```

Good commit messages should explain what changed, for example:

```text
Add doctor referral workflow
Fix patient appointment privacy
Improve login screen layout
Update database setup instructions
```

### 9. Push The Branch To GitHub

Push your branch:

```powershell
git push -u origin feature/update-interface
```

If you are pushing directly to `main`, use:

```powershell
git push origin main
```

### 10. Create A Pull Request

If you worked on a separate branch:

1. Open the repository on GitHub.
2. Click `Compare & pull request`.
3. Write a short description of the change.
4. Submit the pull request.

This allows the project owner to review the changes before merging them into `main`.

### 11. Keep Your Local Project Updated

Before starting new work, download the latest changes:

```powershell
git checkout main
git pull origin main
```

Then create a new branch for your next change:

```powershell
git checkout -b feature/new-change
```

### 12. Authentication Notes

GitHub no longer accepts normal account passwords for Git push operations.

If Git asks for a password, use a GitHub Personal Access Token.

The token should have permission to read and write repository contents.

Repository URL:

```text
https://github.com/EOndoa/HealthAssistanceSystem
```

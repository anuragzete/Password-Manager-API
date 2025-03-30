# 🔐 Password Manager API

**A robust and secure RESTful API** designed to manage password data with **AES encryption**, **synchronization mechanisms**, and support for **both hard and soft sync** strategies.

---

## 🚀 Features

### 🔥 **Authentication & Security**
- **JWT-based authentication** for secure access.
- AES-encrypted password storage.
- **Master password** validation to unlock encrypted data.

### 🔄 **Synchronization Mechanism**
- **Soft Sync:**
    - Only merges missing or new records.
    - Ensures faster, conflict-free synchronization.
- **Hard Sync:**
    - Performs a full data overwrite.
    - Ensures consistency with the backend.

### 🌐 **RESTful Endpoints**
- **/auth** → Authentication and token management.
- **/crud** → Create, read, update, and delete encrypted passwords.
- **/sync** → Sync local and remote data seamlessly.

### 💾 **Data Storage & Encryption**
- Stores encrypted passwords in a **MongoDB database**.
- AES-256 encryption ensures data privacy.

---

## 🛠️ Tech Stack

- **Java (Backend)**: Core API implementation.
- **MongoDB**: Database for storing encrypted data.
- **AES-256 Encryption**: Secures stored passwords.
- **JWT Authentication**: For secure API access.

---

## 📊 REST API Endpoints

### 🔑 **Authentication**
- `POST /auth/login`: Authenticate with master password → Returns JWT token.
- `POST /auth/logout`: Invalidate JWT token.

### 🔥 **CRUD Operations**
- `POST /crud`: Create new encrypted password.
- `GET /crud`: Fetch all encrypted passwords.
- `PUT /crud/{id}`: Update existing password by ID.
- `DELETE /crud/{id}`: Delete password by ID.

### 🔄 **Sync Operations**
- `POST /sync/soft`: Perform soft sync.
- `POST /sync/hard`: Perform hard sync.

---

## 💻 How to Run Locally

### ✅ **Prerequisites:**
- **JDK 21** installed.
- **Apache Tomcat** server.
- **MongoDB credentials** configured in `config.properties` at the root directory:
```
MONGO_URI=<your-mongodb-uri>
DB_NAME=<your-database-name>
COLLECTION_NAME=<your-collection-name>
```

### 🚀 **Steps:**
1. Clone the repository:
```bash
$ git clone https://github.com/anuragzete/Password-Manager-API.git
```
2. Install dependencies:
```bash
$ mvn clean install
```
3. Deploy the WAR file to Tomcat:
- Copy the WAR file from `target/` to `TOMCAT_HOME/webapps/`
- Start Tomcat server.
4. Access the API at:
```
http://localhost:8080
```

---

## 📚 Folder Structure

```plaintext
/password-manager-api
 ├── src
 │     ├── com/password/api            # Core API logic
 │     │     ├── AuthServlet                  # Authentication handlers
 │     │     ├── CRUDServlet                  # CRUD operations
 │     │     ├── SyncServlet                  # Sync logic
 │     │     ├── MongoDBConnection            # MongoDB connection utility class
 │     └── resources                   # Configuration and assets
 ├── config.properties                 # MongoDB credentials
 ├── README.md
 ├── LICENSE
 ├── docs                              # Javadoc documentation
 ├── target                            # Build artifacts
```

---

## ⚖️ License

This project is licensed under the **MIT License**. Feel free to use, modify, and distribute it.

---

## 📧 Contact

- **Email:** anuragzete27@outlook.com
- **Portfolio:** [Anurag Zete](https://portfolio-anuragzete.web.app)

---

🔐 **Efficient and secure password management through a powerful API!** 🎉


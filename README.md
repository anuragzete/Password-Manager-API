# 🚀 Multi-threaded Download Manager

A multi-threaded download manager built with Java Swing. It allows downloading multiple files simultaneously, displaying individual and overall progress with download speed.

---

## 📁 Directory Structure
```
Download_Manager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── multithreaded/
│   │   │           ├── downloader/          # Core download logic
│   │   │           └── downloaderUI/        # Java Swing UI components
│   │   ├── resources/
│   │   │   └── icons/                       # Icon assets
├── pom.xml                                  # Maven configuration file (if used)
├── build.gradle                             # Gradle configuration file (if used)
├── LICENSE                                  # License file
├── README.md                                # Project documentation
```

---

## ⚙️ Features
- Multi-threaded downloads with concurrent execution.
- Real-time progress bars and download speed display.
- Console log for download status.
- Cancel and stop all downloads functionality.
- Easy-to-use Java Swing GUI.

---

## 🛠️ Installation
### Clone the repository
```bash
git clone https://github.com/anuragzete/Download-Manager.git
cd Download_Manager
```

### Compile and Run
If using Maven:
```bash
mvn clean install
java -cp target/Download_Manager-1.0.jar com.multithreaded.downloaderUI.Main
```
If using Gradle:
```bash
gradle build
java -cp build/libs/Download_Manager-1.0.jar com.multithreaded.downloaderUI.Main
```

---

## 🖥️ Usage
1. **Paste URLs:** Enter multiple URLs separated by newlines in the input area.
2. **Start All:** Begins downloading all the URLs concurrently.
3. **Stop All:** Pauses all ongoing downloads.
4. **Cancel All:** Stops and removes all downloads.
5. **Console:** Displays logs with download status, errors, and completion messages.

---

## 🛠️ Technologies Used
- Java (Swing for GUI)
- Multi-threading
- Maven or Gradle for build management

---

## 📝 Future Enhancements
- ✅ **Pause/Resume for individual downloads**
- ✅ **More advanced download management** (retry failed downloads, auto-resume on app restart)
- ✅ **Dark mode UI** 🌙
- ✅ **Database integration for tracking download history**
- ✅ **Custom file destination selection** 📂

---

## ✨ Screenshots
📸 **Main Download Panel**
![Main UI](./screenshots/main_ui.png)

📸 **Progress Panel**
![Progress UI](./screenshots/progress_ui.png)

---

## 📌 Contributions
Contributions are welcome! Feel free to fork the project, submit issues, or create pull requests.

---

## 📜 License
This project is licensed under the [MIT License](LICENSE).

---

## 📧 Contact
For any issues or inquiries, contact me at: [anuragzete27@outlook.com](mailto:anuragzete27@outlook.com)

---

## ✅ Happy downloading! 🚀

# Kotlin UDP File Transfer

![Kotlin](https://img.shields.io/badge/Kotlin-Black?style=for-the-badge&logo=kotlin&logoColor=white)
![UDP Protocol](https://img.shields.io/badge/UDP_Protocol-Red?style=for-the-badge&logo=databricks&logoColor=white)
![CLI & GUI](https://img.shields.io/badge/CLI_&_GUI-White?style=for-the-badge&logo=gnometerminal&logoColor=black)

A robust Client-Server system for local network file transfers using the **UDP** protocol. This project implements block sending (*chunking*), end-to-end integrity verification, and a command-line interface with real-time visual feedback.

---
Next steps:
* Implement packet order verification with a waiting queue
---

## ✨ Features

* **UDP Transfer:** Fast communication using `DatagramSocket` and `DatagramPacket`.
* **Data Integrity (SHA-1):** Since UDP does not guarantee perfect delivery, each 1024-byte packet carries a 20-byte SHA-1 signature. The server recalculates the hash upon arrival and discards corrupted packets.
* **Dynamic Progress Bar:** Visual feedback in the terminal (`|====>     | 50%`) calculated based on the file size and the number of packets sent/received.
* **File Selection (GUI):** The client uses Java Swing's `JFileChooser` with the operating system's native theme to facilitate file selection.
* **Logging System:** The server records start, success, and error events with timestamps using `KotlinLogging`.

---

## 📂 Structure

```text
📦 kotlin-udp-file-transfer
 ┣ 📂 client
 ┃ ┗ 📂 src/main/kotlin/bonfim/jordan
 ┃ ┃ ┗ 📜 Client.kt
 ┣ 📂 server
 ┃ ┣ 📂 files (Received files are saved here)
 ┃ ┗ 📂 src/main/kotlin/bonfim/jordan
 ┃ ┃ ┗ 📜 Server.kt
 ┗ 📜 README.md

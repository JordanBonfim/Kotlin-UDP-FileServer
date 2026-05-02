# Kotlin UDP File Transfer

![Kotlin](https://img.shields.io/badge/Kotlin-Black?style=for-the-badge&logo=kotlin&logoColor=white)
![UDP Protocol](https://img.shields.io/badge/UDP_Protocol-Red?style=for-the-badge&logo=databricks&logoColor=white)
![CLI & GUI](https://img.shields.io/badge/CLI_&_GUI-White?style=for-the-badge&logo=gnometerminal&logoColor=black)

Um sistema Cliente-Servidor robusto para transferência de arquivos via rede local utilizando o protocolo **UDP**. Este projeto implementa envio em blocos (*chunking*), verificação de integridade de ponta a ponta e uma interface de linha de comando com feedback visual em tempo real.

---
Próximos passos:
* Implementar verificação de ordem dos pacotes com fila de espera
---

## ✨ Funcionalidades

* **Transferência via UDP:** Comunicação rápida utilizando `DatagramSocket` e `DatagramPacket`.
* **Integridade de Dados (SHA-1):** Como o UDP não garante a entrega perfeita, cada pacote de 1024 bytes carrega uma assinatura SHA-1 de 20 bytes. O servidor recalcula o hash na chegada e descarta pacotes corrompidos.
* **Barra de Progresso Dinâmica:** Feedback visual no terminal (`|====>     | 50%`) calculado com base no tamanho do arquivo e na quantidade de pacotes enviados/recebidos.
* **Seleção de Arquivos (GUI):** O cliente utiliza o `JFileChooser` do Java Swing com o tema nativo do sistema operacional para facilitar a escolha dos arquivos.
* **Sistema de Logs:** O servidor registra eventos de início, sucesso e erro com timestamps usando `KotlinLogging`.

---

## 📂 Estrutura

```text
📦 kotlin-udp-file-transfer
 ┣ 📂 client
 ┃ ┗ 📂 src/main/kotlin/bonfim/jordan
 ┃ ┃ ┗ 📜 Client.kt
 ┣ 📂 server
 ┃ ┣ 📂 files (Arquivos recebidos são salvos aqui)
 ┃ ┗ 📂 src/main/kotlin/bonfim/jordan
 ┃ ┃ ┗ 📜 Server.kt
 ┗ 📜 README.md

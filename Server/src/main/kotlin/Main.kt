package bonfim.jordan

import java.io.File
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.security.MessageDigest
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private val logger = KotlinLogging.logger {}

fun main() {
    val porta = 5090
    val socket = DatagramSocket(porta)
    val buffer = ByteArray(1024+20)
    val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")

    println("Servidor UDP rodando na porta $porta. Aguardando arquivos...")

    val digest = MessageDigest.getInstance("SHA-1")
    logger.info { "SERVER STARTED AT ${LocalDateTime.now().format(dateFormatter)}" }

    while (true) {
        try {
            // Receive file size and name
            val initialPackage = DatagramPacket(buffer, buffer.size)
            socket.receive(initialPackage)

            val startStr = LocalDateTime.now().format(dateFormatter)

            val receivedData = String(initialPackage.data, 0, initialPackage.length)

            val parts = receivedData.split("%", limit = 2)

            if(parts.size == 2) {

                val totalSize = parts[0].toLong()

                if (totalSize == null) {
                    val endStr = LocalDateTime.now().format(dateFormatter)
                    println("Erro: Tamanho do arquivo inválido recebido -> $totalSize")
                    logger.info { "[ERROR] FILE NAME: ${parts[1]} | SIZE: ${totalSize} BYTES | STARTED AT: $startStr | FINISHED AT: $endStr" }
                    continue // Volta para o início do loop principal
                }

                val fileName = parts[1]

                println("Arquivo recebido -> Nome: $fileName | Tamanho: $totalSize bytes")

                println("Iniciando transferência: $fileName ($totalSize bytes)")

                // Cria o arquivo no disco
                val arquivoDestino = File("./files/$fileName")
                val outputStream = FileOutputStream(arquivoDestino)
                var bytesRecebidos: Long = 0


                var progressBar = "                                                  "


                //CALCULO PARA BARRA DE PROGRESSO
                val totalProgressParts = 50
                var totalPackages = totalSize/1024
                if(totalPackages.toInt() ==0) totalPackages=1

                var packagesPerProgresssPart = 1

                if(totalPackages>50){
                    packagesPerProgresssPart = (totalPackages/totalProgressParts).toInt()
                }

                var packageNumber: Long = 0
                println("Progress:")

                var percentage = 0

                // Loop para receber as partes do arquivo
                while (bytesRecebidos < totalSize) {
                    val pacoteDados = DatagramPacket(buffer, buffer.size)
                    socket.receive(pacoteDados)

                    val tamanhoTotalPacote = pacoteDados.length
                    val tamanhoDados = tamanhoTotalPacote - 20

                    val checksum = ByteArray(20)
                    System.arraycopy(buffer, tamanhoDados, checksum, 0, 20)

                    digest.update(buffer, 0, tamanhoDados)
                    val checksumCalculado = digest.digest()

                    if( checksumCalculado.contentEquals(checksum)) {
                        outputStream.write(buffer, 0, tamanhoDados)
                        bytesRecebidos += tamanhoDados

                        packageNumber+=1
                        percentage = (packageNumber*100/totalPackages).toInt()

                        if((packageNumber%packagesPerProgresssPart).toInt() == 0  ){
                            progressBar = progressBar.replaceFirst(">", "=")
                            progressBar = progressBar.replaceFirst(" ", ">")

                        }
                        print("|$progressBar| $percentage% \r")
                    }else{
                        println("Erro na integridade no pacote.")
                    }
                }

                val endStr = LocalDateTime.now().format(dateFormatter)
                logger.info { "[SUCCESS] FILE NAME: ${parts[1]} | SIZE: $totalSize BYTES | STARTED AT: $startStr | FINISHED AT: $endStr" }
                outputStream.close()
                println("Arquivo '$fileName' recebido com sucesso! Total: $bytesRecebidos bytes.\n")
            }

        } catch (e: Exception) {
            println("Ocorreu um erro durante o recebimento: ${e.message}")
        }
    }
}
package bonfim.jordan
import java.io.FileInputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.security.MessageDigest
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.UIManager


fun main() {
    val ipDestino = "127.0.0.1"
    val portaDestino = 5090
    val socket = DatagramSocket()
    val enderecoServidor = InetAddress.getByName(ipDestino)

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    while (true){
        val choice = JOptionPane.showConfirmDialog(
            null, "Fazer upload?",
            "Confirmation", JOptionPane.YES_NO_OPTION
        )

        if(choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.NO_OPTION) break

        val selector = JFileChooser()
        val arquivo = selector.showOpenDialog(null) // Abre a janela
        if (arquivo == JFileChooser.APPROVE_OPTION) {

            var arquivoParaEnviar = selector.selectedFile
            println("Arquivo selecionado: ${arquivoParaEnviar.absolutePath}")

            if (!arquivoParaEnviar.exists()) {
                println("Erro: Arquivo não encontrado!")
                return
            }

            println("Iniciando envio do arquivo")

            // Enviar o tamanho e nome do arquivo
            val tamanhoArquivo:String = arquivoParaEnviar.length().toString()

            val nomeArquivo = arquivoParaEnviar.name
            var bytesEnvio = (tamanhoArquivo+"%"+nomeArquivo).toByteArray()

            var pacote = DatagramPacket(bytesEnvio, bytesEnvio.size, enderecoServidor, portaDestino)
            socket.send(pacote)


            Thread.sleep(100)

            //  Enviar as partes do arquivo (1024 bytes por vez)
            val inputStream = FileInputStream(arquivoParaEnviar)
            val buffer = ByteArray(1024)
            val digest = MessageDigest.getInstance("SHA-1")

            // Barra de progresso com 50 caracteres
            var progressBar = "                                                  "


            //CALCULO PARA BARRA DE PROGRESSO
            val totalProgressParts = 50
            var totalPackages = tamanhoArquivo.toInt()/1024
            if(totalPackages.toInt() ==0) totalPackages=1

            var packagesPerProgresssPart = 1

            if(totalPackages>50){
                packagesPerProgresssPart = (totalPackages/totalProgressParts).toInt()
            }

            println()
            var packageNumber: Long = 0

            var percentage = 0

            println("Progress:")

            while (true) {
                val bytesLidos = inputStream.read(buffer)
                if(bytesLidos == -1) break

                digest.update(buffer, 0, bytesLidos)
                val checksum = digest.digest()

                // Dados + Checksum
                val pacoteFinal = ByteArray(bytesLidos + checksum.size)
                System.arraycopy(buffer, 0, pacoteFinal, 0, bytesLidos)
                System.arraycopy(checksum, 0, pacoteFinal, bytesLidos, checksum.size)

                // Envia o pacote completo
                val pacoteDados = DatagramPacket(pacoteFinal, pacoteFinal.size, enderecoServidor, portaDestino)
                socket.send(pacoteDados)

                packageNumber+=1

                if((packageNumber%packagesPerProgresssPart).toInt() == 0  ){
                    progressBar = progressBar.replaceFirst(">", "=")
                    progressBar = progressBar.replaceFirst(" ", ">")

                }

                percentage = (packageNumber*100/totalPackages).toInt()
                print("|$progressBar| $percentage% \r")

                Thread.sleep(0,5)
            }

            inputStream.close()
            println("Envio concluído!")
        }
    }
    socket.close()
}

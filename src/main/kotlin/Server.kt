import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


abstract class Server(private val port: Int){

    private var server: ServerSocket? = null
    private var socket: Socket? = null

    private val connections: HashMap<Pair<String, Int>, EchoThread> = HashMap()

    var isRunning: Boolean = true

    fun start(){
        try {
            server = ServerSocket(port)
        }catch (e: IOException){
            e.printStackTrace()
        }

        while (isRunning){
            try {
                socket = server?.accept()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val echoThread: EchoThread = EchoThread(socket!!, this)
            echoThread.start()

            connections[Pair(socket!!.inetAddress.toString(), socket!!.port)] = echoThread

            //----------
            val data: String = Scanner(socket?.getInputStream()!!, "UTF-8").useDelimiter ("\\r\\n\\r\\n").next()
            val get: Matcher = Pattern.compile("^GET").matcher(data)

            if (get.find()) {
                val match: Matcher = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data)
                match.find()

                val response: ByteArray= ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1")
                        .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
                )
                        + "\r\n\r\n").toByteArray()

                socket?.getOutputStream()!!.write(response, 0, response.size)
            }
            //-------------
            this.processNewConnection(socket!!.inetAddress.toString(), socket!!.port)
        }
    }

    fun sendMessage(clientIpAddress: String, clientPort: Int, message: String){
        this.connections[Pair(clientIpAddress, clientPort)]?.sendMessage(message)
    }

    abstract fun processMessage(clientIpAddress: String, clientPort: Int, message: String)

    abstract fun processClosingConnection(clientIpAddress: String, clientPort: Int)

    abstract fun processNewConnection(clientIpAddress: String, clientPort: Int)
}

class EchoThread(private var socket: Socket, private var server: Server): Thread(){

    var inp: InputStream? = null
    var br: BufferedReader? = null
    var output: DataOutputStream? = null

    var isRunning: Boolean = true

    init {
        try {
            inp = socket.getInputStream()
            br = BufferedReader(InputStreamReader(inp!!))
            output= DataOutputStream(socket.getOutputStream())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        while (isRunning) {
            try {
                val content = br?.readLine()
                if (content == null || content.equals("exit", ignoreCase = true)) {
                    server.processClosingConnection(this.socket.inetAddress.toString(), this.socket.port)
                    return
                } else {
                    val decoded = ByteArray(6)
                    val encoded =
                        byteArrayOf(198.toByte(), 131.toByte(), 130.toByte(), 182.toByte(), 194.toByte(), 135.toByte())
                    val key = byteArrayOf(167.toByte(), 225.toByte(), 225.toByte(), 210.toByte())
                    for (i in encoded.indices) {
                        decoded[i] = (encoded[i]).toByte()
                    }

                    this.server.processMessage(this.socket.inetAddress.toString(), this.socket.port, content)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
    }

    fun sendMessage(message: String): Boolean{
        return if(output != null) {
            output!!.writeUTF(message)
            true
        }else
            false
    }

    fun end(){
        isRunning = false
    }
}
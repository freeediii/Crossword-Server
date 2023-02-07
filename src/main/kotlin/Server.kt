import java.io.*
import java.net.ServerSocket
import java.net.Socket


abstract class Server(private val port: Int){

    private var server: ServerSocket? = null
    private var socket: Socket? = null

    private val connections: HashMap<Pair<String, Int>, EchoThread> = HashMap()

    var isRunning: Boolean = false

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
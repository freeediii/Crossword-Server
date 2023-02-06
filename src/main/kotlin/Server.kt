import java.io.ObjectInputStream
import java.net.ServerSocket
import java.net.Socket

abstract class Server(private val port: Int): Thread(), Runnable {
    val server: ServerSocket = ServerSocket(port)

    //val connections: HashMap<Int, >

    var isRunning: Boolean = false

    override fun run(){
        while (true){
            val socket: Socket = server.accept()

            val input: ObjectInputStream = ObjectInputStream(socket.getInputStream())
            val inputString: String = input.readUTF()

            println(inputString)


        }
    }
}
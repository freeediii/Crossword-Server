import java.util.*
import kotlin.collections.HashMap

class CrosswordServer: Server(649649) {

    private val soloGames: LinkedList<CrosswordSoloGame> = LinkedList()
    private val multiGames: LinkedList<CrosswordMultiplayerGame> = LinkedList()

    private val onlineMember: HashMap<Int, Pair<String, Int>> = HashMap()

    override fun processMessage(clientIpAddress: String, clientPort: Int, message: String) {
        TODO("Not yet implemented")
    }

    override fun processClosingConnection(clientIpAddress: String, clientPort: Int) {
        TODO("Not yet implemented")
    }

    override fun processNewConnection(clientIpAddress: String, clientPort: Int) {
        TODO("Not yet implemented")
    }

    private fun createId(): Int{

    }
}
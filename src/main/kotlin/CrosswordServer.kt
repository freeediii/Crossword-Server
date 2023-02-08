import org.json.JSONObject
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.HashMap

class CrosswordServer: Server(649649) {

    private val soloGames: LinkedList<CrosswordSoloGame> = LinkedList()
    private val multiGames: LinkedList<CrosswordMultiplayerGame> = LinkedList()

    private val onlineMember: HashMap<Pair<String, Int>, OnlinePlayer> = HashMap()

    init {

    }

    override fun processMessage(clientIpAddress: String, clientPort: Int, message: String) {
        val json = JSONObject(message)

        when(json.getString("messageType")){
            "GAME"->{
                when (json.getString("ISSUE")){
                    "STARTSOLO" ->{
                        try {
                            val size: Int = (json.getString("SIZE")).toInt()

                            val game: CrosswordSoloGame = CrosswordSoloGame(onlineMember[Pair(clientIpAddress, clientPort)]!!, size)

                            this.soloGames.add(game)
                            onlineMember[Pair(clientIpAddress, clientPort)]!!.currentGame = game
                        }catch (e: NumberFormatException){
                            onlineMember[Pair(clientIpAddress, clientPort)]!!.sendMessage("")
                            e.printStackTrace()
                        }
                    }
                }
            }

        }
    }

    override fun processClosingConnection(clientIpAddress: String, clientPort: Int) {
        TODO("Not yet implemented")
    }

    override fun processNewConnection(clientIpAddress: String, clientPort: Int) {
        onlineMember[Pair(clientIpAddress, clientPort)] = OnlinePlayer(this, createId(), clientIpAddress, clientPort)
    }

    private fun createId(): Int{
        val id: Int = (Math.random()*100000000).toInt()

        for (onlinePlayer in onlineMember.values) {
            if(onlinePlayer.id == id){
                return createId()
            }
        }
        return id
    }
}
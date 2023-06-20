import org.json.JSONObject
import java.lang.NumberFormatException
import java.security.MessageDigest
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap

class CrosswordServer: Server(42649) {

    private val soloGames: LinkedList<CrosswordSoloGame> = LinkedList()
    private val multiGames: LinkedList<CrosswordMultiplayerGame> = LinkedList()

    private val onlineMember: HashMap<Pair<String, Int>, OnlinePlayer> = HashMap()

    init {

    }

    override fun processMessage(clientIpAddress: String, clientPort: Int, message: String) {
        println(message)
        val json = JSONObject(message)


        when(json.getString("messageType")){
            "GAMESTART"->{
                when (json.getString("issue")){
                    "STARTSOLO" ->{
                        try {
                            val size: Int = (json.getString("size")).toInt()

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
        println("User joined: $clientIpAddress $clientPort")
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
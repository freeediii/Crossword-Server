class OnlinePlayer(val server: Server, val id: Int, val ipAddress: String, val port: Int) {

    var currentGame: CrosswordGame? = null

    fun sendMessage(message: String){
        server.sendMessage(this.ipAddress, this.port, message)
    }
}
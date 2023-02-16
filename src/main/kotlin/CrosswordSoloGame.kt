class CrosswordSoloGame(val player: OnlinePlayer,
                        size: Int): CrosswordGame(size) {

    init {
        this.player.sendMessage("{\"messageType\": \"GAME\", \"gameType\": \"SOLO\", \"questions\": {\"${this.questionPool.joinToString("\", \"")}\"}}")
    }
}
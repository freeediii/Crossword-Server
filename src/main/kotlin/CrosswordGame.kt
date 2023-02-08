import java.util.*

abstract class CrosswordGame(val size: Int) {
    val questionPool: LinkedList<Pair<String, String>> = LinkedList()

    init {
        for(i in 0 until size){
            val q: Pair<String, String>? = getRandomQuestion()
            if(q != null)
                questionPool.add(q)
        }
    }
}
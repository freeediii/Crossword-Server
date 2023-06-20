import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

private var connection: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crossword?user=master&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useUnicode=yes")
private var statement: Statement = connection.createStatement()


fun getRandomQuestion(): Pair<String, String>?{
    val rslt: ResultSet = statement.executeQuery("SELECT term.word, term3.word as 'original' " +
                                                     "FROM term, synset, term term2, (SELECT word " +
                                                                                     "FROM term " +
                                                                                     "ORDER BY RAND() LIMIT 1) as term3 " +
                                                     "WHERE synset.is_visible = 1 " +
                                                         "AND synset.id = term.synset_id " +
                                                         "AND term2.synset_id = synset.id " +
                                                         "AND term2.word = term3.word;")

    if(!rslt.next())
        return null

    val questionPool: LinkedList<Pair<String, String>> = LinkedList()

    questionPool.add(Pair(rslt.getString("word"), rslt.getString("original")))

    while(!rslt.next() && (rslt.getString("word") != rslt.getString("original")))
        questionPool.add(Pair(rslt.getString("word"), rslt.getString("original")))

    for (questions in questionPool){
        return if(questions.first.contains(" "))
            if(questions.second.contains(" "))
                continue
            else
                Pair(questions.first, questions.second)
        else
            if(questions.second.contains(" "))
                Pair(questions.second, questions.first)
            else
                if(Math.random() < 0.5)
                    Pair(questions.first, questions.second)
                else
                    Pair(questions.second, questions.first)
    }

    return null
}


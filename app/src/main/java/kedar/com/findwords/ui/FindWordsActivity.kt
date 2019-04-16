package kedar.com.findwords.ui

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import kedar.com.findwords.R
import kedar.com.findwords.interfaces.IWordValidator
import kedar.com.findwords.models.BoardGame
import kedar.com.findwords.models.ColumnRowPair
import kedar.com.findwords.models.SelectedWord
import kedar.com.findwords.puzzleuielements.GridRow
import kedar.com.findwords.puzzleuielements.PuzzleGridRecycleAdapter
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class FindWordsActivity : AppCompatActivity(), IWordValidator {

    lateinit var headerWord :  TextView

    lateinit var attemptsText :TextView

    lateinit var recyclerView: CustomRecyclerView

    var totalGames = 0

    var currentIndex = 0

    val jsonArray = JSONArray()

    lateinit var currentGame: BoardGame


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_words)
    }

    init {

        val url = baseUrl
            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    val body = response?.body()?.string()
                    val array = body?.split(split_by)
                    if(array!=null) {
                        for (element in array) {
                            if(!element.isBlank()&& !element.isEmpty())
                                jsonArray.put(JSONObject(element+ split_by))
                        }
                    }
                    runOnUiThread { initView() }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    e?.printStackTrace()
                }
            })
    }

    private fun initView(){
        totalGames = jsonArray.length()
        headerWord = findViewById(R.id.section_label)
        attemptsText = findViewById(R.id.attempts_left)
        recyclerView = findViewById(R.id.gridRecyclerView)
        checkForTheNextGame()
    }

    private fun checkForTheNextGame(){
        if(currentIndex < totalGames){
            currentGame = BoardGame(jsonArray[currentIndex++] as JSONObject)
            setTheBoard(currentGame)
        }
    }

    private fun setTheBoard(game: BoardGame){
        setHeader(game.word)
        setupGridRecyclerView(game.gridRows)
        setAttempts(game.totalPuzzles)
    }

    private fun setHeader(title: String){
        headerWord.text = title
    }

    private fun setAttempts(attempts:Int){
        attemptsText.text = String.format(this.getString(R.string.attempts),attempts)
    }
    private fun setupGridRecyclerView(grid: List<GridRow>){
        recyclerView.layoutManager = GridLayoutManager(this, grid.size)
        recyclerView.wordValidator = this
        recyclerView.gridRowSize = grid.size
        recyclerView.boardLocked = false
        val puzzleAdapter = PuzzleGridRecycleAdapter(this)
        recyclerView.adapter = puzzleAdapter
        puzzleAdapter.setGridLetters(grid, grid.size)
    }

    override fun validateWord(selectedWord: SelectedWord):Boolean {
        val listToCompare = ArrayList<ColumnRowPair>()
        for(selectedLetter in selectedWord.selectedLetters.iterator()){
            listToCompare.add(ColumnRowPair(selectedLetter.col,selectedLetter.row))
        }
        for(wordLocation in currentGame.wordLocations.keys){
            if(!currentGame.wordLocations[wordLocation]!! && listToCompare.size == wordLocation.colRowPairs.size
                && listToCompare.containsAll(wordLocation.colRowPairs)){
                currentGame.solvedPuzzles++
                setAttempts(currentGame.totalPuzzles - currentGame.solvedPuzzles)
                currentGame.wordLocations[wordLocation] = true
                return true
            }

        }
        return false
    }

    override fun isGameComplete(): Boolean {
       return currentGame.totalPuzzles == currentGame.solvedPuzzles
    }

    override fun notifyRightAnswersSelected() {
        runOnUiThread {
            checkForTheNextGame()
        }
    }

    companion object{
        const val split_by ="\"target_language\": \"es\"}"
        const val baseUrl = "https://s3.amazonaws.com/duolingo-data/s3/js2/find_challenges.txt"
    }

}

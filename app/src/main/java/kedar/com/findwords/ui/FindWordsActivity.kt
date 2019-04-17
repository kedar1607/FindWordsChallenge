package kedar.com.findwords.ui

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kedar.com.findwords.R
import kedar.com.findwords.interfaces.DownloadServiceCallback
import kedar.com.findwords.interfaces.GamePlayValidator
import kedar.com.findwords.models.BoardGame
import kedar.com.findwords.models.ColumnRowPair
import kedar.com.findwords.models.SelectedWord
import kedar.com.findwords.network.DownloadGameService
import kedar.com.findwords.models.GridRow
import java.util.*

import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * This activity is responsible for calling the DownloadGameService, handling it's callbacks and depending
 * on the result of the callback set all the UI components for the game. It also implements GamePlayValidator
 * which is responsible for notifying the game-play about valid selections, game completions.
 *
 * Other than what mentioned above, it is also doing work of managing all different UI components for the
 * game play screen.
 */
class FindWordsActivity : AppCompatActivity(), GamePlayValidator, DownloadServiceCallback {

    /**
     *TextView for the word to be translated
     */
    private lateinit var headerWord :  TextView

    /**
     * TextView for the numberof words to be found per challenge
     */
    private lateinit var attemptsText :TextView

    /**
     *  RCV used for loading a grid of letters and receive touch events
     */
    private lateinit var recyclerView: CustomRecyclerView

    /**
     *Layout with animation components
     */
    private lateinit var loadingProgress: LinearLayout

    /**
     * TextView for Loading view text message
     */
    private lateinit var tvLoadingText : TextView

    /**
     * Gameplay layout
     */
    private lateinit var challengeLayout: LinearLayout

    /**
     * button to start over all the challenges
     */
    private lateinit var startOver: Button

    /**
     * Loading animator
     */
    private lateinit var progressIndicator: ProgressBar

    /**
     * to track total challenges in the game
     */
    private var totalGames = 0

    /**
     * to track currently loaded challenge
     */
    private var currentIndex = 0

    /**
     * List of all the challenges
     */
    private val boardGames = ArrayList<BoardGame>()

    /**
     * current challenge
     */
    lateinit var currentGame: BoardGame


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_words)
        initViews()
    }

    /**
     * UI components not required to be initialized to download data
     */
    init {
        startDownloadService()
    }

    /**
     * Initialize all the UI components
     */
    private fun initViews(){
        headerWord = findViewById(R.id.section_label)
        attemptsText = findViewById(R.id.attempts_left)
        recyclerView = findViewById(R.id.gridRecyclerView)
        loadingProgress = findViewById(R.id.downloading)
        tvLoadingText = findViewById(R.id.tv_loading_progress)
        challengeLayout = findViewById(R.id.challenge_layout)
        startOver = findViewById(R.id.start_over)
        progressIndicator = findViewById(R.id.loading_indicator)
        startOver.setOnClickListener{
            startOver()
        }
    }

    /**
     * This method is used to start over all the challenges
     */
    private fun startOver(){
        tvLoadingText.text = getString(R.string.downloading)
        startOver.visibility = GONE
        progressIndicator.visibility = VISIBLE
        currentIndex = 0
        totalGames = 0
        boardGames.clear()
        startDownloadService()
    }

    /**
     * Start the service to download data for all the challenges
     */
    private fun startDownloadService(){
        DownloadGameService(this, this)
    }

    /**
     * Once data is downloaded start the challenge using this method
     */
    private fun startChallenge(){
        stopLoadingAnimation()
        totalGames = boardGames.size
        checkForTheNextGame()
    }

    /**
     * Check if the next challenge is available, otherwise show startover button
     */
    private fun checkForTheNextGame(){
        if(currentIndex < totalGames){
            animateLoadingNext()
            Timer(false).schedule(DELAY_FOR_NEXT_CHALLENGE) {
                runOnUiThread {
                    stopLoadingAnimation()
                    currentGame = boardGames[currentIndex++]
                    setTheBoard(currentGame)
                }
            }
        }else{
            startOver.text = getString(R.string.start_over)
            startOver.visibility = VISIBLE
        }
    }

    /**
     * Intentional animation before start of next challenge
     */
    private fun animateLoadingNext(){
        challengeLayout.visibility = GONE
        loadingProgress.visibility = VISIBLE
        tvLoadingText.text = getString(R.string.loading_next)
    }

    /**
     * stop animation after a certain delay and show the challenge UI components
     */
    private fun stopLoadingAnimation(){
        challengeLayout.visibility = VISIBLE
        loadingProgress.visibility = GONE
    }

    /**
     * set the board UI for current challenge
     * [game] current challenge being played
     */
    private fun setTheBoard(game: BoardGame){
        setHeader(game.word)
        setupGridRecyclerView(game.gridRows)
        setAttempts(game.totalPuzzles)
    }


    /**
     * Set the word for translation
     * [title] word for translation
     */
    private fun setHeader(title: String){
        headerWord.text = title
    }

    /**
     * set the challenge board (grid) with recycler view and it's adapter
     * [grid] List of all the rows in the grid
     */
    private fun setupGridRecyclerView(grid: List<GridRow>){
        recyclerView.layoutManager = GridLayoutManager(this, grid.size)
        recyclerView.gamePlayValidator = this
        //Row size is needed to calculate the item height/width at run time
        recyclerView.gridRowSize = grid.size
        recyclerView.boardLocked = false
        //grid size is needed to calculate the item height/width and text size at run time
        val puzzleAdapter = PuzzleGridRecycleAdapter(this, grid.size)
        recyclerView.adapter = puzzleAdapter
        puzzleAdapter.setGridLetters(grid)
    }

    /**
     * set number of words left to be selected to answer all translations
     * [attempts] number of words left to be selected
     */
    private fun setAttempts(attempts:Int){
        if(attempts > 0) {
            attemptsText.text = String.format(resources.getQuantityString(R.plurals.attempts, attempts), attempts)
            attemptsText.setTextColor(ContextCompat.getColor(this,R.color.colorAccent))
        }else{
            attemptsText.text = resources.getString(R.string.matches_found)
            attemptsText.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }
    }

    /**
     * validate the selected word formed from multiple grid items in certain direction.
     * This method checks if all the different row-col indexes are included in the current selection which
     * are also part of word location in an array of wordlocations in currently set game board
     * [selectedWord] selected word that contains list of LetterTile and which will be validated agains word-
     * locations of currently playing game board.
     *
     * @return true If the word is validated (and not previously validated), false otherwise
     */
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

    /**
     * notifies the grid recycler view about the game completion so it can stop all touches to the grid
     * @return true if all word locations are validated correctly, false otherwise
     */
    override fun isGameComplete(): Boolean {
       return currentGame.totalPuzzles == currentGame.solvedPuzzles
    }

    /**
     * Call back from the grid recycler view to notify that the current challenge is fully answered
     * and waited for the delay that was set before next challenge is shown to the user.
     * After the delay, check for the next available challenge
     */
    override fun notifyRightAnswersSelected() {
        runOnUiThread {
            checkForTheNextGame()
        }
    }

    /**
     * call back from the DownloadGameService that the download of challenges data is successful with the
     * list of all Board games
     * [boardGames] list of all the board games
     */
    override fun onSuccess(boardGames: List<BoardGame>) {
        this.boardGames.addAll(boardGames)
        runOnUiThread { startChallenge() }
    }

    /**
     * callback from the DownloadGameService that the download of the data has failed for some reason.
     * This method also sets the error message for an end user to let them know that there was something wrong
     * and an option for them to retry the game in case failure was due to no network connection
     * [message] message to the end user
     */
    override fun onFailure(message: String) {
        runOnUiThread {
            progressIndicator.visibility = GONE
            startOver.visibility = VISIBLE
            startOver.text = getString(R.string.retry)
            tvLoadingText.text = getString(R.string.download_failed)
        }
    }

    companion object{
        /**
         * Delay for showing the animation before next challenge
         */
        const val DELAY_FOR_NEXT_CHALLENGE = 750L
    }

}

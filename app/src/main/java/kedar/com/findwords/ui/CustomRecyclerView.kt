package kedar.com.findwords.ui

import android.content.Context
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.core.content.ContextCompat
import kedar.com.findwords.R
import kedar.com.findwords.interfaces.IWordValidator
import kedar.com.findwords.models.LetterTile
import kedar.com.findwords.models.SelectedWord
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.concurrent.schedule


class CustomRecyclerView : RecyclerView, RecyclerView.OnItemTouchListener {
    private var mTouchSlope: Int = 0

    //To keep track of the selected tiles so that they won't be selected again for any other selection
    var tilesForCorrectAnswers = HashSet<LetterTile>()

    //To form a word with the tiles (SelectedLetters) and eventually send it out to the Word Validator for validating
    var selectedWord: SelectedWord? = null

    //Grid row size is needed for indexing purposes
    var gridRowSize = 0

    //To stop all the interactions to the board once all matching words are found
    var boardLocked = false

    //word validator is used to validate the selected word from the source of the game board data (in this case it's Find Words Activity)
    lateinit var wordValidator: IWordValidator

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        val vc = ViewConfiguration.get(context)
        mTouchSlope = vc.scaledTouchSlop
    }

    /**
     * When the selection of the word is being made, get the tiles in between the first and the last tile touched upon
     * [startLetterTile]  is the first tile that was selected
     * [endLetterTile] is the final tile that was selected
     * @return the list of selected letters for the current selection
     */
    private fun getTilesBetween(startLetterTile: LetterTile?, endLetterTile: LetterTile): List<LetterTile> {
        val tiles = ArrayList<LetterTile>()
        val direction = startLetterTile?.getDirection(endLetterTile)
        var currPoint = startLetterTile
        if (direction != DIRECTION_UNKNOWN) {
            while (currPoint!=null && currPoint != endLetterTile) {
                //Moves the selection in the existing direction with 1 block at a time
                currPoint = moveSelection(currPoint, direction, 1)
                val t = LetterTile(currPoint.row, currPoint.col, gridRowSize)
                //If the selection is already made for the specific tile t, just stop the selection there
                if (isAlreadySelected(t)) {
                    break
                } else {
                    tiles.add(t)
                }
            }
        }
        return tiles
    }

    /**
     * Moves the selection from a given currentLetterTile (tile) in given direction numberOfMoves times
     * [currentLetterTile] Given starting currentLetterTile (end of last selection)
     * [direction] direction in which selection should be moved
     * [numberOfMoves] number of moves to be made in given direction
     * @return next tile / currentLetterTile in the grid to be selected
     */
    private fun moveSelection(currentLetterTile: LetterTile, direction: Int?, numberOfMoves: Int): LetterTile {
        return when (direction) {
            DIRECTION_TOP_TO_BOTTOM -> LetterTile(currentLetterTile.row + numberOfMoves, currentLetterTile.col, gridRowSize)
            DIRECTION_BOTTOM_TO_TOP -> LetterTile(currentLetterTile.row - numberOfMoves, currentLetterTile.col, gridRowSize)
            DIRECTION_LEFT_TO_RIGHT -> LetterTile(currentLetterTile.row, currentLetterTile.col + numberOfMoves, gridRowSize)
            DIRECTION_RIGHT_TO_LEFT -> LetterTile(currentLetterTile.row, currentLetterTile.col - numberOfMoves, gridRowSize)
            DIRECTION_TOP_BOTTOM_LEFT_RIGHT -> LetterTile(currentLetterTile.row + numberOfMoves, currentLetterTile.col + numberOfMoves, gridRowSize)
            DIRECTION_BOTTOM_TOP_RIGHT_LEFT -> LetterTile(currentLetterTile.row - numberOfMoves, currentLetterTile.col - numberOfMoves, gridRowSize)
            else -> currentLetterTile
        }
    }


    /**
     * Updates the selection depending on the [tiles] that are provided. For each tile, it checks if it's already isCorrectSelection or
     * it's a candidate for the selection / pressure
     *  [tiles] Set of all the unique tiles (selectedLetter objects) on the game board
     *  [selected] will be true if the user is in the process of selection and hasn't lifted finger after touch, false otherwise
     *  [isCorrectSelection] will be true if the currently isCorrectSelection word ios already validated and is valid, false otherwise
     */
    private fun updateSelection(tiles: HashSet<LetterTile>?, selected: Boolean, isCorrectSelection: Boolean) {
        if(tiles!=null) {
            for (tile in tiles) {
                // We can get the child view depending on th row number, column number and the grid size
                val rootView = getChildAt(tile.row * gridRowSize + tile.col)
                val textView = rootView.findViewById<TextView>(R.id.letterTile)

                val backgroundColor = when {
                    selected || isCorrectSelection || isAlreadySelected(tile) -> if(isCorrectSelection || isAlreadySelected(tile)){
                        R.color.selection
                    } else {
                        R.color.pressed
                    }
                    else -> R.color.white
                }

                val textColor = when {
                    selected || isCorrectSelection || isAlreadySelected(tile) -> R.color.white
                    else -> R.color.dark
                }

                textView.setBackgroundColor(ContextCompat.getColor(context,backgroundColor))
                textView.setTextColor(ContextCompat.getColor(context,textColor))
            }
        }
    }


    override fun onTouchEvent(e: MotionEvent?): Boolean {
        val X = e?.getX()
        val Y = e?.getY()
        var row: Int = -1
        var col: Int = -1

        // Get relative row and column depending on the X and Y
        if(Y!=null)
             row = (Y / pxFromDp(context, dp)).toInt()
        if(X!=null)
            col = (X / pxFromDp(context, dp)).toInt()

        // By using column and row number above, get the child at that position
        val child = getChildAt(row * gridRowSize + col)

        //Exit on invalid touches
        if (e?.actionMasked != MotionEvent.ACTION_UP && (row >= gridRowSize   || col >= gridRowSize || child == null) || boardLocked) {
            updateSelection(selectedWord?.selectedLetters, false, false)
            return true
        }

        super.onTouchEvent(e)

        when (e?.actionMasked) {
            //Capture all the move events
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val selectedLetter = LetterTile(row, col, gridRowSize)
                //Is the first touch before selection
                if(selectedWord==null){
                    selectedWord = SelectedWord(selectedLetter)
                }
                //check if current selection is not same as the last one and is valid
                else if(selectedLetter != selectedWord?.last && selectedWord!!.isValid(selectedLetter)){
                    //Check if the selection is allowed in the given sequence of selected letters (currently selected word)
                    if(selectedWord!=null && !selectedWord?.isSelectionAllowed(selectedLetter)!!){
                        //If the selection is not allowed clear the selection
                        updateSelection(selectedWord?.selectedLetters,false,false)
                        // And move the selection to the very first selected tile
                        if(selectedWord?.selectedLetters!=null)
                             selectedWord =
                                 SelectedWord(selectedWord?.selectedLetters!!.first())
                    }
                    // if the selection is valid and is allowed, then add the selection to the existing selected tiles
                    // This process involves getting the tiles in between the last selected letter and current selected letter [selectedLetter]
                    val letters = getTilesBetween(selectedWord?.last, selectedLetter)
                    if (letters.isNotEmpty()) {
                        selectedWord?.addLetters(letters)
                    }
                }
                //Final step of updating the selection with selected status
                // All steps are repeated until user has lifted the finger after selection is made
                updateSelection(selectedWord?.selectedLetters ,true,false)
            }
            MotionEvent.ACTION_UP -> {
                if(selectedWord!=null){
                    //Check if the selected word is valid and add it (all it's tiles/selected letters) to the selected tiles / letters
                    val isValid = wordValidator.validateWord(selectedWord!!)
                    if(isValid){
                        addToSelectedTiles(selectedWord!!)
                    }
                    // This updates the selection from just selected to confirmed for correct answer
                    updateSelection(selectedWord?.selectedLetters, false,isValid )
                    // If the game is complete, we lock the board for any further touches until new game is loaded

                    if(isValid && wordValidator.isGameComplete()){
                        tilesForCorrectAnswers.clear()
                        boardLocked = true
                        Timer(false).schedule(DELAY_BEFORE_NEXT_GAME) {
                            // notify the source / word validator that all the right answers are matched after a delay of 1 second
                            wordValidator.notifyRightAnswersSelected()
                        }
                    }
                }
                // Reset the selected word for the next selection if any
                selectedWord = null
            }

        }
        return true
    }

    /**
     * Add letters from the selected word to the selected tiles, so that next selection shouldn't change this selection
     * [selectedWord] selected valid word formed
     * [tilesForCorrectAnswers] Please refer above in the member variables
     */
    private fun addToSelectedTiles(selectedWord: SelectedWord){
        tilesForCorrectAnswers.addAll(selectedWord.selectedLetters)
    }

    /**
     * This method checks if the current tile / letter is already selected in the method above and returns true, false otherwise
     * [letterTileTile] a letter / tile that will be checked for the selection
     */
    private fun isAlreadySelected(letterTileTile: LetterTile):Boolean{
       return tilesForCorrectAnswers.contains(letterTileTile)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object{
        const val DIRECTION_LEFT_TO_RIGHT = 0
        const val DIRECTION_TOP_TO_BOTTOM = 1
        const val DIRECTION_TOP_BOTTOM_LEFT_RIGHT = 2
        const val DIRECTION_RIGHT_TO_LEFT = 3
        const val DIRECTION_BOTTOM_TO_TOP = 4
        const val DIRECTION_BOTTOM_TOP_RIGHT_LEFT = 5
        const val DIRECTION_UNKNOWN = -1

        const val dp = 40.0f

        const val DELAY_BEFORE_NEXT_GAME = 1000L

        private fun pxFromDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

    }
}
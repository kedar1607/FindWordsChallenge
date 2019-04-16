package kedar.com.findwords.ui

import android.content.Context
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.core.content.ContextCompat
import kedar.com.findwords.R
import kedar.com.findwords.interfaces.IWordValidator
import kedar.com.findwords.models.SelectedLetter
import kedar.com.findwords.models.SelectedWord
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.concurrent.schedule


class CustomRecyclerView : RecyclerView, RecyclerView.OnItemTouchListener {
    private var mTouchSlope: Int = 0
//    var selectedWords = ArrayList<SelectedWord>()
    var selectedTiles = HashSet<SelectedLetter>()
    var selectedWord: SelectedWord? = null
    var gridRowSize = 0
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

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


//    private fun isTileSelected(tile: SelectedLetter, direction: Int?): Boolean {
//        for (word in selectedWords) {
//            //A selected tile cannot be selected again for the same selection type
//            if (direction == DIRECTION_UNKNOWN || word.direction == direction) {
//                for (wordTile in word.selectedLetters) {
//                    //Check also the previous tile in the same direction to prevent connected
//                    //selected tiles for different words from happening
//                    if (wordTile.equals(tile) || wordTile.equals(shift(tile, direction, -1))) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }

    private fun getTilesBetween(startLetter: SelectedLetter?, endLetter: SelectedLetter): List<SelectedLetter> {
        val tiles = ArrayList<SelectedLetter>()
        val direction = startLetter?.getDirection(endLetter)
        var currPoint = startLetter
        if (direction != DIRECTION_UNKNOWN) {
            while (currPoint!=null && !currPoint.equals(endLetter)) {
                currPoint = shift(currPoint, direction, 1)
                val t = SelectedLetter(currPoint.row, currPoint.col, gridRowSize)
                if (isAlreadySelected(t)) {
                    break
                } else {
                    tiles.add(t)
                }
            }
        }
        return tiles
    }


    private fun shift(point: SelectedLetter, direction: Int?, n: Int): SelectedLetter {
        if (direction == DIRECTION_TOP_TO_BOTTOM) {
            return SelectedLetter(point.row + n, point.col, gridRowSize)
        }

        else if(direction == DIRECTION_BOTTOM_TO_TOP){
            return SelectedLetter(point.row - n, point.col, gridRowSize)
        }

        else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            return SelectedLetter(point.row, point.col + n, gridRowSize)
        }

        else if (direction == DIRECTION_RIGHT_TO_LEFT) {
            return SelectedLetter(point.row, point.col - n, gridRowSize)
        }

        else if (direction == DIRECTION_TOP_BOTTOM_LEFT_RIGHT) {
            return SelectedLetter(point.row + n, point.col + n, gridRowSize)
        }

//        else if (direction == DIRECTION_BOTTOM_TOP_LEFT_RIGHT) {
//            return SelectedLetter(point.row + n, point.col - n, gridRowSize)
//        }
        return point
    }

    private fun updateTiles(tiles: HashSet<SelectedLetter>?, pressed: Boolean, selected: Boolean) {
        if(tiles!=null) {
            for (tile in tiles) {
                val rootView = getChildAt(tile.row * gridRowSize + tile.col)
                val view = rootView.findViewById<TextView>(R.id.letter)
                val backgroundColor =  if(pressed||selected|| isAlreadySelected(tile)){
                    if(selected || isAlreadySelected(tile))R.color.selection
                    else R.color.pressed
                }else{
                    R.color.white
                }

                val textColor =  if(pressed||selected|| isAlreadySelected(tile)){
                   R.color.white
                }else{
                    R.color.dark
                }
                view.setBackgroundColor(ContextCompat.getColor(context,backgroundColor))
                view.setTextColor(ContextCompat.getColor(context,textColor))
            }
        }
    }


    override fun onTouchEvent(e: MotionEvent?): Boolean {
        val X = e?.getX()
        val Y = e?.getY()
        var row: Int = -1
        var col: Int = -1

        if(Y!=null)
             row = (Y / pxFromDp(context,40.0f)).toInt()
        if(X!=null)
         col = (X / pxFromDp(context,40.0f)).toInt()

        Log.i("touch_event",row.toString() + " " +col.toString())

        val child = getChildAt(row * gridRowSize + col)

        //Exit on invalid touches
        if (e?.getActionMasked() != MotionEvent.ACTION_UP && (row >= gridRowSize   || col >= gridRowSize || child == null)) {
            updateTiles(selectedWord?.selectedLetters, false, false)
            return true
        }

        super.onTouchEvent(e)

        when (e?.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val selectedLetter = SelectedLetter(row, col, gridRowSize)
                if(selectedWord==null){
                    selectedWord = SelectedWord(selectedLetter)
                }else if(selectedLetter != selectedWord?.last && selectedWord!!.isValid(selectedLetter)){
                    if(selectedWord!=null && !selectedWord?.isSelectionAllowed(selectedLetter)!!){
                        updateTiles(selectedWord?.selectedLetters,false,false)
                        if(selectedWord?.selectedLetters!=null)
                             selectedWord =
                                 SelectedWord(selectedWord?.selectedLetters!!.first())
                    }

                    val letters = getTilesBetween(selectedWord?.last, selectedLetter)
                    if (letters.isNotEmpty()) {
                        selectedWord?.addLetters(letters)
                    }
                }
                updateTiles(selectedWord?.selectedLetters ,true,false)
            }
            MotionEvent.ACTION_UP -> {
                if(selectedWord!=null){
                    val isValid = wordValidator.validateWord(selectedWord!!)
                    if(isValid){
                        addToSelectedTiles(selectedWord!!)
                    }
                    updateTiles(selectedWord?.selectedLetters, false,isValid )
                    if(isValid && wordValidator.isGameComplete()){
                        selectedTiles.clear()
                        Timer("SettingUp", false).schedule(1000) {
                            wordValidator.notifyRightAnswersSelected()
                        }
                    }
                }
                selectedWord = null
            }
        }
        return true
    }

    private fun addToSelectedTiles(selectedWord: SelectedWord){
        selectedTiles.addAll(selectedWord.selectedLetters)
    }

    private fun isAlreadySelected(letterTile: SelectedLetter):Boolean{
       return selectedTiles.contains(letterTile)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    companion object{
        const val DIRECTION_LEFT_TO_RIGHT = 0
        const val DIRECTION_TOP_TO_BOTTOM = 1
        const val DIRECTION_TOP_BOTTOM_LEFT_RIGHT = 2
        const val DIRECTION_RIGHT_TO_LEFT = 3
        const val DIRECTION_BOTTOM_TO_TOP = 4
//        const val DIRECTION_BOTTOM_TOP_LEFT_RIGHT = 5
//        const val DIRECTION_BOTTOM_TOP_RIGHT_LEFT = 6
//        const val DIRECTION_TOP_BOTTOM_RIGHT_LEFT = 7
        const val DIRECTION_UNKNOWN = -1

    }
}
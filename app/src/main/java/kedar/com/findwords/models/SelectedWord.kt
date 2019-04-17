package kedar.com.findwords.models

import kedar.com.findwords.ui.CustomRecyclerView

/**
 * This class represents the selected word on the grid (while in selection mode)
 * This is created with the initialLetterTile and as selection proceeds valid selected letter tiles
 * are added in [selectedLetters]
 * [last] last tile in the [selectedLetters]
 * [direction] direction in which entire selection is made
 */
class SelectedWord(initialLetterTile: LetterTile){
    /**
     * unique selected letter tiles
     */
    var selectedLetters = LinkedHashSet<LetterTile>()
    /**
     * last letter tile in [selectedLetters]
     */
    var last : LetterTile? = initialLetterTile
    /**
     * [direction] direction in which entire selection is made
     */
    var direction: Int? = CustomRecyclerView.DIRECTION_UNKNOWN

    init {
        addLetter(initialLetterTile)
    }

    /**
     * This function checks if the selection to [selection] letter tile is allowed considering the direction of the
     * last selected letter
     */
    fun isSelectionAllowed(selection: LetterTile): Boolean {
        val currentDirection = last?.getDirection(selection)
        return currentDirection != CustomRecyclerView.DIRECTION_UNKNOWN && (direction == CustomRecyclerView.DIRECTION_UNKNOWN || direction == currentDirection)
    }

    /**
     * This funtion adds individual letter tile in the [selectedLetters]
     * [letterTile] letter tile to be added in this selected word
     */
    fun addLetter(letterTile: LetterTile){
        selectedLetters.add(letterTile)
    }

    /**
     * Checks if the selection to a specific letter tile is valid by checking the direction of the first
     * selected letter
     * [letterTile] letter tile to be validated
     */
    fun isValid(letterTile: LetterTile): Boolean{
        return selectedLetters.first().getDirection(letterTile) != CustomRecyclerView.DIRECTION_UNKNOWN
    }

    /**
     * this method adds multiple letter tiles to the [selectedLetters]
     * [letterTiles] input
     * this also assigns [last] tile as letters are added
     */
    fun addLetters(letterTiles: LinkedHashSet<LetterTile>){
        if (direction == CustomRecyclerView.DIRECTION_UNKNOWN) {
            direction = last?.getDirection(letterTiles.first())
        }
        selectedLetters.addAll(letterTiles)
        last = selectedLetters.last()
    }
}

package kedar.com.findwords.models

import kedar.com.findwords.ui.CustomRecyclerView

class SelectedWord(initialLetterTile: LetterTile){
    var selectedLetters = HashSet<LetterTile>()
    var last : LetterTile? = initialLetterTile
    var direction: Int? = CustomRecyclerView.DIRECTION_UNKNOWN

    init {
        addLetter(initialLetterTile)
    }

    fun isSelectionAllowed(selection: LetterTile): Boolean {
        val currentDirection = last?.getDirection(selection)
        return currentDirection != CustomRecyclerView.DIRECTION_UNKNOWN && (direction == CustomRecyclerView.DIRECTION_UNKNOWN || direction == currentDirection)
    }

    fun addLetter(letterTile: LetterTile){
        selectedLetters.add(letterTile)
    }

    fun isValid(letterTile: LetterTile): Boolean{
        return selectedLetters.first().getDirection(letterTile) != CustomRecyclerView.DIRECTION_UNKNOWN
    }

    fun addLetters(letterTiles: List<LetterTile>){
        if (direction == CustomRecyclerView.DIRECTION_UNKNOWN) {
            direction = last?.getDirection(letterTiles.first())
        }
        selectedLetters.addAll(letterTiles)
        last = selectedLetters.last()
    }
}

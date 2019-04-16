package kedar.com.findwords.models

import kedar.com.findwords.ui.CustomRecyclerView

class SelectedWord(initialLetter: SelectedLetter){
    var selectedLetters = HashSet<SelectedLetter>()
    var last : SelectedLetter? = initialLetter
    var direction: Int? = CustomRecyclerView.DIRECTION_UNKNOWN

    init {
        addLetter(initialLetter)
    }

    fun isSelectionAllowed(selection: SelectedLetter): Boolean {
        val currentDirection = last?.getDirection(selection)
        return currentDirection != CustomRecyclerView.DIRECTION_UNKNOWN && (direction == CustomRecyclerView.DIRECTION_UNKNOWN || direction == currentDirection)
    }

    fun addLetter(letter: SelectedLetter){
        selectedLetters.add(letter)
    }

    fun isValid(letter: SelectedLetter): Boolean{
        return selectedLetters.first().getDirection(letter) != CustomRecyclerView.DIRECTION_UNKNOWN
    }

    fun addLetters(letters: List<SelectedLetter>){
        if (direction == CustomRecyclerView.DIRECTION_UNKNOWN) {
            direction = last?.getDirection(letters.first())
        }
        selectedLetters.addAll(letters)
        last = selectedLetters.last()
    }
}

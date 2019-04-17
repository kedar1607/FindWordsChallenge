package kedar.com.findwords.interfaces

import kedar.com.findwords.models.SelectedWord

/**
 * handles the callbacks for validating the words, to know if an individual game play is finished, and to notify the
 *caller about selection of all right answers
 */
interface GamePlayValidator{

    /**
     * validate the selected word formed from multiple grid items in certain direction.
     * This method checks if all the different row-col indexes are included in the current selection which
     * are also part of word location in an array of wordlocations in currently set game board
     * [selectedWord] selected word that contains list of LetterTile and which will be validated agains word-
     * locations of currently playing game board.
     *
     * @return true If the word is validated (and not previously validated), false otherwise
     */
    fun validateWord(selectedWord: SelectedWord):Boolean
    /**
     * notifies the grid recycler view about the game completion so it can stop all touches to the grid
     * @return true if all word locations are validated correctly, false otherwise
     */
    fun isGameComplete():Boolean
    /**
     * Call back from the grid recycler view to notify that the current challenge is fully answered
     * and waited for the delay that was set before next challenge is shown to the user.
     * After the delay, check for the next available challenge
     */
    fun notifyRightAnswersSelected()
}
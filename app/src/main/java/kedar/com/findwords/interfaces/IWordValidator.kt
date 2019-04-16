package kedar.com.findwords.interfaces

import kedar.com.findwords.models.SelectedWord

interface IWordValidator{
    fun validateWord(selectedWord: SelectedWord):Boolean
    fun isGameComplete():Boolean
    fun notifyRightAnswersSelected()
}
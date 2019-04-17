package kedar.com.findwords.models

/**
 * This class represents word locations for the correct translations
 * [colRowPairs] list of column-row index pairs
 * [word] correct translated word
 */
class WordLocation (val colRowPairs:List<ColumnRowPair>, val word:String){
    override fun equals(other: Any?): Boolean {
        return this.word == (other as WordLocation).word
    }

    override fun hashCode(): Int {
        return word.hashCode()
    }
}
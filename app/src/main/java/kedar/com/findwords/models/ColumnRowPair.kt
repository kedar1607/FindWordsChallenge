package kedar.com.findwords.models

/**
 * This class is used for making column - row pair a comparable object with other objects that also have similar
 * member variables (col index and row index). Those objects can be easily transformed in to this.
 * [col] column index
 * [row] row index
 */
class ColumnRowPair (val col:Int, val row:Int){
    override fun equals(other: Any?): Boolean {
        return col == (other as ColumnRowPair).col && row == other.row
    }

    override fun hashCode(): Int {
        return 31 * row + col
    }
}
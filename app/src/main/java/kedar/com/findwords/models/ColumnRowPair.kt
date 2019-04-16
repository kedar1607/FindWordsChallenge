package kedar.com.findwords.models

class ColumnRowPair (val col:Int, val row:Int){
    override fun equals(other: Any?): Boolean {
        return col == (other as ColumnRowPair).col && row == other.row
    }

    override fun hashCode(): Int {
        return 31 * row + col
    }
}
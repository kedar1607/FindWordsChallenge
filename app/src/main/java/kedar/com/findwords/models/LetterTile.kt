package kedar.com.findwords.models

import kedar.com.findwords.ui.CustomRecyclerView

class LetterTile(val row: Int, val col: Int, val gridSize: Int){

    var id = row * gridSize + col

    fun getDirection(other: LetterTile): Int{
        return if (row == other.row && col < other.col) {
            CustomRecyclerView.DIRECTION_LEFT_TO_RIGHT
        }
        else if(row == other.row && col > other.col) {
            CustomRecyclerView.DIRECTION_RIGHT_TO_LEFT
        }
        else if (row < other.row && col == other.col) {
            CustomRecyclerView.DIRECTION_TOP_TO_BOTTOM
        }
        else if (row > other.row && col == other.col) {
            CustomRecyclerView.DIRECTION_BOTTOM_TO_TOP
        }
        else if (row < other.row
            && col < other.col
            && row - other.row == col - other.col
        ){
            CustomRecyclerView.DIRECTION_TOP_BOTTOM_LEFT_RIGHT
        }
        else if(row > other.row
            && col > other.col
            && row - other.row == col - other.col){
            CustomRecyclerView.DIRECTION_BOTTOM_TOP_RIGHT_LEFT
        }

        else {
            CustomRecyclerView.DIRECTION_UNKNOWN
        }
    }

    override fun equals(other: Any?): Boolean{
        return id == (other as LetterTile).id
    }

    override fun hashCode(): Int {
        return 31 * row + col
    }

}
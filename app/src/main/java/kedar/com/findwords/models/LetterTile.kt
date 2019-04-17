package kedar.com.findwords.models

import kedar.com.findwords.ui.CustomRecyclerView

/**
 * This class represents the single letter tile in the grid of challenge
 * [row] row index
 * [col] col index
 * [gridSize] grid size to find unique ids
 */
class LetterTile(val row: Int, val col: Int, val gridSize: Int){

    var id = row * gridSize + col

    /**
     * This function finds the direction from this letter tile to [other].
     * Finding the direction is crucial step in determining the valid selection of words in the grid.
     * This method is called multiple times as finger is moved on the grid for making selection
     * [other] other tile to which direction is found and returned
     * @return returns direction
     */
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
package kedar.com.findwords.models

/**
 * This class represents each row of the grid with the row number. List of such grid rows are provided to the
 * grid recycler view to draw the grid
 * [rowNumber] row number / index
 * [list] list of letters in this row
 */
class GridRow (val rowNumber: Int, val list: List<String>)
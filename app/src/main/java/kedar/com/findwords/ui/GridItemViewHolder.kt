package kedar.com.findwords.ui


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.grid_layout_item.view.*

/**
 * This is an item view holder for the grid recycler view
 */
class GridItemViewHolder(val context:Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * Binds each item (this view holder) to the recycler view
     * [letter] letter to be displayed in the tile / item view
     * [rowNum] row number of this item
     * [colNum] column number of this item
     * [id] unique ID calculated
     * [letterTile] textview that holds the letter text
     */
    fun bindView(letter: String, rowNum:Int, colNum:Int) {
        itemView.id = rowNum*8+colNum
        itemView.letterTile.text = letter
    }
}
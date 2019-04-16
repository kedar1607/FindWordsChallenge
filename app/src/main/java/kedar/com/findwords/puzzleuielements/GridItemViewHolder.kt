package kedar.com.findwords.puzzleuielements


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.grid_layout_item.view.*

class GridItemViewHolder(val context:Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    var row =  -1
    var col = -1
    fun bindView(letter: String, rowNum:Int, colNum:Int) {
        row= rowNum
        col = colNum
        itemView.id = rowNum*8+colNum
        itemView.letterTile.text = letter
    }
}
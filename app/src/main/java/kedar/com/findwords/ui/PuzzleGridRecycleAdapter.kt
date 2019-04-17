package kedar.com.findwords.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kedar.com.findwords.R
import kedar.com.findwords.utils.SizeUtil
import kedar.com.findwords.models.GridRow
import kotlinx.android.synthetic.main.grid_layout_item.view.*

/**
 * This class is an adapter / data source to the recycler view
 */
class PuzzleGridRecycleAdapter(val context: Context, val listSize: Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * list of grid rows that forms the grid
     */
    private var gridRows = listOf<GridRow>()

    /**
     * creates the view holder from grid_layout_item and returns it for draw
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val gridItm = GridItemViewHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.grid_layout_item, parent, false)
        )
        // Assign the item width and height, text size at run-time depending on the number of rows
        val itemSize = SizeUtil.getGridItemHeightAndWidth(context,listSize)
        gridItm.itemView.layoutParams.height = itemSize.toInt()
        gridItm.itemView.layoutParams.width = itemSize.toInt()
        gridItm.itemView.letterTile.textSize = SizeUtil.getGridItemTextSize(listSize)
        return gridItm

    }

    /**
     * returns number of items in the grid
     */
    override fun getItemCount(): Int = gridRows.size * gridRows[0].list.size

    /**
     * binds each item view to the rcv
     * to bind each item from [gridRows] which is a list of rows for the grid
     * [position] / [listSize] determines the row index and [position] % [listSize] determines the column index
     */
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val gridItemViewHolder = viewHolder as GridItemViewHolder

        gridItemViewHolder.bindView(gridRows[position / listSize].list[position % listSize],position / listSize,position % listSize)
    }

    /**
     * sets the data for the grid
     * [listOfLetters] list of grid rows
     * [listSize] number of rows
     */
    fun setGridLetters(listOfLetters: List<GridRow>) {
        this.gridRows = listOfLetters
        notifyDataSetChanged()
    }

}
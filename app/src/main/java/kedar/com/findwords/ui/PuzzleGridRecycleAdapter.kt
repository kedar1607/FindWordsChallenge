package kedar.com.findwords.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kedar.com.findwords.R
import kedar.com.findwords.models.GridRow
import kedar.com.findwords.ui.GridItemViewHolder

/**
 * This class is an adapter / data source to the recycler view
 */
class PuzzleGridRecycleAdapter(val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * list of grid rows that forms the grid
     */
    private var gridRows = listOf<GridRow>()

    /**
     * number of rows
     */
    private var listSize = 0

    /**
     * creates the view holder from grid_layout_item and returns it for draw
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val gridItm = GridItemViewHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.grid_layout_item, parent, false)
        )
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
    fun setGridLetters(listOfLetters: List<GridRow>, listSize: Int) {
        this.listSize = listSize
        this.gridRows = listOfLetters
        notifyDataSetChanged()
    }

}
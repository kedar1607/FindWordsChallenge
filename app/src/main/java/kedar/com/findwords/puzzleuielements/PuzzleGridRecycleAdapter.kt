package kedar.com.findwords.puzzleuielements

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kedar.com.findwords.R

class PuzzleGridRecycleAdapter(val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var gridRows = listOf<GridRow>()

    private var listSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val gridItm = GridItemViewHolder(context , LayoutInflater.from(parent.context).inflate(R.layout.grid_layout_item, parent, false))
        return gridItm

    }

    override fun getItemCount(): Int = gridRows.size * gridRows[0].list.size


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val gridItemViewHolder = viewHolder as GridItemViewHolder

        gridItemViewHolder.bindView(gridRows[position / listSize].list[position % listSize],position / listSize,position % listSize)
    }

    fun setGridLetters(listOfLetters: List<GridRow>, listSize: Int) {
        this.listSize = listSize
        this.gridRows = listOfLetters
        notifyDataSetChanged()
    }

}
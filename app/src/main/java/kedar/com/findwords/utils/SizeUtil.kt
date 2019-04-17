package kedar.com.findwords.utils

import android.content.Context

/**
 * This object is responsible for hosting all the utils method for sizing the UI components. All the methods are
 * static and no instance creation required for this class
 */
object SizeUtil {
    /**
     * Gets pixels from dp
     * [context] current context to load the resources
     * [dp] value in dp to be converted to pixels
     */
    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Calculate grid item height/width at run time using total number of rows [rowNum]
     * [context] current context to be passed on to [pxFromDp]
     * [rowNum] total number of rows
     * @return float value of item height / width
     */
    fun getGridItemHeightAndWidth(context: Context,rowNum: Int): Float{
        return pxFromDp(
            context,
            (TOTAL_WIDTH_DIVISIBLE / rowNum).toFloat()
        )
    }

    /**
     * Calculate text size for letters in the grid at run time using total number of rows [rowNum]
     * [rowNum] total number of rows
     * @return float value of text size in sp
     */
    fun getGridItemTextSize(rowNum: Int): Float{
        return (TOTAL_TEXT_AREA_DIVISIBLE /rowNum).toFloat()
    }

    const val TOTAL_WIDTH_DIVISIBLE = 300
    const val TOTAL_TEXT_AREA_DIVISIBLE = 160

}
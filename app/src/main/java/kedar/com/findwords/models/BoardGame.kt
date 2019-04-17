package kedar.com.findwords.models

import org.json.JSONArray
import org.json.JSONObject

/**
 * This class represents one single find challenge
 * [json] Json object for a single find challenge
 */
class BoardGame(json: JSONObject){
    /**
     * word for which translations should be found
     */
    var word = json.get("word") as String
    /**
     * character grid for the game play
     */
    var charGrid = json.get("character_grid")
    /**
     * JSON word locations of the right answer
     */
    var wordLoc = json.get("word_locations") as JSONObject
    /**
     * total words to be found
     */
    var totalPuzzles = 0
    /**
     * number of already solved words for the challenge
     */
    var solvedPuzzles = 0
    /**
     * list of grid rows for the game play
     */
    var gridRows = ArrayList<GridRow>()
    /**
     * word locations map with WordLocation as the key and boolean value that indicates if the word has
     * already been found or not
     */
    var wordLocations = HashMap<WordLocation, Boolean>()

    init {
        getGrid()
        getWordLocations()
    }

    /**
     * this method parses the JSON for all the word locations and stores then in the map [wordLocations]
     */
    private fun getWordLocations(){
        for(key in wordLoc.keys()){
            wordLocations[WordLocation(getListOfColRow(key),wordLoc.get(key) as String)] = false
        }
        totalPuzzles = wordLocations.size
    }

    /**
     * this method transforms list of row and column indexes in to the list of ColumnRowPair models, which is
     * used further for for that faster comparison with the selected words' letter indexes
     * [pairs] a string with comma separated numbers (representing cols at even indexes and rows at odd)
     * @return list of ColumnRowPairs
     */
    private fun getListOfColRow(pairs: String):List<ColumnRowPair>{
        val arr = pairs.split(",")
        val result = ArrayList<ColumnRowPair>()
        var count = 0
        while (count < arr.size){
            result.add(ColumnRowPair(arr[count++].toInt(),arr[count++].toInt()))
        }
        return result
    }

    /**
     * parses the [charGrid] jsonArray and transforms it in to [gridRows]
     */
    private fun getGrid(){
        val arrCharGrid = charGrid as JSONArray
        for (index in 0 until arrCharGrid.length()) {
            val item = arrCharGrid.getJSONArray(index)
            gridRows.add(GridRow(index, getEachRowOfGrid(item)))
        }
    }

    /**
     * parses [json] Json array of single row of a grid and returns the list of Strings which will be further
     * used to draw the grid row in grid recycler view
     * @return list of letters which represents one row of the grid
     */
    private fun getEachRowOfGrid(json: JSONArray): List<String>{
        val list = ArrayList<String>()
        for(index in 0 until json.length()){
            list.add(json[index].toString())
        }
        return list
    }
}
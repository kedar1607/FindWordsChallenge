package kedar.com.findwords.models

import android.os.Build
import com.google.gson.JsonObject
import kedar.com.findwords.puzzleuielements.GridRow
import org.json.JSONArray
import org.json.JSONObject

class BoardGame(json: JSONObject){
    var word = json.get("word") as String
    var charGrid = json.get("character_grid")
    var wordLoc = json.get("word_locations") as JSONObject
    var totalPuzzles = 0
    var solvedPuzzles = 0
    var gridRows = ArrayList<GridRow>()

    var wordLocations = HashMap<WordLocation, Boolean>()

    init {
        getGrid()
        getWordLocations()
    }

    private fun getWordLocations(){
        for(key in wordLoc.keys()){
            wordLocations.put(WordLocation(getListOfColRow(key),wordLoc.get(key) as String),false)
        }
        totalPuzzles = wordLocations.size
    }

    private fun getListOfColRow(pairs: String):List<ColumnRowPair>{
        val arr = pairs.split(",")
        val result = ArrayList<ColumnRowPair>()
        var count = 0
        while (count < arr.size){
            result.add(ColumnRowPair(arr[count++].toInt(),arr[count++].toInt()))
        }
        return result
    }

    private fun getGrid(){
        val arrCharGrid = charGrid as JSONArray
        for (index in 0..(arrCharGrid.length() - 1)) {
            val item = arrCharGrid.getJSONArray(index)
            gridRows.add(GridRow(index, getEachRowOfGrid(item)))
        }
    }

    private fun getEachRowOfGrid(json: JSONArray): List<String>{
        val list = ArrayList<String>()
        for(index in 0..json.length() -1){
            list.add(json[index].toString())
        }
        return list
    }
}
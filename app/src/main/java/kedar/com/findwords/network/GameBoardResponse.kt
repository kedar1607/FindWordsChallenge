package kedar.com.findwords.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

class GameBoardResponse {

    @SerializedName("source_language")
    @Expose
    var sourceLanguage: String? = null
    @SerializedName("word")
    @Expose
    var word: String? = null
    @SerializedName("character_grid")
    @Expose
    var characterGrid: List<List<String>>? = null
    @SerializedName("word_locations")
    @Expose
    var wordLocations: JSONObject? = null
    @SerializedName("target_language")
    @Expose
    var targetLanguage: String? = null

}
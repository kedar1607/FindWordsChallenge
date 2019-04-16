package kedar.com.findwords.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Streaming

interface GameDownloadService {
    @Streaming
    @GET("duolingo-data/s3/js2/find_challenges.txt")
    fun getGameData(): Call<GameBoardResponse>
}
package kedar.com.findwords.network

import android.content.Context
import kedar.com.findwords.R
import kedar.com.findwords.interfaces.DownloadServiceCallback
import kedar.com.findwords.models.BoardGame
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * This class is responsible for making network calls to download the data for the game play
 * [downloadServiceCallback] callbacks to the caller with status of the operation.
 */
class DownloadGameService(private val context: Context, private val downloadServiceCallback: DownloadServiceCallback) : Callback{

    /**
     * list of game boards converted from JSON to BoardGame objects
     */
    private val gameBoards = ArrayList<BoardGame>()

    init {
        val url = baseUrl
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(this)
    }

    /**
     * This function is called when response is received from the network.
     * [response] response that holds response body and other elements
     */
    override fun onResponse(call: Call?, response: Response?) {
        val body = response?.body()?.string()
        val array = body?.split(split_by)
        if(array!=null) {
            for (element in array) {
                if(!element.isBlank()&& element.isNotEmpty())
                    // transform all the JSON objects in BoardGame
                    gameBoards.add(BoardGame(JSONObject(element+ split_by)))
            }
            downloadServiceCallback.onSuccess(gameBoards)
        }else{
            downloadServiceCallback.onFailure(context.getString(R.string.download_failed))
        }
    }

    /**
     * This method reports failure to the caller in case of failure with the message whi it failed
     */
    override fun onFailure(call: Call?, e: IOException?) {
        downloadServiceCallback.onFailure(e?.message?:context.getString(R.string.download_failed))
        e?.printStackTrace()
    }

    companion object{
        const val split_by ="\"target_language\": \"es\"}"
        const val baseUrl = "https://s3.amazonaws.com/duolingo-data/s3/js2/find_challenges.txt"
    }
}
package kedar.com.findwords.interfaces

import kedar.com.findwords.models.BoardGame

/**
 * Callbacks to notify status of the executing jobs for DownloadGameService
 */
interface DownloadServiceCallback {
    /**
     * triggered when download is successful with the list of all the board games
     * [boardGames] list of all the game boards
     */
    fun onSuccess(boardGames: List<BoardGame>)

    /**
     * triggered in case of failure with the message
     * [message] message about download failure to end user
     */
    fun onFailure(message: String)
}
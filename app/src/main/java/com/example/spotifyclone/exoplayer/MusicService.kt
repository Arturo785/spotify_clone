package com.example.spotifyclone.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject


private const val SERVICE_TAG = "MusicService"
// this kind of service is used for broadcast music
@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){

    @Inject
    lateinit var dataSourceFactory : DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer : SimpleExoPlayer

    private val serviceJob = Job()
    // our custom scope
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var mediaSessionConnector : MediaSessionConnector


    override fun onCreate() {
        super.onCreate()

        /*
        *A future intent that other apps can use.

        * the returned object can be handed to other applications so that they can perform the action you described on your behalf
        * at a later time.
          By giving a PendingIntent to another application, you are granting it the right to perform the operation
            *  you have specified as if the other application was yourself (with the same permissions and identity).
        * */

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }


        // our session
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        // our connector
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // cancels all the pending jobs inside the scope
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

}
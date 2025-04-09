//package com.project.samay.domain.service
//
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.session.MediaSession
//import androidx.media3.session.MediaSessionService
//
//class AudioService: MediaSessionService() {
//    private var mediaSession: MediaSession? = null
//
//    //Creating a player at the start of the service
//    override fun onCreate() {
//        super.onCreate()
//        val player = ExoPlayer.Builder(this).build()
//        mediaSession = MediaSession.Builder(this, player).build()
//    }
//
//    //Releasing the resources on onDestroy
//    override fun onDestroy(){
//        mediaSession?.run {
//            player.release()
//            release()
//            mediaSession = null
//        }
//        super.onDestroy()
//    }
//
//    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
//        return mediaSession
//    }
//}
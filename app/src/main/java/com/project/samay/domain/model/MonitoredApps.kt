package com.project.samay.domain.model

import com.project.samay.R

enum class MonitoredApps(val displayName: String, val packageName: String, val iconsId: Int){
    INSTAGRAM("Instagram", "com.instagram.android", R.drawable.instagram_2111463),
    TWITTER("X", "com.twitter.android", R.drawable.twitter_5968830),
    LINKEDIN("LinkedIn", "com.linkedin.android", R.drawable.linkedin_3536505),
    YOUTUBE("YouTube", "com.google.android.youtube", R.drawable.youtube_1384060),
    WHATSAPP("WhatsApp", "com.whatsapp", R.drawable.whatsapp_5968841),
    DISCORD("Discord", "com.discord", R.drawable.discord_2626288),
    GMAIL("Gmail", "com.google.android.gm", R.drawable.new_10829119),
    CHROME("Chrome", "com.android.chrome", R.drawable.chrome_6125000),
    YTMUSIC("YT Music", "com.google.android.apps.youtube.music", R.drawable.ytmusic),
    NETLIX("Netflix", "com.netflix.mediaclient", R.drawable.netflix),
}
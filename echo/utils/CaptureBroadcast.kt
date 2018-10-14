package com.internshala.echo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_NEW_OUTGOING_CALL
import android.telephony.TelephonyManager
import com.internshala.echo.activity.MainActivity
import com.internshala.echo.R
import com.internshala.echo.Fragments.SongPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == ACTION_NEW_OUTGOING_CALL) {
            MainActivity.Statified.notificationManager?.cancel(1997)

            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            var tm = p0?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> try {
                    MainActivity.Statified.notificationManager?.cancel(1997)
                    if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                        SongPlayingFragment.Statified.mediaPlayer?.pause()
                        SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}




@file:Suppress("CAST_NEVER_SUCCEEDS")

package com.internshala.echo.activity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import com.internshala.echo.Fragments.MainFrameFragment
import com.internshala.echo.Fragments.SongPlayingFragment
import com.internshala.echo.R
import com.internshala.echo.adapters.NavigationDrawerAdapter


class MainActivity : AppCompatActivity() {


    var navigationDrawer_iconList = arrayListOf<String>()
    var imageFor_navigationDrawer = intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites
            , R.drawable.navigation_settings, R.drawable.navigation_aboutus)

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManager? = null
    }

    var trackNotificationBuilder: Notification? = null
    var mainFrameFragment: Fragment? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Statified.drawerLayout = findViewById(R.id.drawer_layout)
        navigationDrawer_iconList.add("All Songs")
        navigationDrawer_iconList.add("Favorites")
        navigationDrawer_iconList.add("Settings")
        navigationDrawer_iconList.add("About us")
        val togle = ActionBarDrawerToggle(this@MainActivity, Statified.drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        Statified.drawerLayout?.addDrawerListener(togle)
        togle.syncState()
        // setSupportActionBar(toolbar)


        //  setSupportActionBar(toolbar)
        /*     val actionBar = supportActionBar
             actionBar?.title = getString(R.string.navigation_drawer_open)
     */
        //  getSupportActionBar()?.setTitle("all Songs")
        mainFrameFragment = MainFrameFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.detail_fragment, mainFrameFragment as MainFrameFragment, "MainFrameFragment")
                .commit()
        val _navigationDrawerAdapter = NavigationDrawerAdapter(navigationDrawer_iconList,
                imageFor_navigationDrawer, this)
        _navigationDrawerAdapter.notifyDataSetChanged()


        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationDrawerAdapter
        navigation_recycler_view.setHasFixedSize(true)

        var intent = Intent(this@MainActivity, MainActivity::class.java)
        var pIntent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),
                intent, 0)
        trackNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in the Background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(1997)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1997, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(1997)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}






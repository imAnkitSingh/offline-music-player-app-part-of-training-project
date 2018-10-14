package com.internshala.echo.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.Fragments.SongPlayingFragment
import com.internshala.echo.R
import com.internshala.echo.utils.Songs

class FavoriteAdapter(_songsDetail: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {


    var songsDetail: ArrayList<Songs>? = null
    var mcontext: Context? = null

    init {
        this.songsDetail = _songsDetail
        this.mcontext = _context
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        var displayingObject = songsDetail?.get(p1)
        p0.trackTitle?.text = displayingObject?.songtitle
        p0.trackArtist?.text = displayingObject?.artist

        p0.contentHolder?.setOnClickListener({

            SongPlayingFragment.Staticated.multiSong()
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songAartist", displayingObject?.artist)
            args.putString("songTitle", displayingObject?.songtitle)
            args.putString("path", displayingObject?.songData)
            args.putInt("songid", displayingObject?.songId as Int)
            args.putLong("date", displayingObject?.dateAdded)
            args.putInt("position", p1)
            args.putParcelableArrayList("songDetail", songsDetail)
            songPlayingFragment?.arguments = args
            (mcontext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_fragment, songPlayingFragment, "SongPlayingFragment")
                    .addToBackStack("songPlayingFragment")
                    .commit()

        })
    }

    override fun getItemCount(): Int {
        if (songsDetail == null) {
            return 0
        } else {
            return (songsDetail as ArrayList<Songs>).size
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        var itemView = LayoutInflater.from(p0?.context)
                .inflate(R.layout.row_custom_mainframe_adapter, p0, false)
        return MyViewHolder(itemView)
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.trackTitle)
            trackArtist = view.findViewById(R.id.trackAartist)
            contentHolder = view.findViewById(R.id.contentRow)

        }

    }
}

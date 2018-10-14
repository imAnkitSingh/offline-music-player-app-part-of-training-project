package com.internshala.echo.Fragments


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.adapters.MainScreenAdapterr
import com.internshala.echo.R
import com.internshala.echo.utils.Songs
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainFrameFragment : Fragment() {
    var songsList = arrayListOf<Songs>()
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var recyclerView: RecyclerView? = null
    var noSongs: RelativeLayout? = null
    var visibleLayout: RelativeLayout? = null
    var songTitle: TextView? = null
    var songArtist: TextView? = null
    var myactivity: Activity? = null // this is created so that we can link it to the activity
    var mainScreenAdapter: MainScreenAdapterr? = null
    var trackPositon: Int = 0

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater!!.inflate(R.layout.fragment_main_frame, container, false)
        activity?.title = "All Songs"
        recyclerView = view?.findViewById(R.id.contentMain)
        visibleLayout = view?.findViewById(R.id.visible_layout)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        songArtist = view?.findViewById(R.id.songArtist)
        songTitle = view?.findViewById(R.id.songTitleMainScreen)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        noSongs = view?.findViewById(R.id.noSongs)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        songsList = getSongFromPhone()
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if (songsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            mainScreenAdapter = MainScreenAdapterr(songsList, myactivity as Context)
            var mlayoutManager = LinearLayoutManager(myactivity)
            recyclerView?.layoutManager = mlayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = mainScreenAdapter
        }
        if (songsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(songsList, Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(songsList, Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var switcher = item?.itemId
        if (switcher == R.id.action_sort_recent) {
            val editor = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_recent", "false")
            editor?.putString("action_Sort_ascending", "true")
            editor?.apply()
            if (songsList !== null) {
                Collections.sort(songsList, Songs.Statified.nameComparator)

            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_ascending) {
            val editor = myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_recent", "true")
            editor?.putString("action_sort_ascending", "false")
            editor?.apply()
            if (songsList !== null) {
                Collections.sort(songsList, Songs.Statified.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }


    fun getSongFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myactivity?.contentResolver
        var songsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songsCursor = contentResolver?.query(songsuri, null, null, null, null)
        if (songsCursor != null && songsCursor.moveToFirst()) {
            var songsid = songsCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songsArtist = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var title = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var songsData = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var dateAdded = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songsCursor.moveToNext()) {
                var currentid = songsCursor.getInt(songsid)
                var currenttitle = songsCursor.getString(title)
                var currentArtist = songsCursor.getString(songsArtist)
                var currentdata = songsCursor.getString(songsData)
                var currentdateadded = songsCursor.getLong(dateAdded)

                arrayList.add(Songs(currentid, currenttitle, currentArtist, currentdata, currentdateadded))


            }
        }

        return arrayList

    }

    fun bottomBarSetup() {
        bottoBarClickHandler()
        try {
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?._songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?._songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            }
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottoBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener {
            //   playPauseHiddenBottomBar()


            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songAartist", SongPlayingFragment.Statified.currentSongHelper?._songAartist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?._songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?._songPath)
            args.putInt("songid", SongPlayingFragment.Statified.currentSongHelper?.songId as Int)
            args.putInt("position", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songDetail", SongPlayingFragment.Statified.fetchSongs)
            //  args.putParcelableArrayList("SongDetail", songsList)
            args.putString("mainBottomBar", "Success")
            songPlayingFragment?.arguments = args // actually not get this line why this was needed here
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.detail_fragment, songPlayingFragment, "SongPlayingFragment")
                    ?.addToBackStack("SongPlayingFragment")
                    ?.commit()

        }
        playPauseButton?.setOnClickListener(
                {

                    if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                        SongPlayingFragment.Statified.mediaPlayer?.pause()
                        trackPositon = SongPlayingFragment.Statified.mediaPlayer?.currentPosition?.toInt() as Int
                        playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                    } else {
                        SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPositon)
                        SongPlayingFragment.Statified.mediaPlayer?.start()
                        playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                    }
                }
        )

    }
    /*  fun playPauseHiddenBottomBar() {
          if (MainFrameFragment.Statified.mediaPlayer?.isPlaying != null) {
              if (MainFrameFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                  SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
              } else {
                  SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
              }
          }
      }
  */

}


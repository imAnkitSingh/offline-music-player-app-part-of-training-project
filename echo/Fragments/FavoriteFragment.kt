package com.internshala.echo.Fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.*
import com.internshala.echo.adapters.FavoriteAdapter
import com.internshala.echo.database.EchoDatabase
import com.internshala.echo.utils.Songs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {
    //  var getsongList:ArrayList<Songs>?=null
    var noFavorite: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var songArtist: TextView? = null
    var myActivity: Activity? = null
    var trackPositon: Int = 0
    var favoriteContent: EchoDatabase? = null
    var refreshList = arrayListOf<Songs>()
    var getListfromDatabase: ArrayList<Songs>? = null

    //   var FavoriteFragment:Fragment?=null
    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        (activity as AppCompatActivity).title = "Favorite Songs"
        favoriteContent = EchoDatabase(myActivity)
        noFavorite = view?.findViewById(R.id.noFavoirite)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)
        songArtist = view?.findViewById(R.id.nowPlaying)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        display_favoirit_by_searching()

        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()

        activity?.title = "Favorite Songs"
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
        val item2 = menu?.findItem(R.id.action_redirect)
        item2?.isVisible = false
        //val item3=menu?.findItem(R.id.action_sort_ascending)
        //item3?.isVisible=false
    }

    fun getSongFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
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

                arrayList.add(Songs(currentid, currentArtist, currenttitle, currentdata, currentdateadded))


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
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?._songTitle)
            args.putString("songAartist", SongPlayingFragment.Statified.currentSongHelper?._songAartist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?._songPath)
            args.putInt("songid", SongPlayingFragment.Statified.currentSongHelper?.songId as Int)
            args.putInt("position", SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songDetail", SongPlayingFragment.Statified.fetchSongs)
            args.putString("favBottomBar", "Success")
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

    fun display_favoirit_by_searching() {
        if (favoriteContent?.checkSize() != null) {
            if (favoriteContent?.checkSize() as Int > 0) {

                /*New list for storing the favorites*/
                refreshList = ArrayList<Songs>()

                /*Getting the list of songs from database*/
                getListfromDatabase = favoriteContent?.queryDbList()

                /*Getting list of songs from phone storage*/
                val fetchListfromDevice = getSongFromPhone()

                /*If there are no songs in phone then there cannot be any favorites*/
                if (fetchListfromDevice != null) {

                    /*Then we check all the songs in the phone*/
                    for (i in 0..fetchListfromDevice?.size - 1) {

                        /*We iterate through every song in database*/
                        for (j in 0..getListfromDatabase?.size as Int - 1) {

                            /*While iterating through all the songs we check for the songs which are in both the lists
                        * i.e. the favorites songs*/
                            if ((getListfromDatabase?.get(j)?.songId) == (fetchListfromDevice?.get(i)?.songId)) {

                                /*on getting the favorite songs we add them to the refresh list*/
                                refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                            }
                        }
                    }
                } else {
                }
                /*If refresh list is null we display that there are no favorites*/
                if (refreshList == null) {
                    recyclerView?.visibility = View.INVISIBLE
                    noFavorite?.visibility = View.VISIBLE
                } else {
                    // recyclerView?.visibility = View.VISIBLE
                    // noFavorite?.visibility = View.INVISIBLE
                    /*Else we setup our recycler view for displaying the favorite songs*/
                    val favoriteAdapter = FavoriteAdapter(refreshList, myActivity as Context)
                    val mLayoutManager = LinearLayoutManager(activity)
                    recyclerView?.layoutManager = mLayoutManager
                    recyclerView?.itemAnimator = DefaultItemAnimator()
                    recyclerView?.adapter = favoriteAdapter
                    recyclerView?.setHasFixedSize(true)
                }
            } else {

                /*If initially the checkSize() function returned 0 then also we display the no favorites present message*/
                recyclerView?.visibility = View.INVISIBLE
                noFavorite?.visibility = View.VISIBLE
            }
        } else {
        }
    }

}






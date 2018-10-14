package com.internshala.echo.Fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.GRAVITY_EARTH
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.internshala.echo.Fragments.SongPlayingFragment.Statified.msensorManager
import com.internshala.echo.Fragments.SongPlayingFragment.Statified.myActivity
import com.internshala.echo.utils.CurrentSongHelper
import com.internshala.echo.database.EchoDatabase
import com.internshala.echo.R
import com.internshala.echo.adapters.FavoriteAdapter
import com.internshala.echo.utils.Songs
import kotlinx.android.synthetic.main.fragment_song__playing.*
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("CAST_NEVER_SUCCEEDS", "DEPRECATION")
/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    override fun onStopTrackingTouch(p0: SeekBar?) {
        // Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //     Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        //  Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        //  Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (p2 && Statified.mediaPlayer?.isPlaying as Boolean) {
            //myProgress = oprogress;
            //   arg1=mediaPlayer.getCurrentPosition();
            Statified.mediaPlayer?.seekTo(p1);
//            Statified.mediaPlayer?.prepare()
            Statified.seekbar?.max = Statified.mediaPlayer?.duration as Int
        }
    }

    object Statified {

        var songTitle: String? = null
        var songAartist: String? = null
        var songId: Int? = null
        var date: Long? = null
        var path: String? = null
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fav: ImageButton? = null
        var favoriteContent: EchoDatabase? = null
        var msensorManager: SensorManager? = null
        var msensirListner: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var updateSongTime = object : Runnable {
            override fun run() {
                var getCurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long)
                        , TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
                var getEnd = mediaPlayer?.duration
                endTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(getEnd?.toLong() as Long)
                        , TimeUnit.MILLISECONDS.toSeconds(getEnd?.toLong() as Long) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getEnd?.toLong() as Long))))
                seekbar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }

        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle Feature"
        var MY_PREFS_LOOP = "Loop Feature"
        fun clickHandler() {

            Statified.fav?.setOnClickListener({
                if (Statified.currentSongHelper?.songId != null) {
                    if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) as Boolean) {
                        Statified.fav?.setImageResource(R.drawable.favorite_off)
                        Statified.favoriteContent?.deleteFaviorate(Statified.currentSongHelper?.songId?.toInt() as Int)
                        Toast.makeText(Statified.myActivity, "song is Successfully deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Statified.fav?.setImageResource(R.drawable.favorite_on)
                        Statified.favoriteContent?.storeASFaviorate(Statified.currentSongHelper?.songId?.toInt() as Int,
                                Statified.currentSongHelper?._songAartist, Statified.currentSongHelper?._songTitle, Statified.currentSongHelper?._songPath)
                        Toast.makeText(Statified.myActivity, "song is succesfully inserted as favorite", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            })
            Statified.shuffleImageButton?.setOnClickListener({
                var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
                if (Statified.currentSongHelper?.isShuffle != null) {
                    if (Statified.currentSongHelper?.isShuffle as Boolean) {
                        Statified.currentSongHelper?.isShuffle = false
                        Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                        editorShuffle?.putBoolean("feature", false)
                        editorShuffle?.apply()
                    } else {
                        if (Statified.currentSongHelper?.isloop as Boolean) {
                            Statified.currentSongHelper?.isloop = false
                            Statified.currentSongHelper?.isShuffle = true
                            Statified?.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                            Statified?.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                            editorShuffle?.putBoolean("feature", true)
                            editorLoop?.apply()
                        } else {
                            Statified.currentSongHelper?.isShuffle = true
                            Statified.currentSongHelper?.isloop = false
                            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                            editorShuffle?.putBoolean("feature", true)
                            editorShuffle?.apply()
                            editorLoop?.putBoolean("feature", false)
                            editorLoop?.apply()
                        }
                    }
                }

            })
            Statified.nextImageButton?.setOnClickListener({
                Statified.currentSongHelper?.isplaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                if (Statified.currentSongHelper?.isShuffle != null) {
                    if (Statified.currentSongHelper?.isShuffle as Boolean) {
                        playNext("playNextLikeNormalShuffle")

                    } else {
                        playNext("playNextNormal")

                    }
                } else {
                }

            })

            Statified.previousImageButton?.setOnClickListener({
                Statified.currentSongHelper?.isplaying = true
                if (Statified.currentSongHelper?.isloop != null) {
                    if (Statified.currentSongHelper?.isloop as Boolean) {
                        Statified.currentSongHelper?.isloop = false
                        Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    }
                    playPrevious()
                } else {
                }
            })

            Statified.loopImageButton?.setOnClickListener({
                var editorShuffle = Statified.myActivity?.getSharedPreferences(MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop = Statified.myActivity?.getSharedPreferences(MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
                if (Statified.currentSongHelper?.isloop != null) {
                    if (Statified.currentSongHelper?.isloop as Boolean) {
                        Statified.currentSongHelper?.isloop = false
                        Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                        editorLoop?.putBoolean("feature", false)
                        editorLoop?.apply()
                    } else {
                        Statified.currentSongHelper?.isShuffle = false
                        Statified.currentSongHelper?.isloop = true
                        Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                        Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                        editorLoop?.putBoolean("feature", true)
                        editorLoop?.apply()
                        editorShuffle?.putBoolean("feature", false)
                        editorShuffle?.apply()
                    }
                } else {
                }

            })

            Statified.playPauseImageButton?.setOnClickListener({
                if (Statified.mediaPlayer?.isPlaying as Boolean) {
                    Statified.mediaPlayer?.pause()
                    Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                } else {
                    Statified.mediaPlayer?.start()
                    Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
                    Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                }
            })
        }

        fun multiSong() {
            if (Statified.mediaPlayer?.isPlaying != null) {
                if (Statified.mediaPlayer?.isPlaying as Boolean) {
                    Statified.currentSongHelper?.isloop = false
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?._songPath = nextSong?.songData
                    Statified.currentSongHelper?._songTitle = nextSong?.songtitle
                    Statified.currentSongHelper?._songAartist = nextSong?.artist
                    Statified.currentSongHelper?.songId = nextSong?.songId
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.stop()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    //  Statified.mediaPlayer?.start()
                }
            }
        }

        fun playNext(check: String) {
            if (check.equals("playNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("playNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                if (Statified.fetchSongs?.size?.plus(1) != null) {
                    var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                    Statified.currentPosition = randomPosition
                }
            }
            if (Statified.currentPosition.equals(Statified.fetchSongs?.size)) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isloop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?._songPath = nextSong?.songData
            Statified.currentSongHelper?._songTitle = nextSong?.songtitle
            Statified.currentSongHelper?._songAartist = nextSong?.artist
            if (nextSong?.songId != null) {
                Statified.currentSongHelper?.songId = nextSong?.songId as Int
            }
            //    Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            /*   if(Statified.mediaPlayer==MainFrameFragment.Statified.mediaPlayer)
               {
                   if ((Statified.currentSongHelper?._songTitle !=null )&& (Statified.currentSongHelper?._songAartist !=null) ) {
                       Staticated.updateTextView(Statified.currentSongHelper?._songTitle as String, Statified.currentSongHelper?._songAartist as String)
                       MainFrameFragment.Statified.mediaPlayer?.reset()
                       try {
                           MainFrameFragment.Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                           MainFrameFragment.Statified.mediaPlayer?.prepare()
                           MainFrameFragment.Statified.mediaPlayer?.start()
                           Staticated.processInformation(MainFrameFragment.Statified.mediaPlayer as MediaPlayer)
                       } catch (e: Exception) {
                           e.printStackTrace()
                       }
                       if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId?.toLong() as Long) as Boolean) {
                           Statified.fav?.setImageResource(R.drawable.favorite_on)
                       } else {
                           Statified.fav?.setImageResource(R.drawable.favorite_off)

                       }
                   }
               }
               else if (Statified.mediaPlayer==FavoriteFragment.Statified.mediaPlayer)
               {
                   if ((Statified.currentSongHelper?._songTitle !=null )&& (Statified.currentSongHelper?._songAartist !=null) ) {
                       Staticated.updateTextView(Statified.currentSongHelper?._songTitle as String, Statified.currentSongHelper?._songAartist as String)
                       FavoriteFragment.Statified.mediaPlayer?.reset()
                       try {
                           FavoriteFragment.Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                           FavoriteFragment.Statified.mediaPlayer?.prepare()
                           FavoriteFragment.Statified.mediaPlayer?.start()
                           Staticated.processInformation(MainFrameFragment.Statified.mediaPlayer as MediaPlayer)
                       } catch (e: Exception) {
                           e.printStackTrace()
                       }
                       if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId?.toLong() as Long) as Boolean) {
                           Statified.fav?.setImageResource(R.drawable.favorite_on)
                       } else {
                           Statified.fav?.setImageResource(R.drawable.favorite_off)

                       }
                   }
               }
               else { */

            if ((Statified.currentSongHelper?._songTitle) != null && (Statified.currentSongHelper?._songAartist) != null) {
                Staticated.updateTextView(Statified.currentSongHelper?._songTitle as String, Statified.currentSongHelper?._songAartist as String)
                Statified.mediaPlayer?.reset()


                try {
                    Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                    Statified.mediaPlayer?.prepare()
                    Statified.mediaPlayer?.start()
                    Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) as Boolean) {
                    Statified.fav?.setImageResource(R.drawable.favorite_on)
                } else {
                    Statified.fav?.setImageResource(R.drawable.favorite_off)

                }
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

            }
        }

        fun playPrevious() {
            Statified.currentPosition = Statified.currentPosition - 1
            if (Statified.currentPosition == -1) {
                Statified.currentPosition = 0
            }
            if (Statified.currentSongHelper?.isplaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isloop = false
            Statified.currentSongHelper?.isShuffle = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?._songPath = nextSong?.songData
            Statified.currentSongHelper?._songTitle = nextSong?.songtitle
            Statified.currentSongHelper?._songAartist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            if ((Statified.currentSongHelper?._songAartist != null) && (Statified.currentSongHelper?._songTitle != null)) {
                Staticated.updateTextView(Statified.currentSongHelper?._songAartist as String, Statified.currentSongHelper?._songTitle as String)
                Statified.mediaPlayer?.reset()
                try {
                    Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                    Statified.mediaPlayer?.prepare()
                    Statified.mediaPlayer?.start()
                    Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            if (Statified.currentSongHelper?.songId != null) {
                if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) as Boolean) {
                    Statified.fav?.setImageResource(R.drawable.favorite_on)
                } else {
                    Statified.fav?.setImageResource(R.drawable.favorite_off)
                }
            }

        }


        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle != null) {
                if (Statified.currentSongHelper?.isShuffle as Boolean) {
                    Staticated.playNext("playNextLikeNormalShuffle")
                    Statified.currentSongHelper?.isplaying = true
                } else {
                    if (Statified.currentSongHelper?.isloop as Boolean) {
                        Statified.currentSongHelper?.isplaying = true
                        var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                        Statified.currentSongHelper?._songPath = nextSong?.songData
                        Statified.currentSongHelper?._songTitle = nextSong?.songtitle
                        Statified.currentSongHelper?._songAartist = nextSong?.artist
                        Statified.currentSongHelper?.songId = nextSong?.songId
                        Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                        Statified.mediaPlayer?.reset()
                        try {
                            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?._songPath))
                            Statified.mediaPlayer?.prepare()
                            Statified.mediaPlayer?.start()
                            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        Staticated.playNext("playNextNormal")
                        Statified.currentSongHelper?.isplaying = true
                    }
                }
            }



            if ((Statified.currentSongHelper?.songId) != null) {
                if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) != null) {
                    if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) as Boolean) {
                        Statified.fav?.setImageResource(R.drawable.favorite_on)
                    } else {
                        Statified.fav?.setImageResource(R.drawable.favorite_off)
                    }
                }
            }
        }


        fun updateTextView(songtitle: String, artist: String) {
            var songTitleUpdated = songtitle
            var songAartistUpdated = artist

            if (songTitleUpdated?.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songAartistUpdated.equals("<unknown>", true)) {
                songAartistUpdated = "unknown"
            }

            Statified.songTitleView?.setText(songTitleUpdated)
            Statified.songArtistView?.setText(songAartistUpdated)
            return
        }

        /*    fun processInformation(mediaPlayer: MediaPlayer) {
                var finalTime = mediaPlayer?.duration
                var startTime = mediaPlayer?.currentPosition
                Statified.seekbar?.max = finalTime as Int
                Statified.startTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long)
                        , TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))))
                Statified.endTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long)
                        , TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long))))
                Statified.seekbar?.setProgress(startTime?.toInt() as Int )
                Handler().postDelayed(Statified.updateSongTime, 1000)
            }
            */
        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer?.duration

            /*Obtaining the current position*/
            val startTime = mediaPlayer?.currentPosition
            Statified.seekbar?.max = finalTime

            /*Here we format the time and set it to the start time text*/
            Statified.startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )

            /*Similar to above is done for the end time text*/
            Statified.endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )

            /*Seekbar has been assigned this time so that it moves according to the time of song*/
            Statified.seekbar?.setProgress(startTime?.toInt() as Int)

            /*Now this task is synced with the update song time obhect*/
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }


    }

    var mAccelaration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var view = inflater!!.inflate(R.layout.fragment_song__playing, container, false)
        setHasOptionsMenu(true)
         activity?.title = "now Playing"

        Statified.seekbar = view.findViewById(R.id.seekbaar)
        Statified.startTimeText = view.findViewById(R.id.startTime)
        Statified.endTimeText = view.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view.findViewById(R.id.playPauseButton)
        Statified.previousImageButton = view.findViewById(R.id.previousButton)
        Statified.nextImageButton = view.findViewById(R.id.nextButton)
        Statified.loopImageButton = view.findViewById(R.id.loopButton)
        Statified.songArtistView = view.findViewById(R.id.songAartist)
        Statified.songTitleView = view.findViewById(R.id.songTitle)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleIcon)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fav = view?.findViewById(R.id.favoriteIcon)
        Statified.fav?.alpha = 0.8f
        SongPlayingFragment.Statified.seekbar!!.setOnSeekBarChangeListener(this)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity as Activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        msensorManager?.registerListener(Statified.msensirListner, msensorManager?.getDefaultSensor
        (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()
        msensorManager?.unregisterListener(Statified.msensirListner)


    }

    override fun onDestroy() {
        Statified.audioVisualization?.release()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.mAccelaration = 0f
        this.mAccelerationCurrent = GRAVITY_EARTH
        this.mAccelerationLast = GRAVITY_EARTH
        this.bindShakeListener()


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isplaying = true
        Statified.currentSongHelper?.isloop = false
        Statified.currentSongHelper?.isShuffle = false
        Statified.favoriteContent = EchoDatabase(Statified.myActivity)

        try {
            Statified.songTitle = arguments?.getString("songTitle")
            Statified.songAartist = arguments?.getString("songAartist")
            Statified.songId = arguments?.getInt("songid") as Int
            Statified.date = arguments?.getLong("date")
            Statified.path = arguments?.getString("path")
            Statified.currentPosition = arguments?.getInt("position") as Int
            Statified.fetchSongs = arguments?.getParcelableArrayList("songDetail")
            Statified.currentSongHelper?.songId = Statified.songId as Int
            Statified.currentSongHelper?._songTitle = Statified.songTitle
            Statified.currentSongHelper?._songAartist = Statified.songAartist
            Statified.currentSongHelper?._songPath = Statified.path
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Staticated.updateTextView(Statified.currentSongHelper?._songTitle as String, Statified.currentSongHelper?._songAartist as String)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavoriteBar = arguments?.get("favBottomBar") as? String
        var fromMainScreenFragment = arguments?.get("mainBottomBar") as? String
        if (fromFavoriteBar != null) {
            SongPlayingFragment.Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
            //   Statified.mediaPlayer=MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
            Staticated.processInformation(FavoriteFragment.Statified.mediaPlayer as MediaPlayer)
            /*  Statified.mediaPlayer?.setOnCompletionListener {
                  Staticated.onSongComplete()
              } */

        } else if (fromMainScreenFragment != null) {

            SongPlayingFragment.Statified.mediaPlayer = MainFrameFragment.Statified.mediaPlayer
            //  Statified.mediaPlayer= MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
            Staticated.processInformation(MainFrameFragment.Statified.mediaPlayer as MediaPlayer)
            // Statified.mediaPlayer?.setOnCompletionListener {
//                Staticated.onSongComplete()
            //          }
        } else {
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        }


        // Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isplaying as Boolean) {
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }




        Staticated.clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isloop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isloop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            Statified.currentSongHelper?.isloop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        if (Statified.currentSongHelper?.songId != null) {
            if (Statified.favoriteContent?.checkifIdExixts(Statified.currentSongHelper?.songId) as Boolean) {
                Statified.fav?.setImageResource(R.drawable.favorite_on)
            } else {
                Statified.fav?.setImageResource(R.drawable.favorite_off)
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        //  val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        // item?.isVisible = false
        //val item3:MenuItem?=menu?.findItem(R.id.)

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }

        }
        return false
    }


    fun bindShakeListener() {

        /*The sensor listener has two methods used for its implementation i.e. OnAccuracyChanged() and onSensorChanged*/
        Statified.msensirListner = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                /*We do noot need to check or work with the accuracy changes for the sensor*/
            }

            override fun onSensorChanged(event: SensorEvent) {

                /*We need this onSensorChanged function
                * This function is called when there is a new sensor event*/
                /*The sensor event has 3 dimensions i.e. the x, y and z in which the changes can occur*/
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                /*Now lets see how we calculate the changes in the acceleration*/
                /*Now we shook the phone so the current acceleration will be the first to start with*/
                mAccelerationLast = mAccelerationCurrent

                /*Since we could have moved the phone in any direction, we calculate the Euclidean distance to get the normalized distance*/
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()

                /*Delta gives the change in acceleration*/
                val delta = mAccelerationCurrent - mAccelerationLast

                /*Here we calculate thelower filter
                * The written below is a formula to get it*/
                mAccelaration = mAccelaration * 0.9f + delta

                /*We obtain a real number for acceleration
                * and we check if the acceleration was noticeable, considering 12 here*/
                if (mAccelaration > 12) {

                    /*If the accel was greater than 12 we change the song, given the fact our shake to change was active*/
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }
}









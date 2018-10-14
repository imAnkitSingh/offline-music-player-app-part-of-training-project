package com.internshala.echo.utils

import android.os.Parcel
import android.os.Parcelable

class Songs(var songId: Int, var songtitle: String, var artist: String, var songData: String, var dateAdded: Long) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(p0: Parcel?, p1: Int) {
        return
    }

    object Statified {
        var nameComparator: Comparator<Songs> = Comparator<Songs> { song1, song2
            ->
            val songOne = song1.songtitle.toUpperCase()
            val songTwo = song2.songtitle.toUpperCase()
            songOne.compareTo(songTwo)
        }
        var dateComparator: Comparator<Songs> = Comparator<Songs> { song1, song2 ->
            val songOne = song1.dateAdded.toInt()
            val songTwo = song2.dateAdded.toInt()
            songOne.compareTo(songTwo)
        }

    }

    companion object CREATOR : Parcelable.Creator<Songs> {
        override fun createFromParcel(parcel: Parcel): Songs {
            return Songs(parcel)
        }

        override fun newArray(size: Int): Array<Songs?> {
            return arrayOfNulls(size)
        }
    }
}



package com.internshala.echo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.internshala.echo.utils.Songs

@Suppress("CAST_NEVER_SUCCEEDS")
class EchoDatabase : SQLiteOpenHelper {
    var _songList = ArrayList<Songs>()

    object Staticated {
        val DB_VERSION = 1
        val DB_NAME = "FaviorateDatabase"
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "Song_ID"
        val COLUMN_SONG_TITLE = "Song_Title"
        val COLUMN_SONG_ARTIST = "Song_Artist"
        val COLUMN_SONG_PATH = "Song_Path"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID +
                " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING,"
                + Staticated.COLUMN_SONG_PATH + " STRING);")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
            super(context, name, factory, version)

    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)


    fun storeASFaviorate(id: Int?, artist: String?, title: String?, path: String?) {
        val db = this.writableDatabase
        var contentvalue = ContentValues()
        contentvalue.put(Staticated.COLUMN_ID, id)
        contentvalue.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentvalue.put(Staticated.COLUMN_SONG_TITLE, title)
        contentvalue.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentvalue)
        db.close()
    }

    fun queryDbList(): ArrayList<Songs>? {
        try {


            var db = readableDatabase
            var queryParams = "SELECT * FROM " + Staticated.TABLE_NAME
            var csor = db.rawQuery(queryParams, null)
            if (csor.moveToFirst())
                do {
                    val _id = csor?.getInt(csor.getColumnIndex(Staticated.COLUMN_ID))
                    val _title = csor?.getString(csor.getColumnIndex(Staticated.COLUMN_SONG_TITLE))
                    val _artist = csor?.getString(csor.getColumnIndex(Staticated.COLUMN_SONG_ARTIST))
                    val _path = csor?.getString(csor.getColumnIndex(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id as Int, _title as String, _artist as String, _path as String, 0))
                } while (csor.moveToNext())
            else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songList
    }

    fun checkifIdExixts(_id: Int?): Boolean {
        var _songId = -200
        var db = readableDatabase
        if (_id != null) {
            var queryParams = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE Song_ID  = '$_id'"
            var csor = db.rawQuery(queryParams, null)
            if (csor.moveToFirst()) {
                do {
                    _songId = csor?.getInt(csor.getColumnIndexOrThrow(Staticated.COLUMN_ID)) as Int

                } while (csor.moveToNext())
            } else {
                return false
            }
        }
        return _songId != -200


    }

    fun deleteFaviorate(_id: Int) {
        val db = writableDatabase

        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun deletefavoirite(_id: Int) {
        var db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        var queryParams = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(queryParams, null)
        if (cSor.moveToFirst()) {
            do {
                counter = counter + 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }


}
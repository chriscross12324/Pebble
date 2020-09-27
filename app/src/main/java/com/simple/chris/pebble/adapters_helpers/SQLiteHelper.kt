package com.simple.chris.pebble.adapters_helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class SQLiteHelper(val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                GRADIENT_NAME + " TEXT," +
                GRADIENT_COLOURS + " TEXT," +
                GRADIENT_DESCRIPTION + " TEXT," +
                GRADIENT_UID + " TEXT)"
        p0?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXIST " + TABLE_NAME)
        onCreate(p0)
    }

    fun insertGradient(name: String, colours: String, desc: String, uid: String) {
        val db = this.writableDatabase
        var cv = ContentValues()

        cv.put(GRADIENT_NAME, name)
        cv.put(GRADIENT_COLOURS, colours)
        cv.put(GRADIENT_DESCRIPTION, desc)
        cv.put(GRADIENT_UID, uid)
        val result = db.insert(TABLE_NAME, null, cv)

        if (result == (-1).toLong()) {
            Toast.makeText(context, "SQLite Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun readGradients() : ArrayList<HashMap<String, String>> {
        val list = ArrayList<HashMap<String, String>>()
        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME
        val result = db.rawQuery(query, null)

        if (result.moveToLast()) {
            do {
                val item = HashMap<String, String>()
                item["gradientName"] = result.getString(result.getColumnIndex(GRADIENT_NAME))
                item["gradientColours"] = result.getString(result.getColumnIndex(GRADIENT_COLOURS))
                item["description"] = result.getString(result.getColumnIndex(GRADIENT_DESCRIPTION))
                item["gradientUID"] = result.getString(result.getColumnIndex(GRADIENT_UID))

                list.add(item)

            } while (result.moveToPrevious())
        }

        result.close()
        db.close()
        return list
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "UserPublished.db"
        const val TABLE_NAME = "user_gradients"
        const val GRADIENT_NAME = "NAME"
        const val GRADIENT_COLOURS = "COLOURS"
        const val GRADIENT_DESCRIPTION = "DESCRIPTION"
        const val GRADIENT_UID = "UID"
    }

}
package com.example.butceasistanim.ui.fatura

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_TYPE TEXT,"
                + "$COLUMN_DUE_DATE TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun getAllData(): List<Pair<String, String>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val dataList = mutableListOf<Pair<String, String>>()

        if (cursor.moveToFirst()) {
            do {
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
                dataList.add(Pair(type, dueDate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dataList
    }

    fun insertData(type: String, dueDate: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TYPE, type)
        contentValues.put(COLUMN_DUE_DATE, dueDate)
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FaturaDB.db"
        private const val TABLE_NAME = "faturalar"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DUE_DATE = "due_date"
    }
}

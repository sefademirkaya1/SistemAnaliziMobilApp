package com.example.butceasistanim.ui.butce

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "budget.db"
        const val DATABASE_VERSION = 1
        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_RENT = "rent"
        const val COLUMN_UTILITIES = "utilities"
        const val COLUMN_GROCERIES = "groceries"
        const val COLUMN_CLOTHING = "clothing"
        const val COLUMN_FUEL = "fuel"
        const val COLUMN_OTHER = "other"
        const val COLUMN_TOTAL_INCOME = "total_income"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RENT REAL,
                $COLUMN_UTILITIES REAL,
                $COLUMN_GROCERIES REAL,
                $COLUMN_CLOTHING REAL,
                $COLUMN_FUEL REAL,
                $COLUMN_OTHER REAL,
                $COLUMN_TOTAL_INCOME REAL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    //override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
 //      
  //  }

    fun addExpense(rent: Double, utilities: Double, groceries: Double, clothing: Double, fuel: Double, other: Double, totalIncome: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RENT, rent)
            put(COLUMN_UTILITIES, utilities)
            put(COLUMN_GROCERIES, groceries)
            put(COLUMN_CLOTHING, clothing)
            put(COLUMN_FUEL, fuel)
            put(COLUMN_OTHER, other)
            put(COLUMN_TOTAL_INCOME, totalIncome)
        }
        val result = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return result != -1L
    }

    fun updateIncome(totalIncome: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOTAL_INCOME, totalIncome)
        }
        val result = db.update(TABLE_EXPENSES, values, "$COLUMN_ID = (SELECT MAX($COLUMN_ID) FROM $TABLE_EXPENSES)", null)
        db.close()
        return result > 0
    }

    fun updateExpense(rent: Double, utilities: Double, groceries: Double, clothing: Double, fuel: Double, other: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RENT, rent)
            put(COLUMN_UTILITIES, utilities)
            put(COLUMN_GROCERIES, groceries)
            put(COLUMN_CLOTHING, clothing)
            put(COLUMN_FUEL, fuel)
            put(COLUMN_OTHER, other)
        }
        val result = db.update(TABLE_EXPENSES, values, "$COLUMN_ID = (SELECT MAX($COLUMN_ID) FROM $TABLE_EXPENSES)", null)
        db.close()
        return result > 0
    }

    fun getExpenses(): List<Map<String, Double>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSES", null)
        val expensesList = mutableListOf<Map<String, Double>>()

        if (cursor.moveToFirst()) {
            do {
                val expense = mapOf(
                    COLUMN_RENT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RENT)),
                    COLUMN_UTILITIES to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UTILITIES)),
                    COLUMN_GROCERIES to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GROCERIES)),
                    COLUMN_CLOTHING to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING)),
                    COLUMN_FUEL to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FUEL)),
                    COLUMN_OTHER to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_OTHER)),
                    COLUMN_TOTAL_INCOME to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_INCOME))
                )
                expensesList.add(expense)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expensesList
    }

    fun getLatestExpenses(): Map<String, Double>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSES ORDER BY $COLUMN_ID DESC LIMIT 1", null)
        var expense: Map<String, Double>? = null

        if (cursor.moveToFirst()) {
            expense = mapOf(
                COLUMN_RENT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RENT)),
                COLUMN_UTILITIES to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UTILITIES)),
                COLUMN_GROCERIES to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GROCERIES)),
                COLUMN_CLOTHING to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CLOTHING)),
                COLUMN_FUEL to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FUEL)),
                COLUMN_OTHER to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_OTHER)),
                COLUMN_TOTAL_INCOME to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_INCOME))
            )
        }

        cursor.close()
        db.close()
        return expense
    }

    fun getTotalExpenses(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COLUMN_RENT + $COLUMN_UTILITIES + $COLUMN_GROCERIES + $COLUMN_CLOTHING + $COLUMN_FUEL + $COLUMN_OTHER) AS total FROM $TABLE_EXPENSES", null)
        var totalExpenses = 0.0

        if (cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }

        cursor.close()
        db.close()
        return totalExpenses
    }

    fun deleteAllExpenses(): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_EXPENSES, null, null)
        db.close()
        return result > 0
    }
}

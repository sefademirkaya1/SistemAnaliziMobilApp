package com.example.butceasistanim.ui.butce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.butceasistanim.R

class Butce : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextRent: EditText
    private lateinit var editTextUtilities: EditText
    private lateinit var editTextGroceries: EditText
    private lateinit var editTextClothing: EditText
    private lateinit var editTextFuel: EditText
    private lateinit var editTextDiger: EditText
    private lateinit var editTextTotalMonthlyExpense: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_butce, container, false)

        dbHelper = DatabaseHelper(requireContext())

        editTextRent = view.findViewById(R.id.editTextRent)
        editTextUtilities = view.findViewById(R.id.editTextUtilities)
        editTextGroceries = view.findViewById(R.id.editTextGroceries)
        editTextClothing = view.findViewById(R.id.editTextClothing)
        editTextFuel = view.findViewById(R.id.editTextFuel)
        editTextDiger = view.findViewById(R.id.editTextDiger)
        editTextTotalMonthlyExpense = view.findViewById(R.id.editTextTotalMonthlyExpense)

        val buttonSaveExpence = view.findViewById<Button>(R.id.buttonSaveExpence)
        val buttonSaveIncome = view.findViewById<Button>(R.id.buttonSaveIncome)
        val buttonNewMonth = view.findViewById<Button>(R.id.buttonNewMonth)

        buttonSaveExpence.setOnClickListener {
            saveExpense()
        }

        buttonSaveIncome.setOnClickListener {
            saveIncome()
        }

        buttonNewMonth.setOnClickListener {
            showConfirmationDialog()
        }

        loadExpenses()

        return view
    }

    private fun saveExpense() {
        val rent = editTextRent.text.toString().toDoubleOrNull() ?: 0.0
        val utilities = editTextUtilities.text.toString().toDoubleOrNull() ?: 0.0
        val groceries = editTextGroceries.text.toString().toDoubleOrNull() ?: 0.0
        val clothing = editTextClothing.text.toString().toDoubleOrNull() ?: 0.0
        val fuel = editTextFuel.text.toString().toDoubleOrNull() ?: 0.0
        val other = editTextDiger.text.toString().toDoubleOrNull() ?: 0.0
        val totalIncome = editTextTotalMonthlyExpense.text.toString().toDoubleOrNull() ?: 0.0

        Log.d("ButceFragment", "Rent: $rent, Utilities: $utilities, Groceries: $groceries, Clothing: $clothing, Fuel: $fuel, Other: $other, Total Income: $totalIncome")

        val result = dbHelper.addExpense(rent, utilities, groceries, clothing, fuel, other, totalIncome)
        if (result) {
            Toast.makeText(requireContext(), "Giderler başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Giderler kaydedilirken hata oluştu.", Toast.LENGTH_SHORT).show()
        }

        loadExpenses()
    }

    private fun saveIncome() {
        val totalIncome = editTextTotalMonthlyExpense.text.toString().toDoubleOrNull() ?: 0.0
        Log.d("ButceFragment", "Total Income: $totalIncome")

        val latestExpenses = dbHelper.getLatestExpenses()
        if (latestExpenses != null) {
            val result = dbHelper.updateIncome(totalIncome)

            if (result) {
                Toast.makeText(requireContext(), "Gelir başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Gelir başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            }

            loadExpenses()
        } else {
            Toast.makeText(requireContext(), "Giderler güncellenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Yeni Ay")
            .setMessage("Yeni ay butonu Gelir ve Giderlerdeki verileri temizler, yeni ay için hazır hale getirir. Temizleme işlemini onaylıyor musunuz?")
            .setPositiveButton("Evet") { _, _ ->
                clearTextFields()
            }
            .setNegativeButton("Hayır", null)
            .show()
    }

    private fun clearTextFields() {
        editTextRent.text.clear()
        editTextUtilities.text.clear()
        editTextGroceries.text.clear()
        editTextClothing.text.clear()
        editTextFuel.text.clear()
        editTextDiger.text.clear()
        editTextTotalMonthlyExpense.text.clear()

        val result = dbHelper.deleteAllExpenses()
        if (result) {
            Toast.makeText(requireContext(), "Yeni ay başlatıldı veriler temizlendi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Veriler temizlenirken bir hata oluştu.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadExpenses() {
        val expensesList = dbHelper.getExpenses()
        if (expensesList.isNotEmpty()) {
            val latestExpense = expensesList.last()
            Log.d("ButceFragment", "Loading latest expenses: $latestExpense")
            editTextRent.setText(latestExpense[DatabaseHelper.COLUMN_RENT]?.toString() ?: "0.0")
            editTextUtilities.setText(latestExpense[DatabaseHelper.COLUMN_UTILITIES]?.toString() ?: "0.0")
            editTextGroceries.setText(latestExpense[DatabaseHelper.COLUMN_GROCERIES]?.toString() ?: "0.0")
            editTextClothing.setText(latestExpense[DatabaseHelper.COLUMN_CLOTHING]?.toString() ?: "0.0")
            editTextFuel.setText(latestExpense[DatabaseHelper.COLUMN_FUEL]?.toString() ?: "0.0")
            editTextDiger.setText(latestExpense[DatabaseHelper.COLUMN_OTHER]?.toString() ?: "0.0")
            editTextTotalMonthlyExpense.setText(latestExpense[DatabaseHelper.COLUMN_TOTAL_INCOME]?.toString() ?: "0.0")
        } else {
            Log.d("ButceFragment", "No expenses found")
        }
    }
}

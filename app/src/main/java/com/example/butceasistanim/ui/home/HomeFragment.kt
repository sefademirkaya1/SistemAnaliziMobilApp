package com.example.butceasistanim.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.butceasistanim.R
import com.example.butceasistanim.ui.butce.DatabaseHelper

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        displayBudgetInfo(view)

        return view
    }

    private fun displayBudgetInfo(view: View) {
        val dbHelper = DatabaseHelper(requireContext())   //requireContext() Fragment'in bağlı olduğu bağlamı (Context) döndürür.Yani Bütçe sınıfı
        val latestExpenses = dbHelper.getLatestExpenses()
        val totalIncome = latestExpenses?.get(DatabaseHelper.COLUMN_TOTAL_INCOME) ?: 0.0
        val totalExpenses = dbHelper.getTotalExpenses()

        view.findViewById<TextView>(R.id.textTotalIncome).text = "$totalIncome TL"
        view.findViewById<TextView>(R.id.textTotalExpense).text = "$totalExpenses TL"
        view.findViewById<TextView>(R.id.textRemainingBalance).text = "${totalIncome - totalExpenses} TL"
    }
}

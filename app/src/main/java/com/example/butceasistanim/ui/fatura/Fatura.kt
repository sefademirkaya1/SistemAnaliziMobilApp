package com.example.butceasistanim.ui.fatura

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.butceasistanim.MainActivity
import com.example.butceasistanim.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FaturaFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var buttonKaydet: Button
    private lateinit var editTextElektrik: EditText
    private lateinit var editTextSu: EditText
    private lateinit var editTextDogalgaz: EditText
    private lateinit var editTextKira: EditText
    private lateinit var editTextDiger: EditText

    private val requestScheduleExactAlarmPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    requireContext(),
                    "Alarm izni verilmedi. Fatura hatırlatıcı çalışmayacaktır.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fatura, container, false)

        dbHelper = DatabaseHelper(requireContext())

        editTextElektrik = view.findViewById(R.id.editTextElektrik)
        editTextSu = view.findViewById(R.id.editTextSu)
        editTextDogalgaz = view.findViewById(R.id.editTextDogalgaz)
        editTextKira = view.findViewById(R.id.editTextKira)
        editTextDiger = view.findViewById(R.id.editTextDiger)
        buttonKaydet = view.findViewById(R.id.buttonKaydet)

        buttonKaydet.setOnClickListener {
            Log.d("FaturaFragment", "Kaydet butonuna basıldı.")
            saveAndNotify(view)
        }

        editTextElektrik.setOnClickListener { showDatePickerDialog(editTextElektrik) }
        editTextSu.setOnClickListener { showDatePickerDialog(editTextSu) }
        editTextDogalgaz.setOnClickListener { showDatePickerDialog(editTextDogalgaz) }
        editTextKira.setOnClickListener { showDatePickerDialog(editTextKira) }
        editTextDiger.setOnClickListener { showDatePickerDialog(editTextDiger) }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFieldsForEmptyValues()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        editTextElektrik.addTextChangedListener(textWatcher)
        editTextSu.addTextChangedListener(textWatcher)
        editTextDogalgaz.addTextChangedListener(textWatcher)
        editTextKira.addTextChangedListener(textWatcher)
        editTextDiger.addTextChangedListener(textWatcher)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                requestScheduleExactAlarmPermission.launch(intent)
            }
        }

        loadSavedData()

        return view
    }

    private fun checkFieldsForEmptyValues() {
        buttonKaydet.isEnabled = !editTextElektrik.text.isNullOrEmpty() ||
                !editTextSu.text.isNullOrEmpty() ||
                !editTextDogalgaz.text.isNullOrEmpty() ||
                !editTextKira.text.isNullOrEmpty() ||
                !editTextDiger.text.isNullOrEmpty()
    }

    private fun saveAndNotify(view: View) {
        try {
            if (!editTextElektrik.text.isNullOrEmpty()) saveData("Elektrik", editTextElektrik.text.toString())
            if (!editTextSu.text.isNullOrEmpty()) saveData("Su", editTextSu.text.toString())
            if (!editTextDogalgaz.text.isNullOrEmpty()) saveData("Doğalgaz", editTextDogalgaz.text.toString())
            if (!editTextKira.text.isNullOrEmpty()) saveData("Kira", editTextKira.text.toString())
            if (!editTextDiger.text.isNullOrEmpty()) saveData("Diğer", editTextDiger.text.toString())

            if (!editTextElektrik.text.isNullOrEmpty()) setupNotification("Elektrik", editTextElektrik.text.toString())
            if (!editTextSu.text.isNullOrEmpty()) setupNotification("Su", editTextSu.text.toString())
            if (!editTextDogalgaz.text.isNullOrEmpty()) setupNotification("Doğalgaz", editTextDogalgaz.text.toString())
            if (!editTextKira.text.isNullOrEmpty()) setupNotification("Kira", editTextKira.text.toString())
            if (!editTextDiger.text.isNullOrEmpty()) setupNotification("Diğer", editTextDiger.text.toString())

            Toast.makeText(requireContext(), "Faturalar kaydedildi ve hatırlatıcılar kuruldu.", Toast.LENGTH_LONG).show()
        } catch (e: ParseException) {
            Log.e("FaturaFragment", "Geçersiz tarih formatı: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Lütfen geçerli bir tarih formatı girin.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("FaturaFragment", "Hata: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Bir hata oluştu: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun saveData(type: String, dueDate: String) {
        val success = dbHelper.insertData(type, dueDate)
        if (success) {
            Log.d("FaturaFragment", "$type faturası kaydedildi: $dueDate")
        } else {
            Log.d("FaturaFragment", "$type faturası kaydedilemedi.")
        }
    }

    private fun loadSavedData() {
        val dataList = dbHelper.getAllData()
        for (data in dataList) {
            when (data.first) {
                "Elektrik" -> editTextElektrik.setText(data.second)
                "Su" -> editTextSu.setText(data.second)
                "Doğalgaz" -> editTextDogalgaz.setText(data.second)
                "Kira" -> editTextKira.setText(data.second)
                "Diğer" -> editTextDiger.setText(data.second)
            }
        }
        checkFieldsForEmptyValues() // Kaydedilmiş veriler varsa butonun durumu güncellenir
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editText.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupNotification(type: String, dueDate: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dueDateMillis =
            sdf.parse(dueDate)?.time ?: throw ParseException("Geçersiz tarih formatı", 0)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(requireContext(), "fatura_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Fatura Hatırlatıcı")
            .setContentText("$type faturası için son ödeme tarihi: $dueDate")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        createNotificationChannel()

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notification_id", 1)
            putExtra("notification", notification)
        }

        val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            Log.d("FaturaFragment", "Alarm kuruluyor: $dueDateMillis")
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                dueDateMillis - (24 * 60 * 60 * 1000), // 1 gün önce
                alarmPendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("FaturaFragment", "Güvenlik hatası: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Güvenlik hatası: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Fatura Hatırlatıcı"
            val descriptionText = "Fatura hatırlatma bildirimleri"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("fatura_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = intent.getParcelableExtra<Notification>("notification")
            val notificationId = intent.getIntExtra("notification_id", 0)

            notification?.let {
                Log.d("AlarmReceiver", "Bildirim gösteriliyor: $notificationId")
                notificationManager.notify(notificationId, it)
            }
        }
    }
}

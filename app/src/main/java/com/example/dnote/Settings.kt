package com.example.dnote

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class Settings : Fragment() {

    private lateinit var switchDarkMode: Switch
    private lateinit var switchNotifications: Switch
    private lateinit var textNotificationTime: TextView
    private lateinit var llNotificationTime: View
    private lateinit var sharedPreferences: SharedPreferences
    private var notificationTime: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferences = requireContext().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        textNotificationTime = view.findViewById(R.id.textNotificationTime)
        llNotificationTime = view.findViewById(R.id.llNotificationTime)

        // Load dark mode preference
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModePreference(isChecked)
            switchTheme(isChecked)
        }

        // Load notification preference
        val isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false)
        switchNotifications.isChecked = isNotificationsEnabled
        llNotificationTime.isEnabled = isNotificationsEnabled

        val notificationHour = sharedPreferences.getInt("notification_hour", 21)
        val notificationMinute = sharedPreferences.getInt("notification_minute", 0)
        notificationTime.set(Calendar.HOUR_OF_DAY, notificationHour)
        notificationTime.set(Calendar.MINUTE, notificationMinute)
        updateNotificationTimeText()

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
            llNotificationTime.isEnabled = isChecked
            if (isChecked) {
                scheduleNotification()
            } else {
                cancelNotification()
            }
        }

        view.findViewById<View>(R.id.llNotificationTime).setOnClickListener {
            showTimePickerDialog()
        }

        // Set up language preference
        view.findViewById<View>(R.id.languagePreference).setOnClickListener {
            showLanguageSelectionDialog()
        }

        return view
    }

    private fun switchTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        // Smooth theme change with activity recreation
        Handler(Looper.getMainLooper()).postDelayed({
            AppCompatDelegate.setDefaultNightMode(mode)
            recreateActivity()
        }, 100)
    }

    private fun recreateActivity() {
        activity?.let {
            it.recreate()
        }
    }

    private fun saveDarkModePreference(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    private fun saveNotificationPreference(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isEnabled).apply()
    }

    private fun updateNotificationTimeText() {
        val hour = notificationTime.get(Calendar.HOUR_OF_DAY)
        val minute = notificationTime.get(Calendar.MINUTE)
        textNotificationTime.text = String.format("%02d:%02d", hour, minute)
    }

    private fun showTimePickerDialog() {
        val hour = notificationTime.get(Calendar.HOUR_OF_DAY)
        val minute = notificationTime.get(Calendar.MINUTE)

        val dialog = TimePickerDialog(
            requireContext(),
            R.style.CustomTimePickerDialog,
            { _, selectedHour, selectedMinute ->
                notificationTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                notificationTime.set(Calendar.MINUTE, selectedMinute)
                updateNotificationTimeText()
                saveNotificationTimePreference(selectedHour, selectedMinute)
                if (switchNotifications.isChecked) {
                    scheduleNotification()
                }
            },
            hour,
            minute,
            true
        )
        dialog.show()
    }

    private fun saveNotificationTimePreference(hour: Int, minute: Int) {
        sharedPreferences.edit().putInt("notification_hour", hour).putInt("notification_minute", minute).apply()
    }

    private fun scheduleNotification() {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            notificationTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelNotification() {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            getString(R.string.english),
            getString(R.string.ukrainian),
            getString(R.string.russian)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.choose_language)
            .setItems(languages) { _, which ->
                val locale = when (which) {
                    1 -> Locale("uk") // Ukrainian
                    2 -> Locale("ru") // Russian
                    else -> Locale("en") // English
                }
                setLocale(locale)
            }
            .create()
            .show()
    }

    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val resources = requireContext().resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the language preference
        sharedPreferences.edit().putString("language", locale.language).apply()

        // Minimize disruption by avoiding activity recreation
        refreshFragment()
    }

    private fun refreshFragment() {
        parentFragmentManager.beginTransaction().detach(this).commitNow()
        parentFragmentManager.beginTransaction().attach(this).commitNow()
    }
}

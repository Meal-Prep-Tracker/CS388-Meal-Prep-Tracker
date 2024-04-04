package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.Fragment

const val SHARED_PREFS = "SHARED_PREFS"

class SettingsFragment : Fragment() {
    private lateinit var darkModeSwitch: Switch
    private lateinit var notificationSwitch: Switch
    private lateinit var logoutButton: Button
    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        darkModeSwitch = view.findViewById(R.id.darkModeSwitch)
        notificationSwitch = view.findViewById(R.id.notificationSwitch)
        logoutButton = view.findViewById(R.id.logoutButton)

        sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        val notifications = sharedpreferences.getBoolean("notifications", true)

        darkModeSwitch.isChecked = darkMode
        notificationSwitch.isChecked = notifications

        // Dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update shared preferences value
            with(sharedpreferences.edit()) {
                putBoolean("darkMode", isChecked)
                apply()
            }
            // Apply the appropriate theme
            applyTheme(isChecked)
        }


        // Notifications switch listener
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update shared preferences value
            with(sharedpreferences.edit()) {
                putBoolean("notifications", isChecked)
                apply()
            }
        }

        logoutButton.setOnClickListener {
//            startActivity(Intent(this, WelcomeActivity::class.java))
        }

        return view
    }
    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val themeId = if (isDarkMode) {
            R.style.mealPrepThemeDark
        } else {
            R.style.mealPrepTheme
        }
        requireActivity().setTheme(themeId)
    }
}
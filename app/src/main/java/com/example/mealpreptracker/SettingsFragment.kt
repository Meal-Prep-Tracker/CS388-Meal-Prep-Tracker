package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {
    private lateinit var darkModeSwitch: Switch
    private lateinit var notificationButton: Button
    private lateinit var logoutButton: Button
    lateinit var sharedpreferences: SharedPreferences
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth.currentUser ?: run {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "SettingsFragment")
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        val notifications = sharedpreferences.getBoolean("notifications", false)

        val newThemeId = if (darkMode) R.style.mealPrepThemeDark else R.style.mealPrepTheme
        requireActivity().setTheme(newThemeId)

        auth = FirebaseAuth.getInstance()

        return view
    }
    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        darkModeSwitch = view.findViewById(R.id.darkModeSwitch)
        notificationButton = view.findViewById(R.id.notificationButton)
        logoutButton = view.findViewById(R.id.logoutButton)

        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        val notifications = sharedpreferences.getBoolean("notifications", false)

        if (darkMode) {
            darkModeSwitch.setChecked(true)
        } else {
            darkModeSwitch.setChecked(false)
        }

        // Dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update shared preferences value
            with(sharedpreferences.edit()) {
                putBoolean("darkMode", isChecked)
                apply()
            }
            Log.v("Settings", "Dark Mode: $isChecked")
            if (isChecked) {
                requireActivity().setTheme(R.style.mealPrepThemeDark)
            } else {
                requireActivity().setTheme(R.style.mealPrepTheme)
            }
            requireActivity().recreate()
        }

        notificationButton.setOnClickListener {
            askNotificationPermission()
        }

//        // Notifications switch listener
//        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
//            with(sharedpreferences.edit()) {
//                putBoolean("notifications", isChecked)
//                apply()
//            }
//            Log.v("Settings", "Notifications: $isChecked")
//            if (isChecked) {
//                askNotificationPermission()
//            } else {
//                Toast.makeText(requireActivity(), "Notifications are Disabled!", Toast.LENGTH_SHORT).show()
//            }
//
//        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedpreferences.edit()
            editor.putBoolean("notifications", true)
            editor.apply()
            Log.v("Settings", "Notifications: True")
        } else {
            Toast.makeText(requireActivity(), "Notifications are disabled!", Toast.LENGTH_SHORT).show()
            sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedpreferences.edit()
            editor.putBoolean("notifications", false)
            editor.apply()
            Log.v("Settings", "Notifications: False")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireActivity(), "Notifications are Enabled!", Toast.LENGTH_SHORT).show()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // When user already selected "Don't allow" for notifications. They must do it manually through device settings
                Toast.makeText(requireActivity(), "Please enable notifications through device settings", Toast.LENGTH_SHORT).show()
            } else {
                // Asking for permission for notifications
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
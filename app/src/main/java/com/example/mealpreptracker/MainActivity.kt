package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mealpreptracker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "MAIN"
const val SHARED_PREFS = "SHARED_PREFS"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var sharedpreferences: SharedPreferences
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val darkMode = sharedpreferences.getBoolean("darkMode", false)
        val notifications = sharedpreferences.getBoolean("notifications", false)

        if (darkMode) {
            // Apply dark theme
            setTheme(R.style.mealPrepThemeDark)
        } else {
            // Apply light theme
            setTheme(R.style.mealPrepTheme)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            this.startActivity(Intent(this, WelcomeActivity::class.java))
        }

        val dashboardFragment: Fragment = DashboardFragment()
        val mealsListFragment: Fragment = MealListFragment()

        val addMealListener = object : AddMealFragment.SetOnAddMealListener {
            override fun onAddMealClick() {
                binding.tvTitle.text = "Meals List"
                replaceFragment(mealsListFragment)
            }
        }

        val addMealFragment: Fragment = AddMealFragment(addMealListener)


        val profileFragment: Fragment = ProfileFragment()
        val settingsFragment: Fragment = SettingsFragment()


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.action_dashboard -> {
                    binding.tvTitle.text = "Dashboard"
                    fragment = dashboardFragment
                }
                R.id.action_meals_list -> {
                    binding.tvTitle.text = "Meals List"
                    fragment = mealsListFragment
                }
                R.id.action_add_meal -> {
                    binding.tvTitle.text = "Add Meal"
                    fragment = addMealFragment
                }
                R.id.action_profile -> {
                    binding.tvTitle.text = "Profile"
                    fragment = profileFragment
                }
                R.id.action_settings -> {
                    binding.tvTitle.text = "Settings"
                    fragment = settingsFragment
                }
            }
            replaceFragment(fragment)
            true
        }

        bottomNavigationView.selectedItemId = R.id.action_profile

    }

    private fun replaceFragment(mealFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_layout, mealFragment)
        fragmentTransaction.commit()
    }

}
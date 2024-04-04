package com.example.mealpreptracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private const val TAG = "MAIN"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        myRef.setValue(listOf("Hello, World!"))
//        // [END write_message]
//
//        // [START read_message]
//        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue<List<String>>()
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })

        val dashboardFragment: Fragment = DashboardFragment()
//        val mealsListFragment: Fragment = MealListFragment()
//        val addMealFragment: Fragment = AddMealFragment()
        val profileFragment: Fragment = ProfileFragment()
        val settingsFragment: Fragment = SettingsFragment()


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.action_dashboard -> fragment = dashboardFragment
//                R.id.action_meals_list -> fragment = mealsListFragment
//                R.id.action_add_meal -> fragment = addMealFragment
                R.id.action_profile -> fragment = profileFragment
                R.id.action_settings -> fragment = settingsFragment

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
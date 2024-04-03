package com.example.mealpreptracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

private const val TAG = "MealListFragment"

class MealListFragment: Fragment() {
    // Add these properties
    private val meals = mutableListOf<Meal>()
    private lateinit var mealsRecyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Change this statement to store the view in a variable instead of a return statement
        val view = inflater.inflate(R.layout.fragment_meal_list, container, false)

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        mealsRecyclerView = view.findViewById(R.id.mealsRv)
        mealsRecyclerView.layoutManager = layoutManager
        mealsRecyclerView.setHasFixedSize(true)
        mealAdapter = MealAdapter(view.context, meals)
        mealsRecyclerView.adapter = mealAdapter

        // Update the return statement to return the inflated view from above
        return view
    }

    companion object {
        fun newInstance(): MealListFragment {
            return MealListFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference
        // Call the new method within onViewCreated
        val mealsReference = database.child("Meals")
        fetchMeals(mealsReference)
    }

    private fun fetchMeals(mealsReference: DatabaseReference) {
        // Time to call the DB!!!!!!!!!!
        val mealListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                meals.clear()
                // Get Meal object and use the values to update the UI
                for(childSnapshot in dataSnapshot.children) {
                    meals.add(
                        Meal(
                            name = childSnapshot.child("name").value.toString(),
                            price = childSnapshot.child("price").value.toString().toDoubleOrNull()
                                ?: 0.0,
                            calories = childSnapshot.child("calories").value.toString().toDoubleOrNull()
                                ?: 0.0,
                            servings = childSnapshot.child("servings").value.toString().toIntOrNull()
                                ?: 0
                        )
                    )
                }

                mealAdapter.notifyDataSetChanged()
//                Log.w(TAG, meals.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mealsReference.addValueEventListener(mealListener)
    }

}
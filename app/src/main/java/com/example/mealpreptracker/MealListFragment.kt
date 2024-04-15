package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

private const val TAG = "MealListFragment"

class MealListFragment: Fragment() {
    // Add these properties
    private val meals = mutableListOf<Meal>()
    private lateinit var mealsRecyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth.currentUser ?: run {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "MealListFragment")
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val mealsReference = database.child("Meals").orderByChild("user_id").equalTo(auth.currentUser!!.uid)
        fetchMeals(mealsReference)
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
        database = Firebase.database.reference
        storageRef = Firebase.storage.reference
        mealsRecyclerView.layoutManager = layoutManager
        mealsRecyclerView.setHasFixedSize(true)
        mealAdapter = MealAdapter(view.context, meals, database, storageRef)
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
        // Call the new method within onViewCreated

    }

    private fun fetchMeals(mealsReference: Query) {
        // Time to call the DB!!!!!!!!!!
        val mealListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                meals.clear()
                // Get Meal object and use the values to update the UI
                for(childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(Meal::class.java)?.let {
                            meals.add(
                                it
                            )
                        }
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
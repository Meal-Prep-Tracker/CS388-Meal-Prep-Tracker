package com.example.mealpreptracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.launch

private const val TAG = "AddMealFragment"

class AddMealFragment : Fragment() {
    private lateinit var mealNameEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var addMealBtn: Button
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_meal, container, false)
        return view
    }

    companion object {
        fun newInstance(): AddMealFragment {
            return AddMealFragment()
        }
    }

    private fun AddNewMeal()
    {
        val key = database.child("Meals").push().key

        // error log
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for meals")
            return
        }


        // Make a new meal, nutritionSummary and add it to the realtime DB
        val meal = Meal(
            id = key,
            name = mealNameEditText.text.toString(),
            servings = servingsEditText.text.toString().toInt()
        )
        // Add to the meals collection
        database.child("Meals").child(key).setValue(meal)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // [START initialize_database_ref]
        database = Firebase.database.reference
        // Call new methods within onViewCreated
        mealNameEditText = view.findViewById(R.id.mealName)
        servingsEditText = view.findViewById(R.id.mealServings)
        addMealBtn = view.findViewById(R.id.addMealBtn)
        addMealBtn.setOnClickListener{
            AddNewMeal()
        }

        view.findViewById<Button>(R.id.pickDate).setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(parentFragmentManager, "datePicker")
        }
    }

}
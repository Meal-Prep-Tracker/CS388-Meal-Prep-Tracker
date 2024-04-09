package com.example.mealpreptracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar

private const val TAG = "AddMealFragment"
@SuppressLint("SimpleDateFormat")
val dateFormat = SimpleDateFormat("MM/dd/yyyy")

class AddMealFragment : Fragment() {
    private lateinit var mealNameEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var addMealBtn: Button
    private lateinit var database: DatabaseReference

    lateinit var mealDate: TextView
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

    private fun AddNewMeal() {
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
            servings = servingsEditText.text.toString().toInt(),
            date = dateFormat.parse(mealDate.text.toString())?.time
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
        mealDate = view.findViewById(R.id.mealDate)
        addMealBtn.setOnClickListener {
            AddNewMeal()
        }

        view.findViewById<Button>(R.id.pickDate).setOnClickListener {
            val cc = object : DatePickerFragment.OnDateSelectListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSelect(c: Calendar) {
                    mealDate.text =  "${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.YEAR)}"
                }
            }
            val newFragment = DatePickerFragment(cc)
            newFragment.show(parentFragmentManager, "datePicker")
        }
    }

}
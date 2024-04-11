package com.example.mealpreptracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "Dashboard"

class DashboardFragment : Fragment() {
    private lateinit var mealsEnteredText: TextView
    private lateinit var caloriesChart: LineChart
    private lateinit var expensesChart: LineChart
    private lateinit var foodGroupsChart: PieChart
    lateinit var calorieEntries: ArrayList<Entry>
    lateinit var expensesEntries: ArrayList<Entry>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mealsEnteredText = view.findViewById(R.id.mealEnteredText)
        caloriesChart = view.findViewById(R.id.caloriesChart)
        expensesChart = view.findViewById(R.id.expensesChart)
        foodGroupsChart = view.findViewById(R.id.foodGroupsChart)

        calorieEntries = ArrayList()
        expensesEntries = ArrayList()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        database = Firebase.database.reference

        var totalEntries = 0

        // Used to get the current Month
        val currentTime = System.currentTimeMillis()
        val firstDayOfMonth = getFirstDayOfMonth(currentTime)
        val lastDayOfMonth = getLastDayOfMonth(currentTime)

        // This retrieves all meals for the current month
        database.child("Meals")
            .orderByChild("user_id")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val mealsList = snapshot.children.mapNotNull { it.getValue(Meal::class.java) }

                //Filter meals for the current month
                val mealsForCurrentMonth = mealsList.filter { meal ->
                    meal.date in firstDayOfMonth..lastDayOfMonth
                }

                // Sorts meals by their epoch time
                val sortedMeals = mealsForCurrentMonth.sortedBy { it.date }

                sortedMeals.forEach { meal ->
                    // Assuming each Meal has a calorie and expense property
                    var epochTime = meal.date ?: 0L // Default to 0 if date is null
                    var date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(epochTime))
                    var calories = meal.calories!!.toFloat()
                    var price = meal.price!!.toFloat()
                    Log.v(TAG, "$date | $calories | $price | $uid")
                    calorieEntries.add(Entry(epochTime.toFloat(), calories))
                    expensesEntries.add(Entry(epochTime.toFloat(), price))
                }

                fetchCalorieData()
                fetchExpenseData()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting meal data", exception)
            }

        // Gets total number of meals
        database.child("Meals").orderByChild("user_id").equalTo(uid).get()
            .addOnSuccessListener {
                    snapshot ->

                val mealsList = snapshot.children.mapNotNull { it.getValue(Meal::class.java) }

                mealsList.forEach { meal ->
                    totalEntries += 1
                }

                mealsEnteredText.text = totalEntries.toString()

            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting meal data", it)
            }

        return view
    }
    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

    private fun fetchMeals() {
        lifecycleScope.launch(IO) {
            // do a sum on the number of meals a user entered
            var numMeals = 0

            withContext(Dispatchers.Main) {
                mealsEnteredText.text = numMeals.toString()
            }
        }
    }

    private fun fetchCalorieData() {
        lifecycleScope.launch(IO) {
            val calorieDataSet = LineDataSet(calorieEntries, "Calories")
            val calorieLineData = LineData(calorieDataSet)

            caloriesChart.data = calorieLineData

            calorieDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
            calorieDataSet.valueTextColor = Color.BLUE
            calorieDataSet.valueTextSize=16f
            calorieDataSet.color = ContextCompat.getColor(requireActivity(), R.color.secondary)
            calorieDataSet.setDrawFilled(true)

            caloriesChart.description.isEnabled = false
            caloriesChart.setTouchEnabled(true)
            caloriesChart.setPinchZoom(true)

            calorieLineData.setValueTextSize(12f)

            val legend = caloriesChart.legend
            legend.textSize = 16f

            val xAxis: XAxis = caloriesChart.xAxis

            xAxis.valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                override fun getFormattedValue(value: Float): String {
                    // Convert value (which is epoch time) to formatted date string
                    return dateFormat.format(Date(value.toLong()))
                }
            }

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setLabelCount(calorieEntries.size, true)

            val yAxis: YAxis = caloriesChart.axisLeft
            yAxis.setDrawGridLines(false)

            caloriesChart.invalidate()

        }
    }

    private fun fetchExpenseData() {
        lifecycleScope.launch(IO) {
            val expensesDataSet = LineDataSet(expensesEntries, "Price")
            val expensesLineData = LineData(expensesDataSet)

            expensesChart.data = expensesLineData

            expensesDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
            expensesDataSet.valueTextColor = Color.BLUE
            expensesDataSet.valueTextSize=16f
            expensesDataSet.color = ContextCompat.getColor(requireActivity(), R.color.secondary)
            expensesDataSet.setDrawFilled(true)

            expensesChart.description.isEnabled = false
            expensesChart.setTouchEnabled(true)
            expensesChart.setPinchZoom(true)

            expensesLineData.setValueTextSize(12f)

            val legend = expensesChart.legend
            legend.textSize = 16f

            val xAxis: XAxis = expensesChart.xAxis

            xAxis.valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                override fun getFormattedValue(value: Float): String {
                    return dateFormat.format(Date(value.toLong()))
                }
            }

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setLabelCount(expensesEntries.size, true)

            val yAxis: YAxis = expensesChart.axisLeft
            yAxis.setDrawGridLines(false)

            expensesChart.invalidate()

        }
    }
    private fun fetchFoodGroupData() {
        lifecycleScope.launch(IO) {
            val foodGroups = 0 // DAO to get food groups for this month

//            val foodGroupEntries = foodGroups.map { Entry(it.id.toFloat(), it.foodGroup) }

//            val foodGroupDataSet = PieDataSet(foodGroupEntires, "Food Groups")

//            foodGroupDataSet.setColors(ColorTemplate.MATERIAL_COLORS)

//            val foodGroupPieData = PieData(foodGroupDataSet)

//            foodGroupPieData.setValueTextSize(12f)
//
//            foodGroupsChart.description.isEnabled = false
//            foodGroupsChart.setTouchEnabled(true)
//            foodGroupsChart.setPinchZoom(true)
//
//            val legend = foodGroupsChart.legend
//            legend.textSize = 16f
//
//            val xAxis: XAxis = foodGroupsChart.xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//            val yAxis: YAxis = foodGroupsChart.axisLeft
//            yAxis.setDrawGridLines(false)
//
//            foodGroupsChart.data = calorieLineData
//
//            foodGroupsChart.invalidate()


        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchMeals()
        fetchCalorieData()
        fetchExpenseData()
        fetchFoodGroupData()
    }

    private fun getFirstDayOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    private fun getLastDayOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // Set to the last day of the month
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
}
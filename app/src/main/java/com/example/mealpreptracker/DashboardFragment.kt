package com.example.mealpreptracker

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
    lateinit var calorieEntries: ArrayList<Entry>
    lateinit var expensesEntries: ArrayList<Entry>
    private lateinit var mealNames: List<String>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    lateinit var sharedpreferences: SharedPreferences
    var darkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealNames = listOf()

        sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        darkMode = sharedpreferences.getBoolean("darkMode", false)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        auth.currentUser ?: run {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.putExtra(SOURCE_EXTRA, "ProfileFragment")
            startActivity(intent)
            activity?.finish()
        }

        mealsEnteredText = view.findViewById(R.id.mealEnteredText)
        caloriesChart = view.findViewById(R.id.caloriesChart)
        expensesChart = view.findViewById(R.id.expensesChart)
//        foodGroupsChart = view.findViewById(R.id.foodGroupsChart)

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
                mealNames = sortedMeals.map { meal -> meal.name!! }

                var count = 0

                sortedMeals.forEach { meal ->

                    var servings = meal.servings
                    // Assuming each Meal has a calorie and expense property
                    database.child("Ingredients").orderByChild("meal_id").equalTo(meal.id).get()
                        .addOnSuccessListener { snapshot ->
                            // Get the ingredients of the meal u pass from Main Activity in your Intent
                            val ingredients = snapshot.children.map { dataSnapshot ->
                                dataSnapshot.getValue(Ingredient::class.java)
                            }

                            var calories = ((ingredients.sumOf { it?.nutritionSummary?.calories ?: 0.0 }) / servings!!).toFloat()
                            var price = ingredients.sumOf { it?.price ?: 0.0 }.toFloat()

                            var epochTime = meal.date ?: 0L // Default to 0 if date is null
                            var date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(epochTime))

                            calorieEntries.add(Entry(count.toFloat(), calories))
                            expensesEntries.add(Entry(count.toFloat(), price))
                            Log.v(TAG, "Date: $date | Calories: $calories | Price: $price | Servings: $servings | uid: $uid")

                            count += 1

                            Log.v(TAG, calorieEntries.toString())
                            updateCalorieChart()
                            updateExpenseChart()

                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Error getting total cals and price of meal")
                        }
//                    var epochTime = meal.date ?: 0L // Default to 0 if date is null
//                    var date =
//                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(epochTime))
//                    var calories = meal.calories!!.toFloat()
//                    var price = meal.price!!.toFloat()
//                    Log.v(TAG, "$date | $calories | $price | $uid")
//                    calorieEntries.add(Entry(count.toFloat(), calories))
//                    expensesEntries.add(Entry(count.toFloat(), price))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting meal data", exception)
            }

        // Gets total number of meals
        database.child("Meals").orderByChild("user_id").equalTo(uid).get()
            .addOnSuccessListener { snapshot ->

                val mealsList = snapshot.children.mapNotNull { it.getValue(Meal::class.java) }

                mealsList.forEach { _ ->
                    totalEntries += 1
                }

                mealsEnteredText.text = totalEntries.toString()

            }
            .addOnFailureListener {
                Log.e(TAG, "Error getting meal data", it)
            }

        return view
    }

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

//    private fun updateMealsChart() {
//        lifecycleScope.launch(IO) {
//            // do a sum on the number of meals a user entered
//            var numMeals = 0
//
//            withContext(Dispatchers.Main) {
//                mealsEnteredText.text = numMeals.toString()
//            }
//
//        }
//    }

    private fun updateCalorieChart() {
        lifecycleScope.launch(IO) {
            val calorieDataSet = LineDataSet(calorieEntries, "Calories")
            val calorieLineData = LineData(calorieDataSet)

            caloriesChart.data = calorieLineData
            caloriesChart.legend.isEnabled = false

            if (darkMode) {
                calorieDataSet.valueTextColor = Color.WHITE

                val legend = caloriesChart.legend
                        legend.textColor = Color.WHITE

                val xAxis: XAxis = caloriesChart.xAxis
                xAxis.textColor = Color.WHITE

                val yAxis: YAxis = caloriesChart.axisLeft
                yAxis.textColor = Color.WHITE

            } else {
                calorieDataSet.valueTextColor = Color.BLUE

                val legend = caloriesChart.legend
                        legend.textColor = Color.BLACK

                val xAxis: XAxis = caloriesChart.xAxis
                xAxis.textColor = Color.BLACK

                val yAxis: YAxis = caloriesChart.axisLeft
                yAxis.textColor = Color.BLACK
            }

            calorieDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
            calorieDataSet.valueTextSize = 16f
            calorieDataSet.color = ContextCompat.getColor(requireActivity(), R.color.secondary)
            calorieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD)

            caloriesChart.axisRight.isEnabled = false
            caloriesChart.description.isEnabled = false
            caloriesChart.setTouchEnabled(true)
            caloriesChart.setPinchZoom(true)

            calorieLineData.setValueTextSize(12f)

            val legend = caloriesChart.legend
            legend.textSize = 16f

            val xAxis: XAxis = caloriesChart.xAxis

//            xAxis.valueFormatter = object : ValueFormatter() {
//                private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//
//                override fun getFormattedValue(value: Float): String {
//                    // Convert value (which is epoch time) to formatted date string
//                    return dateFormat.format(Date(value.toLong()))
//                }
//            }

            xAxis.valueFormatter = IndexAxisValueFormatter(mealNames)
            xAxis.labelRotationAngle = 45f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setLabelCount(calorieEntries.size, true)

            val yAxis: YAxis = caloriesChart.axisLeft
            yAxis.setDrawGridLines(false)

            caloriesChart.invalidate()

        }
    }

    private fun updateExpenseChart() {
        lifecycleScope.launch(IO) {
            val expensesDataSet = LineDataSet(expensesEntries, "Price")
            val expensesLineData = LineData(expensesDataSet)

            expensesChart.data = expensesLineData
            caloriesChart.legend.isEnabled = false

            if (darkMode) {
                expensesDataSet.valueTextColor = Color.WHITE

                val legend = expensesChart.legend
                legend.textColor = Color.WHITE

                val xAxis: XAxis = expensesChart.xAxis
                xAxis.textColor = Color.WHITE

                val yAxis: YAxis = expensesChart.axisLeft
                yAxis.textColor = Color.WHITE

            } else {
                expensesDataSet.valueTextColor = Color.BLUE

                val legend = caloriesChart.legend
                legend.textColor = Color.BLACK

                val xAxis: XAxis = caloriesChart.xAxis
                xAxis.textColor = Color.BLACK

                val yAxis: YAxis = caloriesChart.axisLeft
                yAxis.textColor = Color.BLACK
            }

            expensesDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
            expensesDataSet.valueTextSize = 16f
            expensesDataSet.color = ContextCompat.getColor(requireActivity(), R.color.secondary)
//            expensesDataSet.setDrawFilled(true)
            expensesDataSet.setValueTypeface(Typeface.DEFAULT_BOLD)

            expensesChart.axisRight.isEnabled = false
            expensesChart.description.isEnabled = false
            expensesChart.setTouchEnabled(true)
            expensesChart.setPinchZoom(true)

            expensesLineData.setValueTextSize(12f)

            val legend = expensesChart.legend
            legend.textSize = 16f

            val xAxis: XAxis = expensesChart.xAxis

//            xAxis.valueFormatter = object : ValueFormatter() {
//                private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//
//                override fun getFormattedValue(value: Float): String {
//                    return dateFormat.format(Date(value.toLong()))
//                }
//            }

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(mealNames)
            xAxis.labelRotationAngle = 45f
            xAxis.setLabelCount(expensesEntries.size, true)

            val yAxis: YAxis = expensesChart.axisLeft
            yAxis.setDrawGridLines(false)

            expensesChart.invalidate()

        }
    }

//    private fun updateFoodGroupChart() {
//        lifecycleScope.launch(Dispatchers.Main) {
//            val foodGroupEntries = ArrayList<PieEntry>()
//            foodGroupEntries.add(PieEntry(35f, "Fruit"))
//            foodGroupEntries.add(PieEntry(40f, "Vegetables"))
//            foodGroupEntries.add(PieEntry(80f, "Protein"))
//            foodGroupEntries.add(PieEntry(60f, "Dairy"))
//            foodGroupEntries.add(PieEntry(25f, "Grains"))
//
//            val foodGroupDataSet = PieDataSet(foodGroupEntries, "")
//            val foodGroupPieData = PieData(foodGroupDataSet)
//
//            foodGroupDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
//            foodGroupPieData.setValueTextSize(14f)
//
//            foodGroupsChart.setEntryLabelColor(Color.BLACK)
//            foodGroupsChart.data = foodGroupPieData
//            foodGroupsChart.getDescription().setEnabled(false)
//            foodGroupsChart.animateY(1000)
//            foodGroupsChart.invalidate()
//
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCalorieChart()
        updateExpenseChart()
//        updateFoodGroupChart()
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
        calendar.set(
            Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        ) // Set to the last day of the month
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
}
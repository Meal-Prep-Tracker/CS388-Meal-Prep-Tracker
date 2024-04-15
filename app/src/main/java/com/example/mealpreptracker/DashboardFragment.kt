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
import android.widget.FrameLayout
import android.widget.ProgressBar
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
import kotlinx.coroutines.tasks.await
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
    private lateinit var progressBar: ProgressBar
    private lateinit var stats: FrameLayout
    private lateinit var caloriesTitle: TextView
    private lateinit var expensesTitle: TextView

    lateinit var sharedpreferences: SharedPreferences
    var darkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mealNames = listOf()

        sharedpreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        darkMode = sharedpreferences.getBoolean("darkMode", false)
        auth = FirebaseAuth.getInstance()

    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        caloriesChart.visibility = View.GONE
        expensesChart.visibility = View.GONE
        stats.visibility = View.GONE
        caloriesTitle.visibility = View.GONE
        expensesTitle.visibility = View.GONE

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        database = Firebase.database.reference

        var totalEntries = 0

        // Used to get the current Month
        val currentTime = System.currentTimeMillis()
        val firstDayOfMonth = getFirstDayOfMonth(currentTime)
        val lastDayOfMonth = getLastDayOfMonth(currentTime)

        lifecycleScope.launch(IO) {
            try {
                val mealsSnapshot = withContext(IO) {
                    database.child("Meals")
                        .orderByChild("user_id")
                        .equalTo(uid)
                        .get()
                        .await()
                }
                val mealsList = mealsSnapshot.children.mapNotNull { it.getValue(Meal::class.java) }

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
                    val ingredientsSnapshot = withContext(IO) {
                        database.child("Ingredients").orderByChild("meal_id").equalTo(meal.id).get().await()
                    }
                    val ingredients = ingredientsSnapshot.children.map { dataSnapshot ->
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
                }

                // Get total number of meals
                val mealsCountSnapshot = withContext(IO) {
                    database.child("Meals").orderByChild("user_id").equalTo(uid).get().await()
                }
                val mealsCountList = mealsCountSnapshot.children.mapNotNull { it.getValue(Meal::class.java) }
                totalEntries = mealsCountList.size

                withContext(Dispatchers.Main) {
                    // Hide progress bar and show charts
                    progressBar.visibility = View.GONE
                    caloriesChart.visibility = View.VISIBLE
                    expensesChart.visibility = View.VISIBLE
                    stats.visibility = View.VISIBLE
                    caloriesTitle.visibility = View.VISIBLE
                    expensesTitle.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving data: ${e.message}")
            }

            // Update UI on the main thread
            withContext(Dispatchers.Main) {
                mealsEnteredText.text = totalEntries.toString()
                updateCalorieChart()
                updateExpenseChart()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        stats = view.findViewById(R.id.dashboardStats)
        caloriesTitle = view.findViewById(R.id.caloriesTitle)
        expensesTitle = view.findViewById(R.id.expensesTitle)

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
            caloriesChart.extraBottomOffset = 64f
            caloriesChart.extraLeftOffset = 12f
            caloriesChart.extraRightOffset = 12f

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
            expensesChart.legend.isEnabled = false

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
            expensesChart.extraBottomOffset = 64f
            expensesChart.extraRightOffset = 12f
            expensesChart.extraLeftOffset = 12f

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
package com.example.mealpreptracker

import android.os.Bundle
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
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {
    private lateinit var mealsEnteredText: TextView
    private lateinit var caloriesChart: LineChart
    private lateinit var expensesChart: LineChart
    private lateinit var foodGroupsChart: PieChart

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
            val weeklyCalories = 0 // DAO to get calories per week

//            val calorieEntries = weeklyCalories.map { Entry(it.id.toFloat(), it.calorie) }

//            val calorieDataSet = LineDataSet(calorieEntries, "Calories")

//            calorieDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)

//            val calorieLineData = LineData(calorieDataSet)

//            calorieLineData.setValueTextSize(12f)
//
//            caloriesChart.description.isEnabled = false
//            caloriesChart.setTouchEnabled(true)
//            caloriesChart.setPinchZoom(true)
//
//            val legend = caloriesChart.legend
//            legend.textSize = 16f
//
//            val xAxis: XAxis = caloriesChart.xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//            val yAxis: YAxis = caloriesChart.axisLeft
//            yAxis.setDrawGridLines(false)
//
//            caloriesChart.data = calorieLineData
//
//            caloriesChart.invalidate()

        }
    }

    private fun fetchExpenseData() {
        lifecycleScope.launch(IO) {
            val weeklyExpenses = 0 // DAO to get expenses per week

//            val expenseEntries = weeklyExpenses.map { Entry(it.id.toFloat(), it.expense) }

//            val expensesDataSet = LineDataSet(expenseEntries, "Expenses")

//            expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)

//            val expenseLineData = LineData(expensesDataSet)

//            expenseLineData.setValueTextSize(12f)
//
//            expensesChart.description.isEnabled = false
//            expensesChart.setTouchEnabled(true)
//            expensesChart.setPinchZoom(true)
//
//            val legend = expensesChart.legend
//            legend.textSize = 16f
//
//            val xAxis: XAxis = expensesChart.xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//            val yAxis: YAxis = expensesChart.axisLeft
//            yAxis.setDrawGridLines(false)
//
//            expensesChart.data = expenseLineData
//
//            expensesChart.invalidate()

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

}
package com.wellness.habittracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.wellness.habittracker.R
import com.wellness.habittracker.utils.PreferencesHelper

class TrendsFragment : Fragment() {
    private lateinit var prefsHelper: PreferencesHelper
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsHelper = PreferencesHelper(requireContext())
        
        val tvAvgMood = view.findViewById<TextView>(R.id.tvAvgMoodTrend)
        val tvGoodDays = view.findViewById<TextView>(R.id.tvGoodDays)
        val tvDaysTracked = view.findViewById<TextView>(R.id.tvDaysTracked)
        lineChart = view.findViewById(R.id.lineChartTrends)
        barChart = view.findViewById(R.id.barChartTrends)
        
        val moodEntries = prefsHelper.getMoodEntries()
        
        if (moodEntries.isNotEmpty()) {
            val avgMood = moodEntries.map { it.mood }.average()
            tvAvgMood.text = String.format("%.1f", avgMood)
            
            val goodDays = moodEntries.count { it.mood >= 4 }
            tvGoodDays.text = goodDays.toString()
            
            tvDaysTracked.text = moodEntries.distinctBy { it.date }.size.toString()
            
            // Setup charts
            setupLineChart(moodEntries)
            setupBarChart(moodEntries)
        } else {
            tvAvgMood.text = "0.0"
            tvGoodDays.text = "0"
            tvDaysTracked.text = "0"
            
            // Show empty charts
            setupEmptyCharts()
        }
    }
    
    private fun setupLineChart(entries: List<com.wellness.habittracker.models.MoodEntry>) {
        // Sort by timestamp to show chronological order
        val sortedEntries = entries.sortedBy { it.timestamp }
        
        // Take last 30 entries for readability
        val recentEntries = if (sortedEntries.size > 30) {
            sortedEntries.takeLast(30)
        } else {
            sortedEntries
        }
        
        val lineEntries = recentEntries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.mood.toFloat())
        }
        
        val lineDataSet = LineDataSet(lineEntries, "Mood Over Time").apply {
            color = Color.parseColor("#5B9EF8")
            setCircleColor(Color.parseColor("#5B9EF8"))
            circleRadius = 4f
            lineWidth = 2.5f
            setDrawValues(false)
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        
        lineChart.apply {
            data = LineData(lineDataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }
            
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 6f
                granularity = 1f
            }
            
            axisRight.isEnabled = false
            
            animateX(800)
            invalidate()
        }
    }
    
    private fun setupBarChart(entries: List<com.wellness.habittracker.models.MoodEntry>) {
        // Count frequency of each mood level (1-5)
        val moodCounts = (1..5).map { mood ->
            entries.count { it.mood == mood }
        }
        
        val barEntries = moodCounts.mapIndexed { index, count ->
            BarEntry((index + 1).toFloat(), count.toFloat())
        }
        
        val barDataSet = BarDataSet(barEntries, "Mood Distribution").apply {
            colors = listOf(
                Color.parseColor("#EF4444"), // Mood 1 - Red
                Color.parseColor("#F97316"), // Mood 2 - Orange
                Color.parseColor("#EAB308"), // Mood 3 - Yellow
                Color.parseColor("#22C55E"), // Mood 4 - Green
                Color.parseColor("#10B981")  // Mood 5 - Bright Green
            )
            valueTextSize = 12f
            setDrawValues(true)
        }
        
        barChart.apply {
            data = BarData(barDataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                axisMinimum = 0.5f
                axisMaximum = 5.5f
            }
            
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
            }
            
            axisRight.isEnabled = false
            
            animateY(800)
            invalidate()
        }
    }
    
    private fun setupEmptyCharts() {
        // Show empty state for line chart
        lineChart.apply {
            clear()
            description.text = "No mood data yet. Add mood entries to see trends!"
            description.isEnabled = true
            invalidate()
        }
        
        // Show empty state for bar chart
        barChart.apply {
            clear()
            description.text = "No mood data yet. Add mood entries to see distribution!"
            description.isEnabled = true
            invalidate()
        }
    }
}

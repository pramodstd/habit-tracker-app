package com.wellness.habittracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.google.android.material.card.MaterialCardView
import com.wellness.habittracker.R
import com.wellness.habittracker.adapters.MoodEntryAdapter
import com.wellness.habittracker.models.MoodEntry
import com.wellness.habittracker.utils.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MoodFragment : Fragment() {

    private lateinit var prefs: PreferencesHelper
    private lateinit var adapter: MoodEntryAdapter

    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mood, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesHelper(requireContext())

        // IDs from your fragment_mood.xml
        rv = view.findViewById(R.id.rvMoodEntries)
        tvEmpty = view.findViewById(R.id.tvNoMoodEntries)
        lineChart = view.findViewById(R.id.lineChartMood)
        barChart = view.findViewById(R.id.barChartMood)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MoodEntryAdapter(
            mutableListOf(),
            onEdit = { showAddEditDialog(it) },
            onDelete = { deleteEntry(it) }
        )
        rv.adapter = adapter

        view.findViewById<View>(R.id.btnAddMood).setOnClickListener { showAddEditDialog(null) }

        refresh()
    }

    private fun refresh() {
        val data = prefs.getMoodEntries().sortedBy { it.timestamp }
        adapter.setData(data)

        val hasData = data.isNotEmpty()
        tvEmpty.visibility = if (hasData) View.GONE else View.VISIBLE
        rv.visibility = if (hasData) View.VISIBLE else View.GONE
        if (hasData) updateCharts(data) else clearCharts()
    }

    private fun clearCharts() {
        lineChart.data = null
        barChart.data = null
        lineChart.invalidate()
        barChart.invalidate()
    }

    private fun updateCharts(entries: List<MoodEntry>) {
        // Line: mood over time
        val linePts = entries.mapIndexed { i, e -> Entry(i.toFloat(), e.mood.toFloat()) }
        val lineSet = LineDataSet(linePts, getString(R.string.mood_over_time)).apply {
            setDrawCircles(true)
            circleRadius = 3f
            lineWidth = 2f
            valueTextSize = 9f
        }
        lineChart.data = LineData(lineSet)
        lineChart.invalidate()

        // Bar: count per mood level (1..5)
        val counts = (1..5).map { m -> entries.count { it.mood == m } }
        val barEntries = counts.mapIndexed { idx, c -> BarEntry((idx + 1).toFloat(), c.toFloat()) }
        val barSet = BarDataSet(barEntries, getString(R.string.mood_levels))
        barChart.data = BarData(barSet)
        barChart.invalidate()
    }

    private fun showAddEditDialog(editing: MoodEntry?) {
        val ctx = requireContext()
        val dialog = layoutInflater.inflate(R.layout.dialog_add_mood, null)

        val np = dialog.findViewById<NumberPicker>(R.id.npMood).apply {
            minValue = 1; maxValue = 5; value = editing?.mood ?: 3
        }
        val et = dialog.findViewById<EditText>(R.id.etNote).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setText(editing?.note ?: "")
        }

        // Tap cards to select mood (and highlight selection)
        val cardIds = listOf(
            R.id.cardMood1 to 1, R.id.cardMood2 to 2,
            R.id.cardMood3 to 3, R.id.cardMood4 to 4, R.id.cardMood5 to 5
        )
        fun highlight(selected: Int) {
            cardIds.forEach { (id, value) ->
                dialog.findViewById<MaterialCardView>(id)?.apply {
                    strokeWidth = 2
                    strokeColor = if (value == selected) 0xFF5B9EF8.toInt() else 0x00000000
                }
            }
        }
        highlight(np.value)
        cardIds.forEach { (id, value) ->
            dialog.findViewById<View>(id)?.setOnClickListener {
                np.value = value; highlight(value)
            }
        }

        AlertDialog.Builder(ctx)
            .setTitle(if (editing == null) R.string.add_mood else R.string.edit_mood)
            .setView(dialog)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { d, _ ->
                val now = Date()
                val dfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val entry = MoodEntry(
                    id = editing?.id ?: UUID.randomUUID().toString(),
                    date = dfDate.format(now),
                    time = dfTime.format(now),
                    mood = np.value,
                    note = et.text.toString().trim(),
                    timestamp = editing?.timestamp ?: System.currentTimeMillis()
                )
                val list = prefs.getMoodEntries().toMutableList()
                val idx = list.indexOfFirst { it.id == entry.id }
                if (idx >= 0) list[idx] = entry else list.add(entry)
                prefs.saveMoodEntries(list)
                Toast.makeText(ctx, R.string.saved, Toast.LENGTH_SHORT).show()
                refresh()
                d.dismiss()
            }
            .show()
    }

    private fun deleteEntry(e: MoodEntry) {
        val list = prefs.getMoodEntries().toMutableList().apply {
            removeAll { it.id == e.id }
        }
        prefs.saveMoodEntries(list)
        refresh()
    }
}
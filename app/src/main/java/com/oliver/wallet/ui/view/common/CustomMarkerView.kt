package com.oliver.wallet.ui.view.common

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.oliver.wallet.R
import com.oliver.wallet.util.DateValueFormatter
import com.oliver.wallet.util.toDecimalFormat

class CustomMarkerView(context: Context) : MarkerView(context, R.layout.marker_view) {

    private var list: List<Entry> = listOf()

    private val money: TextView = findViewById(R.id.money)
    private val data: TextView = findViewById(R.id.data)

    fun setMarkerView(listItems: List<Entry>?) {
        listItems ?: return
        list = listItems
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        entry?.let {
            money.text = list[it.x.toInt()].y.toDecimalFormat()
            data.text = DateValueFormatter().getAxisLabel(it.x, null)
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}
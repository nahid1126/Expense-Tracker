package com.nahid.expensetracker.screens

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.nahid.expensetracker.R
import com.nahid.expensetracker.Utils
import com.nahid.expensetracker.ui.theme.Zinc
import com.nahid.expensetracker.view_model.StatsViewModel
import com.nahid.expensetracker.view_model.StatsViewModelFactory

@Composable
fun StatsScreen(rememberNavController: NavHostController) {
    val statsViewModel: StatsViewModel =
        StatsViewModelFactory(rememberNavController.context).create(StatsViewModel::class.java)
    val expenseByDateList by statsViewModel.expenseList.collectAsState()
    val topExpenseList by statsViewModel.topExpenseList.collectAsState()
    val context = LocalContext.current
    Scaffold(topBar = {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { rememberNavController.popBackStack() },
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Zinc)
            )
            Text(
                text = "Statistics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Zinc,
                modifier = Modifier
                    .padding(16.dp)
                    .align(
                        Alignment.Center
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Zinc)
            )

        }    }) {
        Column(modifier = Modifier.padding(it)) {
            val entries = statsViewModel.getEntriesForChart(expenseByDateList)
            LineChart(entries)
            Spacer(modifier = Modifier.height(16.dp))
            TransactionList(
                Modifier.padding(horizontal = 16.dp,vertical = 8.dp),
                topExpenseList,
                "Top Spending",
                rememberNavController
            )
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>) {
    val context = LocalContext.current
    AndroidView(factory = {
        val view = LayoutInflater.from(context).inflate(R.layout.state_line_chart, null)
        view
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
    ) { view ->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val dataset = LineDataSet(entries, "Expense").apply {
            color = android.graphics.Color.parseColor("#FF2F7E79")
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 3f
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawFilled(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.parseColor("#FF2F7E79")
            fillDrawable = context.getDrawable(R.drawable.gradient_color)
        }
        lineChart.data = com.github.mikephil.charting.data.LineData(dataset)
        lineChart.apply {
            xAxis.valueFormatter =
                object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return Utils.formatDateFormat(value.toLong())
                    }
                }
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
        }

        lineChart.invalidate()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStatsScreen() {
    StatsScreen(rememberNavController())
}
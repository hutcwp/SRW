package com.example.srw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.srw.view.LineView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val yList = mutableListOf<String>();
        yList.add("100%")
        yList.add("80%")
        yList.add("60%")
        yList.add("40%")
        yList.add("20%")
        yList.add("0.0%")
        val time = getCurrentWeekDay()
        val lineView = findViewById<LineView>(R.id.lineView)
        val xList = getCurrentWeekDay()
        lineView?.setViewData(yList, xList)


        updateEndTime(lineView)
    }

    private fun updateEndTime(lineView: LineView?) {
        Handler(Looper.myLooper()!!).postDelayed({
            lineView?.endTime = 2f + lineView?.endTime!!
            Log.i("hutcwp", "endTime = ${lineView?.endTime}")
            updateEndTime(lineView)
        }, 100)
    }

    /**
     * 获取最近一周的时间 MM-dd
     */
    private fun getCurrentWeekDay(): List<String> {
        val data = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val sdf = SimpleDateFormat("MM-dd")
        for (i in 0..7) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            data.add(sdf.format(calendar.time))
        }
        return data
    }

}
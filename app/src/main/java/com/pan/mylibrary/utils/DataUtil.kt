package com.pan.mylibrary.utils

import com.pan.mylibrary.widget.chart.IData
import java.util.*

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
object DataUtil {
    fun ints(size: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until size) {
            list.add(i)
        }
        return list
    }

    fun generateRandomData(bound:Int = 1000,size: Int = 10): ArrayList<IData> {
        val list = ArrayList<IData>()
        val random = Random()
        for (i in 0..size) {
            val int =  random.nextInt(bound)
            val data = object :IData{
                override fun getDataValue(): Int  = int
                override fun getDataLabel(): String = "S$i"
            }
            list.add(data)
        }
        return list
    }
}

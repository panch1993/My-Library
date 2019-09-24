package com.pan.mylibrary.ui.fragment

import com.pan.mylibrary.R
import com.pan.mylibrary.ui.widget.ChartView
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart_view.*

/**
 * Create by panchenhuan on 2019-09-23 15:48
 * walkwindc8@foxmail.com
 * Description:
 */
class ChartViewFragment:BaseFragment() {
    override fun getLayoutId(): Int = R .layout.fragment_chart_view

    override fun initFragment() {

    }

    override fun initView() {
        val list = ArrayList<ChartView.IData>()
        for (i in 0..4) {
            val data = object :ChartView.IData{
                override fun getDataValue(): Int  = i
                override fun getDataLabel(): String = "INDEX$i"
            }
            list.add(data)
        }
        chart_view.setData(list)
    }
}
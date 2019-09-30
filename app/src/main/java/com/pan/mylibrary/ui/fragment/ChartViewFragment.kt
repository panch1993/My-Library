package com.pan.mylibrary.ui.fragment

import android.view.View
import com.pan.mylibrary.R
import com.pan.mylibrary.utils.DataUtil
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart_view.*

/**
 * Create by panchenhuan on 2019-09-23 15:48
 * walkwindc8@foxmail.com
 * Description:
 */
class ChartViewFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_chart_view

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_random, bt_anim)
        chart_view.setNewData(DataUtil.generateRandomData())
    }

    override fun onClick(v: View) {
        when (v) {
            bt_random -> chart_view.setNewData(DataUtil.generateRandomData())
            bt_anim -> chart_view.playAnim()
        }
    }
}
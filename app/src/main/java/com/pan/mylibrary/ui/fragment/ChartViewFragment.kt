package com.pan.mylibrary.ui.fragment

import android.view.View
import android.widget.SeekBar
import com.pan.mylibrary.R
import com.pan.mylibrary.utils.DataUtil
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart_view.*

/**
 * Create by panchenhuan on 2019-09-23 15:48
 * walkwindc8@foxmail.com
 * Description:
 */
class ChartViewFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        chart_view.setNewData(DataUtil.generateRandomData(100,progress))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun getLayoutId(): Int = R.layout.fragment_chart_view

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_random, bt_anim)
        chart_view.setNewData(DataUtil.generateRandomData())
        seek_bar.setOnSeekBarChangeListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            bt_random -> chart_view.setNewData(DataUtil.generateRandomData(size = seek_bar.progress))
            bt_anim -> chart_view.playAnim()
        }
    }
}
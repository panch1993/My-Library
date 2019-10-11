package com.pan.mylibrary.ui.fragment

import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import com.pan.mylibrary.R
import com.pan.mylibrary.utils.DataUtil
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_radart_view.*

/**
 * Create by panchenhuan on 2019-09-23 15:48
 * walkwindc8@foxmail.com
 * Description:
 */
class RadarViewFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener,
    CompoundButton.OnCheckedChangeListener {


    override fun getLayoutId(): Int = R.layout.fragment_radart_view

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_random, bt_anim)

        seek_bar.setOnSeekBarChangeListener(this)

        cb_1.setOnCheckedChangeListener(this)
        cb_2.setOnCheckedChangeListener(this)
    }

    override fun onFirstVisibleToUser() {
        radar_view.setNewData(DataUtil.generateRandomData(100, 5))
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        radar_view.setNewData(DataUtil.generateRandomData(100, progress))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView) {
            cb_1 -> radar_view.isDrawCirBg = isChecked
            cb_2 -> radar_view.isDrawPolygonBg = isChecked
        }
    }

    override fun onClick(v: View) {
        when (v) {
            bt_random -> radar_view.setNewData(DataUtil.generateRandomData(100, seek_bar.progress))
            bt_anim -> radar_view.playAnim()
        }
    }
}
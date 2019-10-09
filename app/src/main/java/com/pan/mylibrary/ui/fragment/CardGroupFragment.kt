package com.pan.mylibrary.ui.fragment

import android.view.View
import com.pan.mylibrary.R
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_card_group.*

/**
 * Create by panchenhuan on 2019-09-23 15:48
 * walkwindc8@foxmail.com
 * Description:
 */
class CardGroupFragment : BaseFragment() {


    override fun getLayoutId(): Int = R.layout.fragment_card_group

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_state)

        cg.setData(listOf(1, 1, 1, 1, 1, 1, 1))
//        radar_view.setNewData(DataUtil.generateRandomData(100, 5))
    }

    override fun onClick(v: View) {
        when (v) {
            bt_state -> {
                if (bt_state.text == "START") {
                    cg.setInterceptHander(false)
                    bt_state.text = "PAUSE"
                } else {
                    cg.setInterceptHander(true)
                    bt_state.text = "START"
                }
            }
        }
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean, invokeInResumeOrPause: Boolean) {
        super.onVisibleToUserChanged(isVisibleToUser, invokeInResumeOrPause)
        if (isVisibleToUser) {
            if (bt_state.text == "PAUSE") {
                cg.setInterceptHander(false)
            }
        } else {
            cg.setInterceptHander(true)
        }
    }
}
package com.pan.mylibrary.ui.fragment

import android.view.View
import com.pan.mylibrary.R
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_touch.*

/**
 * Create by panchenhuan on 2019-12-30
 * walkwindc8@foxmail.com
 * Description:
 */
class TouchFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_touch

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_dispatch, bt_intercept, bt_touch)
    }

    override fun onClick(v: View) {
        when (v) {
            bt_dispatch -> {
                tl.dispatch = !tl.dispatch
                bt_dispatch.text = "DISPATCH-${tl.dispatch}"
            }
            bt_intercept -> {
                tl.intercept = !tl.intercept
                bt_intercept.text = "intercept-${tl.intercept}"
            }
            bt_touch -> {
                tl.touch = !tl.touch
                bt_touch.text = "touch-${tl.touch}"
            }
        }
    }
}
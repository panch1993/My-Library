package com.pan.mylibrary.ui.fragment

import android.view.View
import com.pan.mylibrary.R
import com.tongji.cjt.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_bezier.*

/**
 * Create by panchenhuan on 2019-12-24
 * walkwindc8@foxmail.com
 * Description:
 */
class BezierFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_bezier

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(
            bt_add,
            bt_remove,
            bt_reset
        )
    }

    override fun onClick(v: View) {
        when (v) {
            bt_add -> bezier_view.addPoint()

            bt_remove -> bezier_view.removePoint()

            bt_reset -> bezier_view.resetPoints()
        }
    }
}
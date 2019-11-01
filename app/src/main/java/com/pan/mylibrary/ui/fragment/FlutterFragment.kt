package com.pan.mylibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tongji.cjt.ui.base.BaseFragment
import io.flutter.facade.Flutter

/**
 * Create by panchenhuan on 2019-11-01
 * walkwindc8@foxmail.com
 * Description:
 */
class FlutterFragment:BaseFragment() {
    override fun getLayoutId(): Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return Flutter.createView(context,lifecycle,"route1")
    }

    override fun initFragment() {

    }

    override fun initView() {

    }
}
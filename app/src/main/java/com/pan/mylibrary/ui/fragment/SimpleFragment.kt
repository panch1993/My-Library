package com.pan.mylibrary.ui.fragment

import com.tongji.cjt.ui.base.BaseFragment

/**
 * Create by panchenhuan on 2019-12-30
 * walkwindc8@foxmail.com
 * Description:
 */
class SimpleFragment(private val layoutRes: Int) : BaseFragment() {
    override fun getLayoutId(): Int = layoutRes

    override fun initFragment() {

    }

    override fun initView() {
    }
}
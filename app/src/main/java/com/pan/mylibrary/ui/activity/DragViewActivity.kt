package com.pan.mylibrary.ui.activity

import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.appbar.AppBarLayout
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.base.Config
import com.pan.mylibrary.utils.GlideUtil
import kotlinx.android.synthetic.main.activity_life.*
import kotlin.math.abs

/**
 * Create by panchenhuan on 2019-10-10
 * walkwindc8@foxmail.com
 * Description:仿即刻拖拽
 */
class DragViewActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_life

    override fun initActivity() {

    }

    override fun initView() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        GlideUtil.load(iv_bg, Config.BACKGROUND_LIFE_URL)
        GlideUtil.load(iv_cir, Config.HEAD_URL, asCircle = true)
        GlideUtil.load(iv_big, Config.BACKGROUND_BIG_URL)
        val s = AccelerateDecelerateInterpolator()
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbarLayout, verticalOffset ->
            val collectHeight = appbarLayout.height - toolbar.height
            //计算偏差
            val percent = s.getInterpolation(1 - abs(verticalOffset) * 1.0f / collectHeight)
            if (iv_cir.alpha == percent) return@OnOffsetChangedListener
            iv_cir.scaleX = percent
            iv_cir.scaleY = percent
            iv_cir.alpha = percent
            iv_cir.isEnabled = percent ==1f
        })

    }

}

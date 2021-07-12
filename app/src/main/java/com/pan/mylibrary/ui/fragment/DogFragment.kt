package com.pan.mylibrary.ui.fragment

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.pan.mylibrary.R
import com.tongji.cjt.ui.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dog.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Create by panchenhuan on 2019-12-30
 * walkwindc8@foxmail.com
 * Description:
 */
class DogFragment : BaseFragment() {
    private val sdf = SimpleDateFormat("HH:mm:ss")
    override fun getLayoutId(): Int = R.layout.fragment_dog

    override fun initFragment() {

    }

    @SuppressLint("CheckResult")
    override fun initView() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val currentTimeMillis = System.currentTimeMillis()
                val format = sdf.format(Date(currentTimeMillis))
                tv_time.text = format
                if (format == "18:30:00") {
                    anim()
                }
            }

    }

    private fun anim() {
        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 100
            repeatCount = -1
            repeatMode = Animation.REVERSE
        }
        iv1.startAnimation(alphaAnimation)
        iv2.startAnimation(alphaAnimation)
        iv1.visibility = View.VISIBLE
        iv2.visibility = View.VISIBLE
    }
}
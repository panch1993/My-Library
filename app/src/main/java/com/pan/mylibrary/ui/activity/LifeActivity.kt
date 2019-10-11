package com.pan.mylibrary.ui.activity

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.appbar.AppBarLayout
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.base.Config
import com.pan.mylibrary.utils.GlideUtil
import com.pan.mylibrary.widget.TimeView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_life.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Create by panchenhuan on 2019-10-10
 * walkwindc8@foxmail.com
 * Description:
 */
class LifeActivity : BaseActivity() {
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())

    private lateinit var subscribe: Disposable

    override fun getLayoutId(): Int = R.layout.activity_life

    override fun initActivity() {

    }

    override fun initView() {
        //交互优化
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            TransitionSet().apply {
//                addTransition(ChangeBounds())
//                addTransition(ChangeTransform())
//                addTarget(iv_cir)
//                window.sharedElementEnterTransition = this
//                window.sharedElementExitTransition = this
//                window.sharedElementReturnTransition = this
//            }
//        }
        injectOnClick(bt_y, bt_m, bt_d, bt_h, bt_min, bt_s, bt_ms)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        GlideUtil.load(iv_bg, Config.BACKGROUND_LIFE_URL)
        GlideUtil.load(iv_cir, Config.HEAD_URL, asCircle = true)
        val s = AccelerateDecelerateInterpolator()
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbarLayout, verticalOffset ->
            val collectHeight = appbarLayout.height - toolbar.height
            //计算偏差
            val percent = s.getInterpolation(1 - abs(verticalOffset) * 1.0f / collectHeight)
            iv_cir.scaleX = percent
            iv_cir.scaleY = percent
            iv_cir.alpha = percent
        })

        tv_index.text = getString(R.string.text_index, Config.DEFAULT_BIRTH_DAY)
        time_view.setTimeIndex(Config.DEFAULT_BIRTH_DAY)

        subscribe = Observable.interval(0, 50, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val currentTimeMillis = System.currentTimeMillis()
                tv_current.text = getString(
                    R.string.text_current,
                    sdf.format(Date(currentTimeMillis)),
                    currentTimeMillis.toString()
                )
            }

    }

    override fun onClick(v: View) {
        when (v) {
            bt_y -> time_view.setCurrentType(TimeView.Type.YEAR)
            bt_m -> time_view.setCurrentType(TimeView.Type.MONTH)
            bt_d -> time_view.setCurrentType(TimeView.Type.DAY)
            bt_h -> time_view.setCurrentType(TimeView.Type.HOUR)
            bt_min -> time_view.setCurrentType(TimeView.Type.MIN)
            bt_s -> time_view.setCurrentType(TimeView.Type.SEC)
            bt_ms -> time_view.setCurrentType(TimeView.Type.MSEC)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe.dispose()
    }
}

package com.pan.mylibrary.ui.fragment

import android.view.View
import com.pan.mylibrary.R
import com.pan.mylibrary.base.Config
import com.pan.mylibrary.widget.TimeView
import com.tongji.cjt.ui.base.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_time_view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Create by panchenhuan on 2019-10-12
 * walkwindc8@foxmail.com
 * Description:
 */
class TimeViewFragment:BaseFragment() {

    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())

    private  var subscribe: Disposable?=null
    override fun getLayoutId(): Int  = R.layout.fragment_time_view

    override fun initFragment() {

    }

    override fun initView() {
        injectOnClick(bt_y, bt_m, bt_d, bt_h, bt_min, bt_s, bt_ms)

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

    override fun onDestroyView() {
        super.onDestroyView()
        subscribe?.dispose()
    }
}
package com.pan.mylibrary.ui.activity

import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

/**
 * Create by panchenhuan on 2019-10-11
 * walkwindc8@foxmail.com
 * Description:
 */
class SplashActivity : BaseActivity() {
    private var disposable: Disposable? = null
    override fun getLayoutId(): Int = R.layout.activity_splash


    override fun initActivity() {

    }

    override fun initView() {
        ViewCompat.animate(v_line).translationX(0f).setInterpolator(DecelerateInterpolator())
            .setDuration(1000).start()
        ViewCompat.animate(tv_welcome).alpha(1f).setInterpolator(OvershootInterpolator())
            .translationX(0f).setDuration(1000).start()

        disposable = Observable.just(1)
            .delay(1500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startActivity(MainActivity::class.java)
                finish()
            }

    }

    override fun onPause() {
        super.onPause()
        //首页用透明度过渡,偏移效果不好
        overridePendingTransition(R.anim.act_fade_in, R.anim.act_fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
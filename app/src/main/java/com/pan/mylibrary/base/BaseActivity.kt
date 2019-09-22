package com.pan.mylibrary.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pan.mylibrary.utils.ActManager

/**
 * Create by panchenhuan on 2018/11/26 2:38 PM
 * Description: Activity基类
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    /**
     * 上下文 this
     */
    protected lateinit var context: BaseActivity

    //应用字体大小不跟随系统
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        config.fontScale = 1f
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        ActManager.get().addActivity(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        beforeSetContentView(savedInstanceState)

        val layoutId = getLayoutId()
        if (layoutId <= 0) throw IllegalAccessException("must active one method(getParentView/getLayoutId)")
        val contentView = View.inflate(this, layoutId, null)
        setContentView(contentView)

        initActivity()

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActManager.get().removeActivity(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            onBackClick()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 返回键处理
     */
    open fun onBackClick() {
        finish()
    }

    override fun onClick(v: View) {}

    /**
     * 预加载回调
     */
    open fun beforeSetContentView(savedInstanceState: Bundle?) {}

    fun startActivity(clazz: Class<*>) {
        startActivity(Intent(context, clazz))
    }

    fun injectOnClick(vararg view: View) {
        for (i in view.indices) {
            view[i].setOnClickListener(this)
        }
    }

    protected abstract fun getLayoutId(): Int

    /**
     * 初始化页面
     */
    protected abstract fun initActivity()

    /**
     * 初始化控件
     */
    protected abstract fun initView()

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun showToast(messageId: Int) {
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show()
    }

}
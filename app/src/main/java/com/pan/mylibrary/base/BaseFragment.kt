package com.tongji.cjt.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.utils.FragmentUserVisibleController
import com.pan.mylibrary.utils.KLog

/**
 * Create by panchenhuan on 2018/11/26 3:04 PM
 * Description:
 */
abstract class BaseFragment : Fragment(), FragmentUserVisibleController.UserVisibleCallback, View.OnClickListener{


    protected lateinit var context: BaseActivity
    private var isInit = false
    //管理fragment可见状态,通过fragmentmanager show hide 的需调用 setuservisible
    private val mUserVisibleController: FragmentUserVisibleController = FragmentUserVisibleController(this, this)

    protected abstract fun getLayoutId(): Int

    protected abstract fun initFragment()

    protected abstract fun initView()


    override fun setWaitingShowToUser(waitingShowToUser: Boolean) {
        mUserVisibleController.isWaitingShowToUser = waitingShowToUser
    }

    override fun isWaitingShowToUser(): Boolean = mUserVisibleController.isWaitingShowToUser

    override fun isVisibleToUser(): Boolean = mUserVisibleController.isVisibleToUser

    override fun callSuperSetUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean, invokeInResumeOrPause: Boolean) {
        KLog.v("$this   ---isVisibleToUser---   $isVisibleToUser")
        if (!isInit && isVisibleToUser) {
            isInit = true
            onFirstVisibleToUser()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mUserVisibleController.setUserVisibleHint(isVisibleToUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.context = activity as BaseActivity
        initFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mUserVisibleController.activityCreated()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = getLayoutId()
        if (layoutId <= 0) {
            throw RuntimeException("Layout files can not be empty")
        }
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        mUserVisibleController.resume()
    }

    override fun onPause() {
        super.onPause()
        mUserVisibleController.pause()
    }

    fun showToast(rsId: Int) {
        context.showToast(rsId)
    }

    fun showToast(message: String) {
        context.showToast(message)
    }

    fun injectOnClick(vararg view: View) {
        for (i in view.indices) {
            view[i].setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {

    }

    open fun onFirstVisibleToUser() {
        KLog.v("onFirstVisibleToUser$this")
    }
}
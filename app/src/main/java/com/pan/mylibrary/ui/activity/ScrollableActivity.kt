package com.pan.mylibrary.ui.activity

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.ui.widget.scrollVp.ScrollableHelper
import com.pan.mylibrary.utils.DataUtil
import kotlinx.android.synthetic.main.activity_scrollable.*

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
class ScrollableActivity : BaseActivity(), ScrollableHelper.ScrollableContainer {
    override fun getScrollableView(): View = rv_content

    override fun getLayoutId(): Int = R.layout.activity_scrollable

    override fun initActivity() {

    }

    override fun initView() {
        rv_content.layoutManager = LinearLayoutManager(context)
        rv_content.adapter = object : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_text) {
            override fun convert(helper: BaseViewHolder, item: Int) {
                (helper.itemView as TextView).text = item.toString()
            }
        }.apply {
            setNewData(DataUtil.ints(500))
        }
        srl.setOnRefreshListener { srl.isRefreshing = false }
        srl.setOnChildScrollUpCallback(sl_layout)
        sl_layout.helper.setCurrentScrollableContainer(this)

    }
}

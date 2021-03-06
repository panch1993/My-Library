package com.pan.mylibrary.ui.activity

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.widget.scrollVp.ScrollableHelper
import com.pan.mylibrary.utils.DataUtil
import kotlinx.android.synthetic.main.activity_scrollable.*

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
class ScrollableActivity : BaseActivity(), ScrollableHelper.ScrollableContainer {
    override fun getScrollableView(): View = vp.getChildAt(vp.currentItem)

    override fun getLayoutId(): Int = R.layout.activity_scrollable

    override fun initActivity() {

    }

    override fun initView() {

        srl.setOnRefreshListener { srl.isRefreshing = false }
        srl.setOnChildScrollUpCallback(sl_layout)

        sl_layout.helper.setCurrentScrollableContainer(this)

        vp.adapter = object :PagerAdapter(){
            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val rv_content = ScrollView(context)
                rv_content.addView(TextView(context).apply {
                    val str = StringBuilder()
                     DataUtil.ints(500).forEach {
                        str.append(it).append("\n")
                    }
                    text = str.toString()
                })
//                rv_content.layoutManager = LinearLayoutManager(context)
//                rv_content.adapter = object : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_text) {
//                    override fun convert(helper: BaseViewHolder, item: Int) {
//                        (helper.itemView as TextView).text = item.toString()
//                    }
//                }.apply {
//                    setNewData(DataUtil.ints(500))
//                }
                container.addView(rv_content)
                return rv_content
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }
            override fun getCount(): Int = 3
        }
    }
}

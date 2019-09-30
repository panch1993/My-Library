package com.pan.mylibrary.ui.activity

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.utils.DataUtil
import kotlinx.android.synthetic.main.activity_nested_scroll.*

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
class NestedScrollableActivity : BaseActivity(){

    override fun getLayoutId(): Int = R.layout.activity_nested_scroll

    override fun initActivity() {

    }

    override fun initView() {
        tb.setupWithViewPager(vp)
        vp.adapter = object :PagerAdapter(){
            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val rv_content = RecyclerView(context)
              /*  rv_content.addView(TextView(context).apply {
                    val str = StringBuilder()
                     DataUtil.ints(500).forEach {
                        str.append(it).append("\n")
                    }
                    text = str.toString()
                })*/
                rv_content.layoutManager = LinearLayoutManager(context)
                rv_content.adapter = object : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_text) {
                    override fun convert(helper: BaseViewHolder, item: Int) {
                        (helper.itemView as TextView).text = item.toString()
                    }
                }.apply {
                    setNewData(DataUtil.ints(500))
                }
                container.addView(rv_content)
                return rv_content
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }
            override fun getCount(): Int = 3
            override fun getPageTitle(position: Int): CharSequence? {
                return "TITLE:$position"
            }
        }
    }
}

package com.pan.mylibrary.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pan.mylibrary.R
import com.pan.mylibrary.base.AppPagerAdapter
import com.pan.mylibrary.ui.fragment.*

private val TAB_TITLES = arrayOf(
    R.string.tab_flow,
    R.string.tab_dog,
    R.string.tab_range_seek,
    R.string.tab_bezier,
    R.string.tab_chart,
    R.string.tab_radar,
    R.string.tab_card
    //,R.string.tab_time
    ,
    R.string.tab_touch
//    ,R.string.tab_flutter
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    AppPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (TAB_TITLES[position]) {
            R.string.tab_chart -> ChartViewFragment()
            R.string.tab_radar -> RadarViewFragment()
            R.string.tab_card -> CardGroupFragment()
            R.string.tab_time -> TimeViewFragment()
//            R.string.tab_flutter -> FlutterFragment()
            R.string.tab_bezier -> BezierFragment()
            R.string.tab_touch -> TouchFragment()
            R.string.tab_range_seek -> RangeSeekFragment()
            R.string.tab_flow -> SimpleFragment(R.layout.fragment_flow)
            R.string.tab_dog -> DogFragment()
            else -> PlaceholderFragment.newInstance(position + 1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}
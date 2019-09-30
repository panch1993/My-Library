package com.pan.mylibrary.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pan.mylibrary.R
import com.pan.mylibrary.ui.fragment.ChartViewFragment
import com.pan.mylibrary.ui.fragment.PlaceholderFragment
import com.pan.mylibrary.ui.fragment.RadarViewFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_chart,
    R.string.tab_radar
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (TAB_TITLES[position]) {
            R.string.tab_chart -> ChartViewFragment()
            R.string.tab_radar -> RadarViewFragment()
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
package com.pan.mylibrary.ui.activity

import android.graphics.Color
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import com.pan.mylibrary.BuildConfig
import com.pan.mylibrary.R
import com.pan.mylibrary.base.BaseActivity
import com.pan.mylibrary.ui.adapter.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun initView() {
        view_pager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(view_pager)
       /* fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        setSupportActionBar(tool_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toggle = ActionBarDrawerToggle(this,drawer_layout, R.string.open, R.string.close)
        toggle.syncState()
        drawer_layout.addDrawerListener(toggle)

        tv_version_name.text = BuildConfig.VERSION_NAME

        nav_view.setNavigationItemSelectedListener (this)
    }
    override fun onNavigationItemSelected(it: MenuItem): Boolean {
        when (it.itemId) {
            R.id.nav_scr -> startActivity(ScrollableActivity::class.java)
            R.id.nav_nested_scr -> startActivity(NestedScrollableActivity::class.java)
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home ->  toggle.onOptionsItemSelected(item)
            R.id.action_settings -> {
                showToast("setting")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_title, menu)
        return true
    }
}
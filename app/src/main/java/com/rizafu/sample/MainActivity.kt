package com.rizafu.sample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem

import com.rizafu.coachmark.CoachMark
import com.rizafu.sample.databinding.AboutTooltipBinding
import com.rizafu.sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, SimpleFragment.newInstance(), "Simple")
                .commit()
        binding.bottomNav.setOnNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu1 -> {
                val binding = DataBindingUtil.inflate<AboutTooltipBinding>(LayoutInflater.from(this), R.layout.about_tooltip, null, false)
                binding.text.text = "Coach Mark v" + BuildConfig.VERSION_NAME + "\nGithub : goo.gl/t2d2JG"
                CoachMark.Builder(this)
                        .setTarget(R.id.menu_custom)
                        .addTooltipChild(binding.root)
                        .setTooltipAlignment(CoachMark.ROOT_CENTER)
                        .setTooltipBackgroundColor(android.R.color.transparent)
                        .setBackgroundAlpha(0.5f)
                        .setDismissible()
                        .setTooltipPointer(CoachMark.POINTER_GONE)
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun wellcomeTutorial() {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_simple -> supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, SimpleFragment.newInstance(), "Simple")
                    .disallowAddToBackStack()
                    .commit()
            R.id.menu_custom -> supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, CustomFragment.newInstance(), "Custom")
                    .disallowAddToBackStack()
                    .commit()
            R.id.menu_sequence -> supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, SequenceFragment.newInstance(), "Sequence")
                    .disallowAddToBackStack()
                    .commit()
        }
        return true
    }
}

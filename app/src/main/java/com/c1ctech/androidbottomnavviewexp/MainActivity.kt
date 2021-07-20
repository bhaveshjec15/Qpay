package com.c1ctech.androidbottomnavviewexp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = getSupportActionBar();

        loadFragment(HomeFragment.newInstance())

        bottom_navigation.setOnItemSelectedListener { item ->
            var fragment: Fragment
            when (item.itemId) {
                R.id.nav_home -> {
                    toolbar?.setTitle("Home")
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.nav_radio -> {
                    toolbar?.setTitle("Radio")
                    fragment = RadioFragment()
                    loadFragment(fragment)
                    true

                }
                R.id.nav_search -> {
                    toolbar?.setTitle("Search")
                    fragment = SearchFragment()
                    loadFragment(fragment)
                    true

                }
                R.id.nav_my_music -> {
                    toolbar?.setTitle("My Music")
                    fragment = MyMusicFragment()
                    loadFragment(fragment)
                    true

                }
                R.id.nav_library -> {
                    toolbar?.setTitle("Library")
                    fragment = LibraryFragment()
                    loadFragment(fragment)
                    true

                }
                else -> false
            }

        }

        bottom_navigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_radio -> {
                    Toast.makeText(this, "Radio Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_search -> {
                    Toast.makeText(this, "Search Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_my_music -> {
                    Toast.makeText(this, "My Music Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_library -> {
                    Toast.makeText(this, "Library Item reselected", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}
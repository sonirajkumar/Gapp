@file:Suppress("OverrideDeprecatedMigration")

package com.soni.gapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.soni.gapp.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class ActivityMain : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(FragmentHome())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(FragmentHome())
                R.id.add -> replaceFragment(FragmentAddCust())
                R.id.search -> replaceFragment(FragmentCustSearch())
                R.id.menu -> replaceFragment(FragmentMenu())
                else -> {
                }
            }
            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
        exitProcess(0)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}
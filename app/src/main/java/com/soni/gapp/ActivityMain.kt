@file:Suppress("OverrideDeprecatedMigration")

package com.soni.gapp

//import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.soni.gapp.databinding.ActivityMainBinding

class ActivityMain : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var token: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        var token = this.getSharedPreferences("email", Context.MODE_PRIVATE)

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


//        binding.username.text = token.getString("loginemail"," ")

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}
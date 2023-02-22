package com.soni.gapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.soni.gapp.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var token: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = this.getSharedPreferences("email", Context.MODE_PRIVATE)

        auth = FirebaseAuth.getInstance()

        if (token.getString("loginemail"," ")!=" "){
            val intent = Intent( this, ActivityMain::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.username.text.toString()
            val pass = binding.password.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){

                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{

                    if (it.isSuccessful){

                        val intent = Intent( this, ActivityMain::class.java)
                        intent.putExtra("email",email)


                        var editor = token.edit()
                        editor.putString("loginemail", email)
                        editor.commit()

                        startActivity(intent)

                    }
                    else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show()
            }
        }



    }
}
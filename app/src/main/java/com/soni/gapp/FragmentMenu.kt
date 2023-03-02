package com.soni.gapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.soni.gapp.databinding.FragmentMenuBinding
import com.soni.gapp.databinding.FragmentTransactionSearchBinding
class FragmentMenu : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        token = requireContext().getSharedPreferences("email", Context.MODE_PRIVATE)

        auth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            token.edit().clear().apply()
            auth.signOut()

            val intent = Intent( requireContext(), ActivityLogin::class.java)
            startActivity(intent)

        }
        return binding.root
    }

}
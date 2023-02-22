package com.soni.gapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soni.gapp.databinding.FragmentSearchResultBinding

class FragmentSearchResult : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var fName: String
    private lateinit var mName: String
    private lateinit var lName: String
    private lateinit var city: String
    private var mobileNumber: String? = null
    private var aadharNumber: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fName = it.getString("f_name").toString()
            mName = it.getString("m_name").toString()
            lName = it.getString("l_name").toString()
            city = it.getString("city").toString()
            mobileNumber = it.getString("mobile_number").toString()
            aadharNumber = it.getString("aadhar_number").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        binding.temptextview.text = "$fName $mName $lName $city $mobileNumber $aadharNumber"
        return binding.root
    }
}
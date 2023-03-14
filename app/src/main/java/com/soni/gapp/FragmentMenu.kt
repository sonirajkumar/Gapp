package com.soni.gapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentMenuBinding
import com.soni.gapp.databinding.FragmentTransactionSearchBinding
class FragmentMenu : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private var custSearchList = ArrayList<DataCustSearch>()
    private lateinit var adapter: AdapterCustSearch
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        token = requireContext().getSharedPreferences("email", Context.MODE_PRIVATE)
        val rvCustHistroy = binding.rvLastTransactions
        rvCustHistroy.layoutManager = LinearLayoutManager(context)
        adapter = AdapterCustSearch(custSearchList)
        adapter.isTransferredFromHistory = true
        rvCustHistroy.adapter = adapter
        custSearchList.clear()
        adapter.notifyDataSetChanged()

        auth = FirebaseAuth.getInstance()

        binding.btnShowLastTransactions.setOnClickListener {
            custSearchList.clear()
            adapter.notifyDataSetChanged()

            db.collection("history").get().addOnSuccessListener { customers->
                if (!customers.isEmpty){
                    for(cust in customers){
                        val custData = DataCustSearch(
                            cust.data["f_name"].toString(),
                            cust.data["m_name"].toString(),
                            cust.data["l_name"].toString(),
                            cust.data["city"].toString(),
                            " ",
                            " ")

                        custSearchList.add(custData)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            db.collection("history").count().get(AggregateSource.SERVER)
                .addOnCompleteListener {count->
                    if(count.isSuccessful){
                        val countDiff = count.result.count - 10
//                        if (countDiff>0){
                            db.collection("history").orderBy("timestamp").get().addOnCompleteListener {
                                println(it.result.documents)
                            }
//                        }
                    }
                }
        }

        binding.btnLogout.setOnClickListener {
            token.edit().clear().apply()
            auth.signOut()

            val intent = Intent(requireContext(), ActivityLogin::class.java)
            startActivity(intent)

        }
        return binding.root
    }

}
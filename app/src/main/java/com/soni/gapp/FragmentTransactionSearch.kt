package com.soni.gapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentTransactionSearchBinding

class FragmentTransactionSearch : Fragment() {
    private var _binding: FragmentTransactionSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var fName: String
    private lateinit var mName: String
    private lateinit var lName: String
    private lateinit var city: String
    private var mobileNumber: String? = null
    private var aadharNumber: String? = null
    private lateinit var rakamType: String
    private lateinit var rakamWeight: String
    private lateinit var custDocumentId: String
    private lateinit var rakamDocumentId: String

    private lateinit var recyclerView: RecyclerView
    private var tranList = ArrayList<DataTransactionSearch>()
    private lateinit var adapter: AdapterTransactionSearch

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle->
            fName = bundle.getString("f_name").toString()
            mName = bundle.getString("m_name").toString()
            lName = bundle.getString("l_name").toString()
            city = bundle.getString("city").toString()
            mobileNumber = bundle.getString("mobile_number").toString()
            aadharNumber = bundle.getString("aadhar_number").toString()
            rakamType = bundle.getString("rakam_type").toString()
            rakamWeight = bundle.getString("rakam_weight").toString()
            custDocumentId = fName.filter { !it.isWhitespace() } +"_"+ mName.filter { !it.isWhitespace() } +"_"+ lName.filter { !it.isWhitespace() } +"_"+ city.filter { !it.isWhitespace() }+"_"+ mobileNumber!!.filter { !it.isWhitespace() }+"_"+ aadharNumber!!.filter { !it.isWhitespace() }
            rakamDocumentId = rakamType.filter { !it.isWhitespace() }+"_"+rakamWeight+"GMS"
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionSearchBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewTransactionSearch
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterTransactionSearch(tranList)
        recyclerView.adapter = adapter
        tranList.clear()
        adapter.notifyDataSetChanged()

        val custDetails = "$fName $mName $lName $city"
        val rakamDetails = "$rakamType $rakamWeight GMS"
        binding.textViewTransactionSearchFragmentCustDetails.text = custDetails
        binding.textViewTransactionSearchFragmentMobileNumber.text = "Mobile: $mobileNumber"
        binding.textViewTransactionSearchFragmentAadharNumber.text = "Aadhar: $aadharNumber"
        binding.textViewTransactionSearchFragmentRakamDetails.text = rakamDetails

        val collectionRef = db.collection("cust").document(custDocumentId).collection("rakam").document(rakamDocumentId).collection("transaction")
        collectionRef.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (tran in it) {

                    val tranDetail = DataTransactionSearch(
                        tran.data["type"] as String,
                        tran.data["amount"] as String,
                        tran.data["ir"] as String ,
                        tran.data["remarks"] as String,
                        tran.data["date"] as String
                    )
                    tranList.add(tranDetail)
                    adapter.notifyDataSetChanged()
                }

            }
        }
        return binding.root
    }

}
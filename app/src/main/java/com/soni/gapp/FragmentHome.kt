package com.soni.gapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var custSearchList = ArrayList<DataCustSearch>()
    private lateinit var adapter: AdapterCustSearch
    private val db = Firebase.firestore
    private var irForIntCal: String = ""
    private lateinit var tranListForIntCal: MutableList<MutableList<String>>
    private var finalAmount: Float = 0f

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        recyclerView = binding.rvShowRakamInLoss
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterCustSearch(custSearchList)
        recyclerView.adapter = adapter
        tranListForIntCal = mutableListOf()
        custSearchList.clear()
        adapter.notifyDataSetChanged()

        binding.btnShowRakamInLoss.setOnClickListener {
            custSearchList.clear()
            adapter.notifyDataSetChanged()
            val silRate = binding.etSilverRate.text?.toString()
            if (silRate.isNullOrEmpty()){
                Toast.makeText(context, "Please Enter a Valid Rate", Toast.LENGTH_SHORT).show()
            }
            else{
                db.collection("cust").get().addOnSuccessListener {custs->
                    if (!custs.isEmpty){
                        for(cust in custs){
                            db.collection("cust").document(cust.id).collection("rakam")
                                .get().addOnSuccessListener {rakams->
                                    if (!rakams.isEmpty){
                                        for (rakam in rakams){
                                            val rakamWeight = rakam.data["weight_gms"].toString().toFloat()
                                            db.collection("cust").document(cust.id)
                                                .collection("rakam").document(rakam.id)
                                                .collection("transaction").get()
                                                .addOnSuccessListener {tss->
                                                    if(!tss.isEmpty){
                                                        for(tran in tss){
                                                            if(!tran.data["ir"].toString().isEmpty()){
                                                                irForIntCal = tran.data["ir"] as String
                                                            }
                                                            tranListForIntCal.add(mutableListOf(tran.data["type"].toString(),
                                                                tran.data["amount"] as String, tran.data["date"] as String
                                                            ))
                                                        }
                                                    }
                                                    val intCalObject = IntCalculator()
                                                    finalAmount = intCalObject.calculateFinalAmount(tranListForIntCal, irForIntCal)
                                                    if (finalAmount > ((rakamWeight * silRate.toFloat())/1000)){
                                                        val custData = DataCustSearch(
                                                            cust.data["f_name"].toString(),
                                                            cust.data["m_name"].toString(),
                                                            cust.data["l_name"].toString(),
                                                            cust.data["city"].toString(),
                                                            cust.data["mobile_no"].toString(),
                                                            cust.data["aadhar_no"].toString())
                                                        custSearchList.add(custData)
                                                        adapter.notifyDataSetChanged()
                                                    }
                                                }

                                        }
                                    }
                                }
                        }
                    }
                }
            }
            finalAmount = 0f
            tranListForIntCal.clear()
        }

        return binding.root
    }

}
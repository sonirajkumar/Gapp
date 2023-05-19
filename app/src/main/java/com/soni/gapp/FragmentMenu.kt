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
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentMenuBinding
class FragmentMenu : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private var custSearchList = ArrayList<DataCustSearch>()
    private lateinit var adapter: AdapterCustSearch
    private var histList: MutableList<String> = mutableListOf()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        token = requireContext().getSharedPreferences("email", Context.MODE_PRIVATE)
        val rvCustHistory = binding.rvLastTransactions
        rvCustHistory.layoutManager = LinearLayoutManager(context)
        adapter = AdapterCustSearch(custSearchList)
        rvCustHistory.adapter = adapter
        custSearchList.clear()
        adapter.notifyDataSetChanged()

        auth = FirebaseAuth.getInstance()

        binding.btnShowLastTransactions.setOnClickListener {
            histList.clear()
            custSearchList.clear()
            adapter.notifyDataSetChanged()

            db.collection("history").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener { custID->
                if (!custID.isEmpty){
                    for(ids in custID){
                        histList.add(ids.data["cid"].toString())
                    }
                    for(cid in histList){
                        db.collection("cust").get().addOnSuccessListener {
                            if (!it.isEmpty) {
                                for (docs in it) {
                                    if(docs.id.takeLast(cid.length) == cid){
                                        db.collection("cust").document(docs.id).get()
                                            .addOnSuccessListener { document ->
                                                val custData = DataCustSearch(
                                                    document.data!!["f_name"].toString(),
                                                    document.data!!["m_name"].toString(),
                                                    document.data!!["l_name"].toString(),
                                                    document.data!!["city"].toString(),
                                                    document.data!!["mobile_no"].toString(),
                                                    document.data!!["aadhar_no"].toString(),
                                                    document.data!!["cid"].toString()
                                                )

                                                custSearchList.add(custData)
                                                adapter.notifyDataSetChanged()
                                            }
                                    }
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Data Fetching Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            db.collection("history").count().get(AggregateSource.SERVER)
                .addOnCompleteListener {count->
                    if(count.isSuccessful){
                        val countDiff = count.result.count - 50
                        if (countDiff>0) {
                            db.collection("history").orderBy("timestamp").limit(countDiff)
                                .get().addOnSuccessListener {
                                    for (docs in it) {
                                        db.collection("history").document(docs.id).delete()
                                            .addOnSuccessListener {

                                            }
                                    }
                                }
                        }
                    }
                }
        }

        binding.btnBackup.setOnClickListener {

            // CREATING NEW COLLECTION HERE FOR CUST
//            var existsRN = ""
//            db.collection("cust").get().addOnSuccessListener {docs->
//                if(!docs.isEmpty){
//                    for (doc in docs){
////                        val updates = hashMapOf<String, Any>(
////                            "cid" to FieldValue.delete()
////                        )
////                        db.collection("cust").document(doc.id).update(updates)
//                        val fName = doc.data["f_name"].toString()
//                        val mName = doc.data["m_name"].toString()
//                        val lName = doc.data["l_name"].toString()
//                        val city = doc.data["city"].toString()
//                        val mobileNumber = doc.data["mobile_no"].toString()
//                        val AadharNumber = doc.data["aadhar_no"].toString()
//
//
//                        val custDetails = fName+"_"+mName+"_"+lName+"_"+city+"_"+mobileNumber+"_"+AadharNumber+"_"
//
//                        db.collection("cust").document(doc.id).collection("rakam").get().addOnSuccessListener {rakams->
//                            if(!rakams.isEmpty){
//                                for (rakam in rakams){
//                                    db.collection("cust").document(doc.id).collection("rakam").document(rakam.id).get().addOnSuccessListener {rakamData->
//                                        existsRN = rakamData.data?.get("rakam_number").toString().replace("/",".").filter { !it.isWhitespace() }
//                                        val newCustDetails = custDetails + existsRN
//                                        db.collection("newcust").document(newCustDetails).set(
//                                            hashMapOf(
//                                                "f_name" to fName,
//                                                "m_name" to mName,
//                                                "l_name" to lName,
//                                                "city" to city,
//                                                "mobile_no" to mobileNumber,
//                                                "aadhar_no" to AadharNumber,
//                                                "cid" to existsRN
//                                            ), SetOptions.merge()
//                                        ).addOnSuccessListener {
//
//                                            db.collection("newcust").document(newCustDetails)
//                                                .collection("rakam").document(rakam.id).set(rakam.data).addOnSuccessListener {
//
//                                                    db.collection("cust").document(doc.id).collection("rakam").document(rakam.id).collection("transaction").get().addOnSuccessListener {ts->
//                                                        if (!ts.isEmpty){
//                                                            for (transactions in ts) {
//                                                                db.collection("newcust").document(newCustDetails)
//                                                                    .collection("rakam").document(rakam.id)
//                                                                    .collection("transaction").document(transactions.id)
//                                                                    .set(transactions.data)
//                                                            }
//                                                        }
//                                                    }
//
//                                                }
//                                        }
//                                    }
//
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            // DELETE CUST AND THEN RUN THIS

//            db.collection("newcust").get().addOnSuccessListener {customers->
//                if (!customers.isEmpty){
//                    for (cust in customers){
//                        db.collection("cust").document(cust.id.filter { !it.isWhitespace() }).set(cust.data).addOnSuccessListener {
//                            db.collection("newcust").document(cust.id).collection("rakam").get().addOnSuccessListener { rakams->
//                                if (!rakams.isEmpty){
//                                    for (rakam in rakams){
//                                        db.collection("cust").document(cust.id.filter { !it.isWhitespace() }).collection("rakam").document(rakam.id).set(rakam.data).addOnSuccessListener {
//                                            db.collection("newcust").document(cust.id).collection("rakam").document(rakam.id).collection("transaction").get().addOnSuccessListener { ts->
//                                                if (!ts.isEmpty){
//                                                    for (trans in ts){
//                                                        db.collection("cust").document(cust.id.filter { !it.isWhitespace() }).collection("rakam").document(rakam.id).collection("transaction").document(trans.id).set(trans.data)
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            // DELETE NEWCUST AND RUN THIS
//            val updates = hashMapOf<String, Any>(
//                "rakam_number" to FieldValue.delete(),
//            )
//            db.collection("cust").get().addOnSuccessListener { customers ->
//                if (!customers.isEmpty) {
//                    for (cust in customers) {
//                        db.collection("cust").document(cust.id).collection("rakam").get().addOnSuccessListener { rakams ->
//                            if (!rakams.isEmpty) {
//                                for (rakam in rakams) {
//                                    db.collection("cust").document(cust.id).collection("rakam").document(rakam.id).update(updates)
//                                }
//                            }
//                        }
//                    }
//                }
//            }

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
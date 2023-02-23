package com.soni.gapp

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.soni.gapp.databinding.FragmentRakamSearchBinding

class FragmentRakamSearch : Fragment() {
    private var _binding: FragmentRakamSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder

    private lateinit var fName: String
    private lateinit var mName: String
    private lateinit var lName: String
    private lateinit var city: String
    private var mobileNumber: String? = null
    private var aadharNumber: String? = null
    private lateinit var documentId: String

    private lateinit var recyclerView: RecyclerView
    private var rakamList = ArrayList<DataRakamSearch>()
    private lateinit var adapter: AdapterRakamSearch

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            fName = bundle.getString("f_name").toString()
            mName = bundle.getString("m_name").toString()
            lName = bundle.getString("l_name").toString()
            city = bundle.getString("city").toString()
            mobileNumber = bundle.getString("mobile_number").toString()
            aadharNumber = bundle.getString("aadhar_number").toString()
            documentId = fName.filter { !it.isWhitespace() } +"_"+ mName.filter { !it.isWhitespace() } +"_"+ lName.filter { !it.isWhitespace() } +"_"+ city.filter { !it.isWhitespace() }+"_"+ mobileNumber!!.filter { !it.isWhitespace() }+"_"+ aadharNumber!!.filter { !it.isWhitespace() }

        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRakamSearchBinding.inflate(inflater, container, false)
        alertBuilder = AlertDialog.Builder(activity)

        recyclerView = binding.recyclerViewRakamSearch
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterRakamSearch(rakamList)
        recyclerView.adapter = adapter
        rakamList.clear()
        adapter.notifyDataSetChanged()

        val custDetails = "$fName $mName $lName $city"
        binding.textViewSearchResultCustDetails.text = custDetails
        binding.textViewMobileNumber.text = "Mobile: $mobileNumber"
        binding.textViewAadharNumber.text = "Aadhar: $aadharNumber"

        val collectionRef = db.collection("cust").document(documentId).collection("rakam")
        collectionRef.get().addOnSuccessListener {
            if(!it.isEmpty){
                for(rakam in it){
                    val rakamDetail = DataRakamSearch(
                        fName,mName,lName,city,mobileNumber,aadharNumber,
                        rakam.data["rakam_type"] as String,
                        rakam.data["weight_gms"] as String
                    )
                    rakamList.add(rakamDetail)
                    adapter.notifyDataSetChanged()
                }

            }

        }.addOnFailureListener {
            Toast.makeText(context, "Data Fetching Failed", Toast.LENGTH_LONG).show()
        }

        binding.buttonRakamSearchAddRakam.setOnClickListener {
            val nextFragment = FragmentAddRakam()
            nextFragment.arguments = arguments
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayout, nextFragment).addToBackStack(null).commit()
        }

        // DELETING CUSTOMER HERE
        binding.btnDeleteCustomer.setOnClickListener {
            alertBuilder.setTitle("Confirmation")
                .setMessage("Are you sure want to Delete Customer?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    db.collection("cust").document(documentId)
                        .collection("rakam").get()
                        .addOnSuccessListener { rakams->
                            if (!rakams.isEmpty){
                                for(rakam in rakams){
                                    db.collection("archive").document(documentId)
                                        .collection("rakam").document(rakam.id).set(rakam)

                                    db.collection("cust").document(documentId)
                                        .collection("rakam").document(rakam.id)
                                        .collection("transaction").get()
                                        .addOnSuccessListener { ts->
                                            if (!ts.isEmpty){
                                                for (transactions in ts) {
                                                    db.collection("archive").document(documentId)
                                                        .collection("rakam").document(rakam.id)
                                                        .collection("transaction").document(transactions.id)
                                                        .set(transactions.data)

                                                    db.collection("cust").document(documentId)
                                                        .collection("rakam").document(rakam.id)
                                                        .collection("transaction").document(transactions.id)
                                                        .delete()
                                                }
                                            }
                                        }
                                    db.collection("cust").document(documentId)
                                        .collection("rakam").document(rakam.id).delete()
                                }
                            }
                        }

                    db.collection("cust").document(documentId).get()
                        .addOnSuccessListener {custDocs->
                            custDocs.data?.let { it1 ->
                                db.collection("archive").document(documentId)
                                    .set(it1)
                            }
                        }

                    db.collection("cust").document(documentId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        }

                }.setNegativeButton("No") { _, _ ->
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            alertBuilder.show()
        }
        return binding.root
    }

}
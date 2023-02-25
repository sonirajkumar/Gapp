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
import com.soni.gapp.databinding.FragmentTransactionSearchBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

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
    private lateinit var irForIntCal: String

    private lateinit var recyclerView: RecyclerView
    private var tranList = ArrayList<DataTransactionSearch>()
    private lateinit var adapter: AdapterTransactionSearch
    private lateinit var alertBuilder: AlertDialog.Builder

    private val db = Firebase.firestore

    private var finalAmount: Float = 0f

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
            alertBuilder = AlertDialog.Builder(activity)
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

                    if(!tran.data["ir"].toString().isEmpty()){
                        irForIntCal = tran.data["ir"] as String
                    }
                    calculateFinalAmount(tran.data["amount"] as String, irForIntCal,
                        tran.data["date"] as String,tran.data["type"] as String)
                }

            }
        }

        binding.btnAddTransaction.setOnClickListener {
            val nextFragment = FragmentAddTransaction()
            nextFragment.arguments = arguments
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayout, nextFragment).addToBackStack(null).commit()
        }

        binding.buttonCalculateTotalBalance.setOnClickListener {
            binding.calculatedAmount.text = finalAmount.toString()
        }

        binding.btnDeleteRakam.setOnClickListener {
            alertBuilder.setTitle("Confirmation")
                .setMessage("Are you sure want to Delete Rakam?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    db.collection("archive").document(custDocumentId).set(
                        hashMapOf(
                            "f_name" to fName,
                            "m_name" to mName,
                            "l_name" to lName,
                            "city" to city,
                            "mobile_no" to mobileNumber,
                            "aadhar_no" to aadharNumber
                        )
                    ).addOnSuccessListener {  }.addOnFailureListener {  }

                    db.collection("archive").document(custDocumentId)
                        .collection("rakam").document(rakamDocumentId).set(
                            hashMapOf(
                                "rakam_type" to rakamType,
                                "weight_gms" to rakamWeight
                            )
                        )

                    db.collection("cust").document(custDocumentId)
                        .collection("rakam").document(rakamDocumentId)
                        .collection("transaction").get()
                        .addOnSuccessListener { ts->
                            if (!ts.isEmpty){
                                for (transactions in ts) {
                                    db.collection("archive").document(custDocumentId)
                                        .collection("rakam").document(rakamDocumentId)
                                        .collection("transaction").document(transactions.id)
                                        .set(transactions.data)

                                    db.collection("cust").document(custDocumentId)
                                        .collection("rakam").document(rakamDocumentId)
                                        .collection("transaction").document(transactions.id)
                                        .delete()
                                }
                            }
                        }

                    db.collection("cust").document(custDocumentId).collection("rakam")
                        .document(rakamDocumentId).delete().addOnSuccessListener {
                            Toast.makeText(activity, "Rakam Deleted Successfully", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(activity, "Unable to delete rakam", Toast.LENGTH_LONG).show()
                        }

                }
                .setNegativeButton("No") { _, _ ->
            Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
        }
            alertBuilder.show()
        }

        return binding.root
    }
    @SuppressLint("SimpleDateFormat")
    private fun calculateFinalAmount(principal: String, ir: String, dateString: String, tranType: String){
        val pa = principal.toFloat()

        val date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
        val dateDiffMs = (Date().time - date.time).toFloat()
        val dateDiffMonths: Float = ceil(dateDiffMs/(2592000000))
        val dateDiffYears = floor(dateDiffMonths/12)
        if (tranType == "NAAME"){
            val remainingMonths = ceil(dateDiffMonths % 12)
            finalAmount += pa
            for(i in 1..dateDiffYears.toInt()){
                finalAmount += calculateInterest(pa, ir.toFloat(), 12f)
            }
            finalAmount += calculateInterest(pa, ir.toFloat(), remainingMonths)
        }else{
            val remainingMonths = floor(dateDiffMonths % 12)
            finalAmount -= pa
            for(i in 1..dateDiffYears.toInt()){
                finalAmount -= calculateInterest(pa, ir.toFloat(), 12f)
            }
            finalAmount -= calculateInterest(pa, ir.toFloat(), max(0f, remainingMonths-1))
        }
    }
    private fun calculateInterest(pa: Float, ir: Float, t: Float): Float {
        val tempAmount = pa * (1 + ((ir/100) * t))
        return tempAmount - pa
    }
}
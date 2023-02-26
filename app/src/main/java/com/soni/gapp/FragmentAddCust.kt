package com.soni.gapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentAddCustBinding


class FragmentAddCust : Fragment() {
    private var _binding: FragmentAddCustBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder
    private lateinit var fName: String
    private lateinit var mName: String
    private lateinit var lName: String
    private lateinit var city: String
    private var mobileNumber: String = "null"
    private var aadharNumber: String = "null"
    private lateinit var custDocumentId: String
    private var isTransferredFromSearch: Boolean = false
    private var isCustomerExists:Boolean = false
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle->
            fName = bundle.getString("f_name").toString()
            mName = bundle.getString("m_name").toString()
            lName = bundle.getString("l_name").toString()
            city = bundle.getString("city").toString()
            mobileNumber = bundle.getString("mobile_number").toString()
            aadharNumber = bundle.getString("aadhar_number").toString()
            isTransferredFromSearch = bundle.getBoolean("isTransferredFromSearch")
            custDocumentId = fName.filter { !it.isWhitespace() } +"_"+ mName.filter { !it.isWhitespace() } +"_"+ lName.filter { !it.isWhitespace() } +"_"+ city.filter { !it.isWhitespace() }+"_"+ mobileNumber.filter { !it.isWhitespace() }+"_"+ aadharNumber.filter { !it.isWhitespace() }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCustBinding.inflate(inflater, container, false)
        alertBuilder = AlertDialog.Builder(activity)

        if (isTransferredFromSearch) {
            binding.firstName.setText(fName)
            binding.middleName.setText(mName)
            binding.lastName.setText(lName)
            binding.city.setText(city)
            binding.mobileNumber.setText(mobileNumber)
            binding.aadharNumber.setText(aadharNumber)
            binding.addAccountButton.text = "Update Customer Info"
        }

        binding.addAccountButton.setOnClickListener {
            fName = binding.firstName.text.toString().uppercase()
            lName = binding.lastName.text.toString().uppercase()
            mName = binding.middleName.text.toString().uppercase()
            city = binding.city.text.toString().uppercase()
            mobileNumber = binding.mobileNumber.text.toString()
            aadharNumber = binding.aadharNumber.text.toString()
            isCustomerExists = false

            if (fName.isEmpty() or lName.isEmpty() or mName.isEmpty() or city.isEmpty()) {
                Toast.makeText(activity, "Please insert Valid Data ", Toast.LENGTH_LONG).show()
            } else if (mobileNumber.isNotBlank() and (mobileNumber.length != 10)) {
                Toast.makeText(activity, "Please insert Valid Number ", Toast.LENGTH_LONG)
                    .show()
            } else if (aadharNumber.isNotBlank() and (aadharNumber.length != 12)) {
                Toast.makeText(activity, "Please insert Valid Aadhar ", Toast.LENGTH_LONG)
                    .show()
            } else {
                db.collection("cust").get().addOnSuccessListener { custDocs ->
                    val query =
                        fName.filter { !it.isWhitespace() } + "_" + mName.filter { !it.isWhitespace() } + "_" + lName.filter { !it.isWhitespace() } + "_" + city.filter { !it.isWhitespace() }

                    if (!custDocs.isEmpty) {
                        for (docs in custDocs) {
                            if (docs.id.contains(query)) {
                                isCustomerExists = true
                            }
                        }
                        println(isCustomerExists)
                        if (isCustomerExists) {
                            Toast.makeText(context, "Customer Already Exists", Toast.LENGTH_LONG).show()
                        } else {
                            alertBuilder.setTitle("Confirmation")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes") { _, _ ->
                                    addCustomer()
                                }.setNegativeButton("No"){_,_->
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                                }
                            alertBuilder.show()
                        }
                    }
                }.addOnFailureListener { Toast.makeText(context, "Data Fetching Failed", Toast.LENGTH_SHORT).show()
                }
            }

        }
            return binding.root
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun addCustomer(){
            val accountHashMap = hashMapOf(
                "f_name" to fName,
                "m_name" to mName,
                "l_name" to lName,
                "city" to city,
                "mobile_no" to mobileNumber,
                "aadhar_no" to aadharNumber

            )
            val tempDocID = fName.filter { !it.isWhitespace() } + "_"+ mName.filter { !it.isWhitespace() } + "_"+ lName.filter { !it.isWhitespace() } + "_"+ city.filter { !it.isWhitespace() } + "_"+ mobileNumber.filter { !it.isWhitespace() } + "_"+ aadharNumber.filter { !it.isWhitespace() }
            if (!isTransferredFromSearch) {
                db.collection("cust").document(tempDocID)
                    .set(accountHashMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Account Added Successfully", Toast.LENGTH_LONG).show()
                        binding.firstName.text.clear()
                        binding.lastName.text.clear()
                        binding.middleName.text.clear()
                        binding.city.text.clear()
                        binding.mobileNumber.text.clear()
                        binding.aadharNumber.text.clear()

                        val bundle = Bundle()
                        val nextFragment = FragmentAddRakam()
                        bundle.putString("f_name", fName)
                        bundle.putString("m_name", mName)
                        bundle.putString("l_name", lName)
                        bundle.putString("city", city)
                        bundle.putString("mobile_number", mobileNumber)
                        bundle.putString("aadhar_number", aadharNumber)
                        nextFragment.arguments = bundle

                        replaceFragment(nextFragment)
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Account insertion Failed", Toast.LENGTH_LONG).show()
                    }
            }
            else{
                db.collection("cust").document(custDocumentId)
                    .set(accountHashMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Account Updated Successfully", Toast.LENGTH_LONG).show()
                        binding.firstName.text.clear()
                        binding.lastName.text.clear()
                        binding.middleName.text.clear()
                        binding.city.text.clear()
                        binding.mobileNumber.text.clear()
                        binding.aadharNumber.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Account Update Failed", Toast.LENGTH_LONG).show()
                    }
            }

    }
}
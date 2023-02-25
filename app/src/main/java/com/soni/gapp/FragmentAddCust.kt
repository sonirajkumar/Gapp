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
    private var mobileNo: String = "null"
    private var aadharNo: String = "null"
    private lateinit var custDocumentId: String
    private var isTransferredFromSearch: Boolean = false
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle->
            fName = bundle.getString("f_name").toString()
            mName = bundle.getString("m_name").toString()
            lName = bundle.getString("l_name").toString()
            city = bundle.getString("city").toString()
            mobileNo = bundle.getString("mobile_number").toString()
            aadharNo = bundle.getString("aadhar_number").toString()
            isTransferredFromSearch = bundle.getBoolean("isTransferredFromSearch")
            custDocumentId = fName.filter { !it.isWhitespace() } +"_"+ mName.filter { !it.isWhitespace() } +"_"+ lName.filter { !it.isWhitespace() } +"_"+ city.filter { !it.isWhitespace() }+"_"+ mobileNo!!.filter { !it.isWhitespace() }+"_"+ aadharNo!!.filter { !it.isWhitespace() }

            alertBuilder = AlertDialog.Builder(activity)
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCustBinding.inflate(inflater, container, false)

        if (isTransferredFromSearch){
            binding.firstName.setText(fName)
            binding.middleName.setText(mName)
            binding.lastName.setText(lName)
            binding.city.setText(city)
            binding.mobileNumber.setText(mobileNo)
            binding.aadharNumber.setText(aadharNo)
            binding.addAccountButton.setText("Update Customer Info")
        }

        binding.addAccountButton.setOnClickListener{
            //Checking Data type
            fName = binding.firstName.text.toString().uppercase()
            lName = binding.lastName.text.toString().uppercase()
            mName = binding.middleName.text.toString().uppercase()
            city = binding.city.text.toString().uppercase()
            mobileNo = binding.mobileNumber.text.toString()
            aadharNo = binding.aadharNumber.text.toString()

            if (isTransferredFromSearch){
                if (fName.isEmpty() or lName.isEmpty() or mName.isEmpty() or city.isEmpty()) {
                    Toast.makeText(activity, "Please insert Valid Data ", Toast.LENGTH_LONG).show()
                } else if (mobileNo.isNotBlank() and (mobileNo.length != 10)) {
                    Toast.makeText(activity, "Please insert Valid Number ", Toast.LENGTH_LONG)
                        .show()
                } else if (aadharNo.isNotBlank() and (aadharNo.length != 12)) {
                    Toast.makeText(activity, "Please insert Valid Aadhar ", Toast.LENGTH_LONG)
                        .show()
                }
                else{
                    alertBuilder.setTitle("Confirmation")
                        .setMessage("Are you sure want to add account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->

                            val accountHashMap = hashMapOf(
                                "f_name" to fName,
                                "m_name" to mName,
                                "l_name" to lName,
                                "city" to city,
                                "mobile_no" to mobileNo,
                                "aadhar_no" to aadharNo

                            )

                            db.collection("cust").document(custDocumentId)
                                .set(accountHashMap, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        activity,
                                        "Account Updated Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    binding.firstName.text.clear()
                                    binding.lastName.text.clear()
                                    binding.middleName.text.clear()
                                    binding.city.text.clear()
                                    binding.mobileNumber.text.clear()
                                    binding.aadharNumber.text.clear()

                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        activity,
                                        "Account Update Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }


                        }
                        .setNegativeButton("No") { _, _ ->
                            Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                        }
                    alertBuilder.show()
                }
            }
            else {

                if (fName.isEmpty() or lName.isEmpty() or mName.isEmpty() or city.isEmpty()) {
                    Toast.makeText(activity, "Please insert Valid Data ", Toast.LENGTH_LONG).show()
                } else if (mobileNo.isNotBlank() and (mobileNo.length != 10)) {
                    Toast.makeText(activity, "Please insert Valid Number ", Toast.LENGTH_LONG)
                        .show()
                } else if (aadharNo.isNotBlank() and (aadharNo.length != 12)) {
                    Toast.makeText(activity, "Please insert Valid Aadhar ", Toast.LENGTH_LONG)
                        .show()
                } else {

                    // Creating Alert to add customer
                    alertBuilder.setTitle("Confirmation")
                        .setMessage("Are you sure want to add account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->

                            val accountHashMap = hashMapOf(
                                "f_name" to fName,
                                "m_name" to mName,
                                "l_name" to lName,
                                "city" to city,
                                "mobile_no" to mobileNo,
                                "aadhar_no" to aadharNo

                            )

                            db.collection("cust").document(
                                fName.filter { !it.isWhitespace() } + "_"
                                        + mName.filter { !it.isWhitespace() } + "_"
                                        + lName.filter { !it.isWhitespace() } + "_"
                                        + city.filter { !it.isWhitespace() } + "_"
                                        + mobileNo.filter { !it.isWhitespace() } + "_"
                                        + aadharNo.filter { !it.isWhitespace() })
                                .set(accountHashMap, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        activity,
                                        "Account Added Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
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
                                    bundle.putString("mobile_number", mobileNo)
                                    bundle.putString("aadhar_number", aadharNo)
                                    nextFragment.arguments = bundle

                                    replaceFragment(nextFragment)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        activity,
                                        "Account insertion Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }


                        }
                        .setNegativeButton("No") { _, _ ->
                            Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                        }
                    alertBuilder.show()
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





}
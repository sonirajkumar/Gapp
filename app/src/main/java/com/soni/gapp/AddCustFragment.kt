package com.soni.gapp

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


class AddCustFragment : Fragment() {
    private var _binding: FragmentAddCustBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder
    private lateinit var fName: String
    private lateinit var mName: String
    private var lName: String = "null"
    private var mobileNo: String = "null"
    private var aadharNo: String = "null"
    private var city: String = "null"
    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCustBinding.inflate(inflater, container, false)

        alertBuilder = AlertDialog.Builder(activity)

        binding.addAccountButton.setOnClickListener{
            //Checking Data type
            fName = binding.firstName.text.toString().uppercase()
            lName = binding.lastName.text.toString().uppercase()
            mName = binding.middleName.text.toString().uppercase()
            city = binding.city.text.toString().uppercase()
            mobileNo = binding.mobileNumber.text.toString()
            aadharNo = binding.aadharNumber.text.toString()

            if (fName.isNullOrEmpty() or lName.isNullOrEmpty() or mName.isNullOrEmpty() or city.isNullOrEmpty()){
                Toast.makeText(activity,"Please insert Valid Data ", Toast.LENGTH_LONG).show()
            }
            else if (mobileNo.isNotBlank() and (mobileNo.length != 10)){
                Toast.makeText(activity,"Please insert Valid Number ", Toast.LENGTH_LONG).show()
            }
            else if (aadharNo.isNotBlank() and (aadharNo.length != 12)){
                    Toast.makeText(activity,"Please insert Valid Aadhar ", Toast.LENGTH_LONG).show()
            }
            else {

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
                            fName.filter { !it.isWhitespace() } +"_"
                                    + mName.filter { !it.isWhitespace() } +"_"
                                    + lName.filter { !it.isWhitespace() } +"_"
                                    + city.filter { !it.isWhitespace() })
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
                                val nextFragment = AddRakamDetails()
                                bundle.putString("f_name", fName)
                                bundle.putString("m_name", mName)
                                bundle.putString("l_name", lName)
                                bundle.putString("city", city)
                                nextFragment.arguments = bundle

                                replaceFragment(nextFragment)
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity, "Account insertion Failed", Toast.LENGTH_LONG).show()
                            }


                    }
                    .setNegativeButton("No") { _, _ ->
                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                    }
                alertBuilder.show()
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
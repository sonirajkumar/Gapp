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
import com.soni.gapp.databinding.FragmentAddRakamDetailsBinding


class AddRakamDetails : Fragment() {

    private var _binding: FragmentAddRakamDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder
    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRakamDetailsBinding.inflate(inflater, container, false)

        val data = arguments
        val fName = data?.getString("f_name")
        val lName = data?.getString("l_name")
        val mName = data?.getString("m_name")
        val city = data?.getString("city")
        val showName = "Customer: $fName $mName $lName $city"
        binding.textViewName.text = showName

        alertBuilder = AlertDialog.Builder(activity)

        binding.addRakamButton.setOnClickListener {
            val rakamType = binding.rakamType.text.toString().uppercase()
            val rakamWeight = binding.rakamWeight.text.toString().uppercase()

            if (rakamType.isEmpty() or rakamWeight.isEmpty()){
                Toast.makeText(activity,"Please insert Valid Data ", Toast.LENGTH_LONG).show()
            }
            else{
                alertBuilder.setTitle("Confirmation")
                    .setMessage("Are you sure want to add rakam?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        val rakamHashMap = hashMapOf(
                            "rakam_type" to rakamType,
                            "weight_gms" to rakamWeight
                            )

                        db.collection("cust").document(
                            fName?.filter { !it.isWhitespace() } +"_"
                                    + mName?.filter { !it.isWhitespace() } +"_"
                                    + lName?.filter { !it.isWhitespace() } +"_"
                                    + city?.filter { !it.isWhitespace() })
                            .collection("rakam").document(rakamType.filter { !it.isWhitespace() }
                                    +"_"+rakamWeight+"GMS")
                            .set(rakamHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Rakam Added Successfully", Toast.LENGTH_LONG).show()
                                binding.rakamType.text.clear()
                                binding.rakamWeight.text.clear()

                                val bundle = Bundle()
                                val nextFragment = AddTransaction()
                                bundle.putString("f_name", fName)
                                bundle.putString("m_name", mName)
                                bundle.putString("l_name", lName)
                                bundle.putString("city", city)
                                bundle.putString("rakam_type", rakamType)
                                bundle.putString("rakam_weight", rakamWeight)
                                nextFragment.arguments = bundle

                                replaceFragment(nextFragment)
                            }
                            .addOnFailureListener{
                                Toast.makeText(activity, "Rakam insertion Failed", Toast.LENGTH_LONG).show()
                            }

                    }
                    .setNegativeButton("No"){_, _ ->
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
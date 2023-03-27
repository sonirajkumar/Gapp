package com.soni.gapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentAddRakamDetailsBinding


class FragmentAddRakam : Fragment() {

    private var _binding: FragmentAddRakamDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder
    private var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRakamDetailsBinding.inflate(inflater, container, false)

        val data = arguments
        val fName = data?.getString("f_name")
        val lName = data?.getString("l_name")
        val mName = data?.getString("m_name")
        val city = data?.getString("city")
        val mobileNo = data?.getString("mobile_number")
        val aadharNo = data?.getString("aadhar_number")
        val documentID = fName?.filter { !it.isWhitespace() } +"_"+ mName?.filter { !it.isWhitespace() } +"_"+ lName?.filter { !it.isWhitespace() } +"_"+ city?.filter { !it.isWhitespace() }+"_"+ mobileNo?.filter { !it.isWhitespace() }+"_"+ aadharNo?.filter { !it.isWhitespace() }
        val showName = "$fName $mName $lName $city"
        binding.textViewName.text = showName

        alertBuilder = AlertDialog.Builder(activity)

        db.collection("max_rakam_number").document("rakam_number").get()
            .addOnSuccessListener {
                val text = "Last Rakam Number: " + it.data?.get("rakam_number")?.toString()
                binding.tvLastRakamNumber.text = text
            }

        binding.addRakamButton.setOnClickListener {
            val rakamType = binding.rakamType.text.toString().uppercase()
            val rakamWeight = binding.rakamWeight.text.toString().uppercase()
            val rakamNumber = binding.rakamNumber.text.toString().uppercase()
            val radioGrpSelection = binding.radioGrpMetalType.checkedRadioButtonId
            val metalType = binding.root.findViewById<RadioButton>(radioGrpSelection).text.toString().uppercase()

            if (rakamType.isEmpty() or rakamWeight.isEmpty() or rakamNumber.isEmpty()){
                Toast.makeText(activity,"Please insert Valid Data ", Toast.LENGTH_LONG).show()
            }
            else{
                alertBuilder.setTitle("Confirmation")
                    .setMessage("Are you sure want to add rakam?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        val rakamHashMap = hashMapOf(
                            "metal_type" to metalType,
                            "rakam_type" to rakamType,
                            "weight_gms" to rakamWeight,
                            "rakam_number" to rakamNumber
                            )

                        db.collection("cust").document(documentID)
                            .collection("rakam").document(rakamType.filter { !it.isWhitespace() }
                                    +"_"+rakamWeight+"GMS")
                            .set(rakamHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Rakam Added Successfully", Toast.LENGTH_LONG).show()
                                binding.rakamType.text.clear()
                                binding.rakamWeight.text.clear()

                                val bundle = Bundle()
                                val nextFragment = FragmentAddTransaction()
                                bundle.putString("f_name", fName)
                                bundle.putString("m_name", mName)
                                bundle.putString("l_name", lName)
                                bundle.putString("city", city)
                                bundle.putString("mobile_number", mobileNo)
                                bundle.putString("aadhar_number", aadharNo)
                                bundle.putString("rakam_type", rakamType)
                                bundle.putString("rakam_weight", rakamWeight)
                                nextFragment.arguments = bundle

                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.frameLayout, nextFragment).commit()
                            }
                            .addOnFailureListener{
                                Toast.makeText(activity, "Rakam insertion Failed", Toast.LENGTH_LONG).show()
                            }
                        db.collection("max_rakam_number").document("rakam_number").set(hashMapOf("rakam_number" to rakamNumber), SetOptions.merge())

                    }
                    .setNegativeButton("No"){_, _ ->
                        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                    }
                alertBuilder.show()
            }
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
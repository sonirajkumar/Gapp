package com.soni.gapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentAddTransactionBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FragmentAddTransaction : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBuilder: AlertDialog.Builder
    private var db = Firebase.firestore
    private lateinit var editTextDate: EditText
    private lateinit var btnDatePicker: Button
    private var ir: String? = null
    private lateinit var radioBtn: RadioButton
    private lateinit var amount: String
    private lateinit var date:String
    private var remarks: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        val data = arguments
        val fName = data?.getString("f_name")
        val lName = data?.getString("l_name")
        val mName = data?.getString("m_name")
        val city = data?.getString("city")
        val mobileNo = data?.getString("mobile_number")
        val aadharNo = data?.getString("aadhar_number")
        val rakamType = data?.getString("rakam_type")
        val rakamWeight = data?.getString("rakam_weight")

        var radioSelection = binding.radioGrpNaameJama.checkedRadioButtonId
        radioBtn = binding.root.findViewById(radioSelection)

        val showName = "Customer: $fName $mName $lName $city"
        val showRakam = "Rakam: $rakamType: $rakamWeight GMS"

        binding.textViewName.text = showName
        binding.textViewRakam.text = showRakam

        val myCalender = Calendar.getInstance()
        editTextDate = binding.editTextDate
        btnDatePicker = binding.datePickerButton

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            editTextDate.setText(SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(myCalender.time))
        }
        btnDatePicker.setOnClickListener{
            DatePickerDialog(requireContext(), datePicker, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
            myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }

        alertBuilder = AlertDialog.Builder(activity)

        binding.radioGrpNaameJama.setOnCheckedChangeListener { _, _ ->
            radioSelection = binding.radioGrpNaameJama.checkedRadioButtonId
            radioBtn = binding.root.findViewById(radioSelection)

            binding.editTextIr.isEnabled = radioBtn.text.toString() != "Jama"
            if (radioBtn.text.toString() == "Jama"){
                binding.editTextIr.text.clear()
            }
        }


        binding.addTransactionBtn.setOnClickListener {
            amount = binding.editTextAmount.text.toString()
            remarks = binding.editTextRemarks.text.toString().uppercase()
            date = editTextDate.text.toString()

            if(radioBtn.text == "Naame"){
                ir = binding.editTextIr.text.toString()
            }

            if (amount.isEmpty() or date.isEmpty()){
                Toast.makeText(activity,"Please insert  ", Toast.LENGTH_LONG).show()
            }
            else if(binding.editTextIr.isEnabled == true and ir.isNullOrEmpty()){
                Toast.makeText(activity,"Please insert IR ", Toast.LENGTH_LONG).show()

            }
            else {

                alertBuilder.setTitle("Confirmation")
                    .setMessage("Are you sure want to add rakam?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        val transactionHashMap = hashMapOf(
                            "type" to radioBtn.text.toString(),
                            "amount" to amount.toInt(),
                            "ir" to ir?.toInt(),
                            "remarks" to remarks,
                            "date" to SimpleDateFormat("dd/MM/yyyy", Locale.UK).parse(date)
                        )
                        db.collection("cust").document(
                            fName?.filter { !it.isWhitespace() } +"_"
                                    + mName?.filter { !it.isWhitespace() } +"_"
                                    + lName?.filter { !it.isWhitespace() } +"_"
                                    + city?.filter { !it.isWhitespace() }+"_"
                                    + mobileNo?.filter { !it.isWhitespace() }+"_"
                                    + aadharNo?.filter { !it.isWhitespace() })
                            .collection("rakam").document(
                                rakamType?.filter { !it.isWhitespace() }
                                    +"_"+rakamWeight+"GMS")
                            .collection("transaction").document(LocalDateTime.now().toString())
                            .set(transactionHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Transaction Added Successfully", Toast.LENGTH_LONG).show()
                                binding.editTextAmount.text.clear()
                                binding.editTextRemarks.text.clear()
                                binding.editTextIr.text.clear()
                                binding.editTextDate.text.clear()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    activity,
                                    "Transaction insertion Failed",
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
        return binding.root
    }

}
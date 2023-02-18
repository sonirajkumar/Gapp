package com.soni.gapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soni.gapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var custSearchList = ArrayList<custSearchData>()
    private lateinit var custSearchView: SearchView
    private lateinit var radioBtn: RadioButton

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        custSearchView = binding.searchView

//        var radioSelection = binding.searchRadioGroup.checkedRadioButtonId
//        radioBtn = binding.root.findViewById(radioSelection)

//        binding.searchRadioGroup.setOnCheckedChangeListener { _, _ ->
//            radioSelection = binding.searchRadioGroup.checkedRadioButtonId
//            radioBtn = binding.root.findViewById(radioSelection)
//
//        }

        custSearchView.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    getCustData(query.uppercase())
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return binding.root
    }

    fun getCustData(query: String?){
        val collectionRef = db.collection("cust")
        collectionRef.get().addOnSuccessListener {
            if (!it.isEmpty){
                custSearchList.clear()
                for (docs in it){
                    if(docs.id.contains("$query")){
                        println(docs.id)
                        collectionRef.document(docs.id).get().addOnSuccessListener {
                            document->
                            val cust = custSearchData(document.data?.get("f_name") as String,
                                document.data?.get("m_name") as String,
                                document.data?.get("l_name") as String,
                                document.data?.get("city") as String)
                            custSearchList.add(cust)
                        }
                            .addOnFailureListener {
                                Toast.makeText(context, "Data Fetching Failed", Toast.LENGTH_LONG).show()
                            }

                    }
                }
                recyclerView.adapter = custSearchAdapter(custSearchList)
            }
        }
            .addOnFailureListener{
                Toast.makeText(context, "Data Fetching Failed", Toast.LENGTH_LONG).show()
            }

    }
}

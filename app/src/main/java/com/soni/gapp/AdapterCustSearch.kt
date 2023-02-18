package com.soni.gapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class AdapterCustSearch(private val custList: ArrayList<DataCustSearch>): RecyclerView.Adapter<AdapterCustSearch.CustSearchViewHolder>() {
    inner class CustSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val custDetails: TextView = itemView.findViewById(R.id.CustDetails)
        val custConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.CustomerConstrainLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_items, parent, false)
        return CustSearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return custList.size
    }

    override fun onBindViewHolder(holder: CustSearchViewHolder, position: Int) {
        val custSearchData = custList[position]
        val custDetailsText: String = custSearchData.fName + " " +
                custSearchData.mName + " " +
                custSearchData.lName + " " +
                custSearchData.city
        holder.custDetails.text = custDetailsText

        holder.custConstraintLayout.setOnClickListener {

        }
    }

}
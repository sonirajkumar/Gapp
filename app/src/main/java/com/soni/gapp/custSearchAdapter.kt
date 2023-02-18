package com.soni.gapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class custSearchAdapter(private val custList: ArrayList<custSearchData>): RecyclerView.Adapter<custSearchAdapter.custSearchViewHolder>() {
    inner class custSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val custDetails: TextView = itemView.findViewById(R.id.cust_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): custSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_items, parent, false)
        return custSearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return custList.size
    }

    override fun onBindViewHolder(holder: custSearchViewHolder, position: Int) {
        val custDetailsText = custList[position].fName + " " +
                custList[position].mName + " " +
                custList[position].lName + " " +
                custList[position].city
        holder.custDetails.text = custDetailsText
    }
}
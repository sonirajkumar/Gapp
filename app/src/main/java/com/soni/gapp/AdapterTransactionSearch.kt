package com.soni.gapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterTransactionSearch(private val tranList: ArrayList<DataTransactionSearch>): RecyclerView.Adapter<AdapterTransactionSearch.TranSearchViewHolder>() {

    inner class TranSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tranDetails: TextView = itemView.findViewById(R.id.SearchDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterTransactionSearch.TranSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_items, parent, false)
        return TranSearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterTransactionSearch.TranSearchViewHolder, position: Int) {
        val tranSearchData = tranList[position]
        val tranSearchText = tranSearchData.tranType + " " + tranSearchData.amount+" "+ tranSearchData.ir + " " + tranSearchData.date +" "+ tranSearchData.remarks
        holder.tranDetails.text = tranSearchText
    }

    override fun getItemCount(): Int {
        return tranList.size
    }
}
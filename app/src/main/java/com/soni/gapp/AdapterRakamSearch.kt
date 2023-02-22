package com.soni.gapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class AdapterRakamSearch(private  val rakamList: ArrayList<DataRakamSearch>): RecyclerView.Adapter<AdapterRakamSearch.RakamSearchViewHolder>() {
    inner class RakamSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rakamDetails: TextView = itemView.findViewById(R.id.SearchDetails)
        val custConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.SearchConstrainLayout)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterRakamSearch.RakamSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_items, parent, false)
        return RakamSearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterRakamSearch.RakamSearchViewHolder, position: Int) {
        val rakamSearchData = rakamList[position]
        val rakamSearchText = rakamSearchData.rakamType + " " + rakamSearchData.rakamWeight + " GMS"
        holder.rakamDetails.text = rakamSearchText
    }

    override fun getItemCount(): Int {
        return rakamList.size
    }
}
package com.soni.gapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CustSearchAdapter(private val custList: ArrayList<CustSearchData>): RecyclerView.Adapter<CustSearchAdapter.CustSearchViewHolder>() {
    inner class CustSearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val custDetails: TextView = itemView.findViewById(R.id.CustDetails)
        val rakamDetails: TextView = itemView.findViewById(R.id.RakamDetails)
        val custConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.CustomerConstrainLayout)

        fun collapseExpandedCustomer(){
            rakamDetails.visibility = View.GONE
        }
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
        val rakamDetailsText: String = custSearchData.rakam+" "+custSearchData.weight+" GMS"
        holder.custDetails.text = custDetailsText
        holder.rakamDetails.text = rakamDetailsText
//        setting rakam details visibility
        val isCustExpandable: Boolean = custSearchData.isCustExpandable
        holder.rakamDetails.visibility = if (isCustExpandable) View.VISIBLE else View.GONE

        holder.custConstraintLayout.setOnClickListener {
            isAnyCustExpanded(position)
            custSearchData.isCustExpandable = !custSearchData.isCustExpandable
            notifyItemChanged(position, Unit)
        }
    }

    private fun isAnyCustExpanded(position: Int){
        val temp = custList.indexOfFirst {
            it.isCustExpandable
        }
        if (temp >=0 && temp != position){
            custList[temp].isCustExpandable = false
            notifyItemChanged(temp, 0)
        }
    }

    override fun onBindViewHolder(
        holder: CustSearchViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == 0){
            holder.collapseExpandedCustomer()
        }
        else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}
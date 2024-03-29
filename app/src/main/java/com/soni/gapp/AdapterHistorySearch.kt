package com.soni.gapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.soni.gapp.databinding.HistoryDateItemBinding
import com.soni.gapp.databinding.HistoryCustItemsBinding

const val ITEM_WITH_DATE = 0
const val ITEM_WITHOUT_DATE = 1
class AdapterHistorySearch(private val custDataList: List<DataHistorySearch>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class DateViewHolder(private val dateBinding: HistoryDateItemBinding): RecyclerView.ViewHolder(dateBinding.root){
       fun bindDate(dataItem: DataHistorySearch) {
           dateBinding.HistoryDateDetails.text = dataItem.timestamp
       }
    }
    inner class CustDataViewHolder(private val custDataBinding: HistoryCustItemsBinding): RecyclerView.ViewHolder(custDataBinding.root){
        fun bindCustData(dataItem: DataHistorySearch){
            val custDataText = "(" +dataItem.cid + ") " +
                    dataItem.fName + " " +
                    dataItem.mName + " " +
                    dataItem.lName + " " +
                    dataItem.city

            custDataBinding.HistorySearchDetails.text = custDataText
        }
        val custDataConstraintLayout: ConstraintLayout = custDataBinding.HistoryConstrainLayout
    }

    override fun getItemViewType(position: Int): Int {
        return if (custDataList[position].timestamp != "null"){
            ITEM_WITH_DATE
        } else {
            ITEM_WITHOUT_DATE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_WITH_DATE){
            val binding = HistoryDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateViewHolder(binding)
        }else{
            val binding = HistoryCustItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CustDataViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return custDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_WITH_DATE){
            (holder as DateViewHolder).bindDate(custDataList[position])
        }else{
            (holder as CustDataViewHolder).bindCustData(custDataList[position])
            holder.custDataConstraintLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("f_name", custDataList[position].fName)
                bundle.putString("m_name", custDataList[position].mName)
                bundle.putString("l_name", custDataList[position].lName)
                bundle.putString("city", custDataList[position].city)
                bundle.putString("mobile_number", custDataList[position].mobileNumber)
                bundle.putString("aadhar_number", custDataList[position].aadharNumber)
                bundle.putString("cid", custDataList[position].cid)
                val nextFragment = FragmentRakamSearch()
                nextFragment.arguments = bundle
                val appCompactActivity = it.context as AppCompatActivity

                appCompactActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, nextFragment).addToBackStack(null)
                    .commit()
            }
        }
    }
}
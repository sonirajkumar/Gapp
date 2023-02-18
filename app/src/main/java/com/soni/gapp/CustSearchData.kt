package com.soni.gapp

data class CustSearchData(
    val fName: String,
    val mName: String,
    val lName: String,
    val city: String,
    val rakam: String? = "",
    val weight: Int? = 0,
    val transaction_type: String? = "",
    val amount: Int? = 0,
    val ir: Float? = 0.0f,
    val remarks: String? = "",
    val date: String = "",
    var isCustExpandable: Boolean = false,
    var isRakamExpandable: Boolean = false,
    var isTransactionExpandable: Boolean = false

)

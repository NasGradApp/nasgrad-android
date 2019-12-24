package com.nasgrad.adapter

interface OnItemClickListener {

    fun onItemClicked(
        itemId: String,
        itemTitle: String?,
        itemType: String?,
        itemDesc: String?,
        itemImage: String?
    )
}
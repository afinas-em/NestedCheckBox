package com.test.jsonchecktest.models

data class ChildrenItem(
    val companyId: Int? = null,
    val children: List<ChildrenItem?>? = null,
    val name: String? = null,
    val assetType: String? = null,
    val id: Int? = null,
    val brandId: Int? = null,
    val brandRegionId: Int? = null
){
	var isChecked: Boolean? = false
}

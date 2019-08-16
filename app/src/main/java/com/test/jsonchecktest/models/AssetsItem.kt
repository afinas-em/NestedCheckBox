package com.test.jsonchecktest.models

data class AssetsItem(
	val children: List<ChildrenItem?>? = null,
	val name: String? = null,
	val assetType: String? = null,
	val id: Int? = null
) {
	var isChecked: Boolean? = false
}

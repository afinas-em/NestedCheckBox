package com.test.jsonchecktest.models

data class Response(
	val response: Response? = null,
	val message: String? = null,
	val assets: List<AssetsItem?>? = null,
	val proceed: Boolean? = null
)

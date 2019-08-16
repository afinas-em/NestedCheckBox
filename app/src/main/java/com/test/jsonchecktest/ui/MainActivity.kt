package com.test.jsonchecktest.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buildware.widget.indeterm.IndeterminateCheckBox
import com.test.jsonchecktest.R
import com.test.jsonchecktest.models.AssetsItem
import com.test.jsonchecktest.models.Response
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    private var data: Response? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getApiResponse()

        parseData()

    }

    //Ui changes
    private fun updateUi() {

        val assetsList: List<AssetsItem?>? = data?.response?.assets
        if (assetsList == null || assetsList.isEmpty())
            return

        val asset = assetsList[0]
        updateCheckBox(ASSET, asset?.name, asset?.isChecked)
        for (brands in asset?.children!!) {
            updateCheckBox(BRAND, brands?.name!!, brands.isChecked)
            for (brand_region in brands.children!!) {
                updateCheckBox(BRAND_REGION, brand_region?.name!!, brand_region.isChecked)
                for (branch in brand_region.children!!)
                    updateCheckBox(BRANCH, branch?.name!!, branch.isChecked)

            }

        }
    }

    private fun updateCheckBox(type: String, name: String?, checked: Boolean?) {
        val checkBox = lytContainer.findViewWithTag(type + name) as IndeterminateCheckBox
        checkBox.state = checked
    }

    //Get response from API
    private fun getApiResponse() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.myjson.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiCalls::class.java)

        retrofit.getData().enqueue(object : Callback<Response?> {

            override fun onResponse(call: Call<Response?>, response: retrofit2.Response<Response?>) {

                Log.e("AA", "Res: "+response.body().toString())

                data = response.body()

                prgssBar.visibility = View.GONE
                parseData()

            }

            override fun onFailure(call: Call<Response?>, t: Throwable) {
                prgssBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Error: " + t.message, Toast.LENGTH_SHORT).show()
                Log.e("AA", "Error: " + t.message)
            }
        })
    }

    // Draw dynamic UI
    private fun parseData() {

        lytContainer.removeAllViews()
        val assetsList: List<AssetsItem?>? = data?.response?.assets
        if (assetsList == null || assetsList.isEmpty())
            return

        val asset = assetsList[0]
        val checkBox = getCheckBox(ASSET, asset?.name!!)
        lytContainer.addView(checkBox)

        val children = asset.children
        checkBox.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->

            if (compoundButton?.isPressed!!) {
                asset.isChecked = b
                for (brands in children!!) {
                    brands?.isChecked = b
                    val children1 = brands?.children
                    for (brand_region in children1!!) {
                        brand_region?.isChecked = b
                        val children2 = brand_region?.children
                        for (branch in children2!!) branch?.isChecked = b
                    }
                }
            }

            updateUi()
        }

        for (brands in children!!) {
            var params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(50, 0, 0, 0)
            val checkBox1 = getCheckBox(BRAND, brands?.name!!)
            lytContainer.addView(checkBox1, params)
            checkBox1.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
                if (compoundButton?.isPressed!!) {
                    brands.isChecked = b
                    val children1 = brands.children
                    for (brand_region in children1!!) {
                        brand_region?.isChecked = b
                        val children2 = brand_region?.children
                        for (branch in children2!!) branch?.isChecked = b
                    }

                    val checked = children.count { it?.isChecked == true }
                    val misc = children.count { it?.isChecked == null }
                    val count = children.size

                    when {
                        misc > 0 -> asset.isChecked = null
                        checked == count -> asset.isChecked = true
                        checked == 0 -> asset.isChecked = false
                        else -> asset.isChecked = null
                    }

                    updateUi()
                }

            }

            val children1 = brands.children
            for (brand_region in children1!!) {

                params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(100, 0, 0, 0)
                val checkBox2 = getCheckBox(BRAND_REGION, brand_region?.name!!)
                lytContainer.addView(checkBox2, params)

                checkBox2.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
                    if (compoundButton?.isPressed!!) {

                        brand_region.isChecked = b
                        val children2 = brand_region.children
                        for (branch in children2!!)
                            branch?.isChecked = b

                        var checked = children1.count { it?.isChecked == true }
                        var misc = children1.count { it?.isChecked == null }
                        var count = children1.size

                        when {
                            misc > 0 -> brands.isChecked = null
                            checked == count -> brands.isChecked = true
                            checked == 0 -> brands.isChecked = false
                            else -> brands.isChecked = null
                        }

                        checked = children.count { it?.isChecked == true }
                        misc = children.count { it?.isChecked == null }
                        count = children.size

                        when {
                            misc > 0 -> asset.isChecked = null
                            checked == count -> asset.isChecked = true
                            checked == 0 -> asset.isChecked = false
                            else -> asset.isChecked = null
                        }

                        updateUi()
                    }
                }

                val children2 = brand_region.children
                for (branch in children2!!) {
                    params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val checkBox3 = getCheckBox(BRANCH, branch?.name!!)
                    params.setMargins(150, 0, 0, 0)
                    lytContainer.addView(checkBox3, params)
                    checkBox3.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
                        if (compoundButton?.isPressed!!) {
                            branch.isChecked = b

                            var checked = children2.count { it?.isChecked == true }
                            var misc = children2.count { it?.isChecked == null }
                            var count = children2.size

                            when {
                                misc > 0 -> brand_region.isChecked = null
                                checked == count -> brand_region.isChecked = true
                                checked == 0 -> brand_region.isChecked = false
                                else -> brand_region.isChecked = null
                            }

                            checked = children1.count { it?.isChecked == true }
                            misc = children1.count { it?.isChecked == null }
                            count = children1.size


                            when {
                                misc > 0 -> brands.isChecked = null
                                checked == count -> brands.isChecked = true
                                checked == 0 -> brands.isChecked = false
                                else -> brands.isChecked = null
                            }

                            checked = children.count { it?.isChecked == true }
                            misc = children.count { it?.isChecked == null }
                            count = children.size

                            when {
                                misc > 0 -> asset.isChecked = null
                                checked == count -> asset.isChecked = true
                                checked == 0 -> asset.isChecked = false
                                else -> asset.isChecked = null
                            }

                            updateUi()
                        }
                    }
                }

            }

        }


    }

    // Create checkbox
    private fun getCheckBox(type: String, name: String): IndeterminateCheckBox {

        val chk = IndeterminateCheckBox(applicationContext)
        chk.apply {
            text = name
            isIndeterminate = true
            state = false
            tag = type + name
        }

        return chk
    }

    // Retrofit interface
    interface ApiCalls {
        @GET("/bins/14aiup")
        fun getData(): Call<Response>
    }

    companion object Type {
        private const val ASSET = "ASSET"
        private const val BRAND = "BRAND"
        private const val BRAND_REGION = "BRAND_REGION"
        private const val BRANCH = "BRANCH"
    }

}

//........................Test Data.....................
//
//
//val json = "{\n" +
//        "    \"message\": \"Success.\",\n" +
//        "    \"response\": {\n" +
//        "        \"assets\": [\n" +
//        "            {\n" +
//        "                \"id\": 1,\n" +
//        "                \"name\": \"LivingBrands\",\n" +
//        "                \"asset_type\": \"company\",\n" +
//        "                \"children\": [\n" +
//        "                    {\n" +
//        "                        \"id\": 5,\n" +
//        "                        \"name\": \"Lemongrass\",\n" +
//        "                        \"company_id\": 1,\n" +
//        "                        \"asset_type\": \"brand\",\n" +
//        "                        \"children\": [\n" +
//        "                            {\n" +
//        "                                \"id\": 8,\n" +
//        "                                \"name\": \"UAE\",\n" +
//        "                                \"brand_id\": 5,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 1,\n" +
//        "                                        \"name\": \"Oud Metha\",\n" +
//        "                                        \"brand_id\": 5,\n" +
//        "                                        \"brand_region_id\": 8,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            }\n" +
//        "                        ]\n" +
//        "                    },\n" +
//        "                    {\n" +
//        "                        \"id\": 6,\n" +
//        "                        \"name\": \"Asian5\",\n" +
//        "                        \"company_id\": 1,\n" +
//        "                        \"asset_type\": \"brand\",\n" +
//        "                        \"children\": [\n" +
//        "                            {\n" +
//        "                                \"id\": 9,\n" +
//        "                                \"name\": \"A5-UAE\",\n" +
//        "                                \"brand_id\": 6,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 3,\n" +
//        "                                        \"name\": \"Asian5 DAFZA\",\n" +
//        "                                        \"brand_id\": 6,\n" +
//        "                                        \"brand_region_id\": 9,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    },\n" +
//        "                                    {\n" +
//        "                                        \"id\": 9,\n" +
//        "                                        \"name\": \"Asian5 Downtown\",\n" +
//        "                                        \"brand_id\": 6,\n" +
//        "                                        \"brand_region_id\": 9,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            },\n" +
//        "                            {\n" +
//        "                                \"id\": 15,\n" +
//        "                                \"name\": \"A5-India\",\n" +
//        "                                \"brand_id\": 6,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 13,\n" +
//        "                                        \"name\": \"Calicut Beach\",\n" +
//        "                                        \"brand_id\": 6,\n" +
//        "                                        \"brand_region_id\": 15,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            },\n" +
//        "                            {\n" +
//        "                                \"id\": 16,\n" +
//        "                                \"name\": \"A5 USA\",\n" +
//        "                                \"brand_id\": 6,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 14,\n" +
//        "                                        \"name\": \"Food Mansion\",\n" +
//        "                                        \"brand_id\": 6,\n" +
//        "                                        \"brand_region_id\": 16,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            }\n" +
//        "                        ]\n" +
//        "                    },\n" +
//        "                    {\n" +
//        "                        \"id\": 7,\n" +
//        "                        \"name\": \"City Dance\",\n" +
//        "                        \"company_id\": 1,\n" +
//        "                        \"asset_type\": \"brand\",\n" +
//        "                        \"children\": [\n" +
//        "                            {\n" +
//        "                                \"id\": 13,\n" +
//        "                                \"name\": \"CD Thailand\",\n" +
//        "                                \"brand_id\": 7,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 11,\n" +
//        "                                        \"name\": \"City Dance Restaurant\",\n" +
//        "                                        \"brand_id\": 7,\n" +
//        "                                        \"brand_region_id\": 13,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            }\n" +
//        "                        ]\n" +
//        "                    },\n" +
//        "                    {\n" +
//        "                        \"id\": 8,\n" +
//        "                        \"name\": \"Sea Salt\",\n" +
//        "                        \"company_id\": 1,\n" +
//        "                        \"asset_type\": \"brand\",\n" +
//        "                        \"children\": [\n" +
//        "                            {\n" +
//        "                                \"id\": 14,\n" +
//        "                                \"name\": \"SS Thailand\",\n" +
//        "                                \"brand_id\": 8,\n" +
//        "                                \"asset_type\": \"brand_region\",\n" +
//        "                                \"children\": [\n" +
//        "                                    {\n" +
//        "                                        \"id\": 12,\n" +
//        "                                        \"name\": \"Sea Salt\",\n" +
//        "                                        \"brand_id\": 8,\n" +
//        "                                        \"brand_region_id\": 14,\n" +
//        "                                        \"asset_type\": \"branch\"\n" +
//        "                                    }\n" +
//        "                                ]\n" +
//        "                            }\n" +
//        "                        ]\n" +
//        "                    }\n" +
//        "                ]\n" +
//        "            }\n" +
//        "        ],\n" +
//        "        \"proceed\": true\n" +
//        "    }\n" +
//        "}"

package com.example.cryptoapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cryptoapp.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter
    private lateinit var data:ArrayList<Model>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        data = ArrayList<Model>()
        apiData
        rvAdapter=RvAdapter(this,data)
        binding.Rv.layoutManager = LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter

        binding.search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val filterdata=ArrayList<Model>()
                for (item in data){
                    if(item.name.lowercase(Locale.getDefault()).contains(s.toString().lowercase(
                            Locale.getDefault()))){
                        filterdata.add(item)
                    }
                }
                if (filterdata.isEmpty()){
                    Toast.makeText(this@MainActivity,"No such coin available",Toast.LENGTH_LONG).show()
                }else{
                    rvAdapter.changeData(filterdata)
                }
            }

        })
    }
    val apiData:Unit
    get() {
        val url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest:JsonObjectRequest=

            @SuppressLint("NotifyDataSetChanged")
            object:JsonObjectRequest(Method.GET,url,null, Response.Listener {
                response ->
                binding.progressBar2.isVisible=false
                try {
                    val dataArray = response.getJSONArray("data")
                    for (i in 0 until dataArray.length())
                    {
                        val dataObject = dataArray.getJSONObject(i)
                        val symbol = dataObject.getString("symbol")
                        val name = dataObject.getString("name")
                        val quote = dataObject.getJSONObject("quote")
                        val USD = quote.getJSONObject("USD")
                        val price = String.format("$ "+"%.2f",USD.getDouble("price"))
                        data.add(Model(name,symbol,price))


                    }
                    rvAdapter.notifyDataSetChanged()
                }catch (e:Exception){
                    Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this,"Error 1",Toast.LENGTH_LONG).show()
            })
            {
                override fun getHeaders(): Map<String, String> {
                    val header=HashMap<String,String>();
                    header["X-CMC_PRO_API_KEY"]="2bc923a4-0950-4677-8dbc-20468e7ce35b"
                    return header

                }
            }

        queue.add(jsonObjectRequest)
    }
}
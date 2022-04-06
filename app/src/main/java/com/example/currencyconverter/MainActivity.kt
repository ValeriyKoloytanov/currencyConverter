package com.example.currencyconverter

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.mynameismidori.currencypicker.CurrencyPicker
import com.mynameismidori.currencypicker.ExtendedCurrency


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var from_cur = "RUB"
    private var to_cur = "USD"
    var symbol_to = "$"
    var symbol_from = "₽"
    fun pickcurrency(imageView: ImageView) {
        val currencies = mutableListOf(
            ExtendedCurrency(
                "EUR",
                "Евро",
                "€",
                com.mynameismidori.currencypicker.R.drawable.flag_eur
            ),
            ExtendedCurrency(
                "RUB",
                "Рубль",
                "₽",
                com.mynameismidori.currencypicker.R.drawable.flag_rub
            ),
            ExtendedCurrency(
                "USD",
                "Доллар",
                "$",
                com.mynameismidori.currencypicker.R.drawable.flag_usd
            ),
            ExtendedCurrency(
                "GBP",
                "Фунт стерлингов",
                "£",
                com.mynameismidori.currencypicker.R.drawable.flag_gbp
            ),
            ExtendedCurrency(
                "JPY",
                "Японская иена",
                "¥",
                com.mynameismidori.currencypicker.R.drawable.flag_jpy
            ),
            ExtendedCurrency(
                "CNY",
                "Китайский юань",
                "¥",
                com.mynameismidori.currencypicker.R.drawable.flag_cny
            ),
            ExtendedCurrency(
                "MXN",
                "Мексиканское песо",
                "$",
                com.mynameismidori.currencypicker.R.drawable.flag_mxn
            ),
            ExtendedCurrency(
                "CHF",
                "Швецарский франк",
                "₣",
                com.mynameismidori.currencypicker.R.drawable.flag_chf
            ),
            ExtendedCurrency(
                "INR",
                "Индийская рупия",
                "₹",
                com.mynameismidori.currencypicker.R.drawable.flag_inr
            ),
            ExtendedCurrency(
                "CAD",
                "Канадский доллар",
                "C$",
                com.mynameismidori.currencypicker.R.drawable.flag_cad
            ),
        )
        val picker: CurrencyPicker = CurrencyPicker.newInstance("Select Currency")
        picker.setCurrenciesList(currencies)
        picker.setListener { name, code, symbol, flagDrawableResID ->
            imageView.setImageResource(flagDrawableResID)

            if (imageView.id == binding.toImage.id) {
                to_cur = code
                symbol_to = symbol
            } else {
                from_cur = code
                symbol_from = symbol
                binding.textView2.text = symbol_from

            }
            binding.webview.loadUrl("file:///android_res/raw/page.html?currency=$from_cur$to_cur")

            picker.dismiss()

        }
        picker.show(supportFragmentManager, "CURRENCY_PICKER")

    }


    fun setupwebview() {
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webview.settings.useWideViewPort = false
        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.setInitialScale(180)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupwebview()
        binding.webview.loadUrl("file:///android_res/raw/page.html?currency=$from_cur$to_cur")

        binding.fromImage.setOnClickListener {
            pickcurrency(it as ImageView)
            binding.toCurrencyview.text = null
        }
        binding.toImage.setOnClickListener {
            pickcurrency(it as ImageView)


        }
        binding.resetBtn.setOnClickListener {
           binding.currencyNumber.text.clear()
            binding.toCurrencyview.text = null

        }
        binding.convertBtn.setOnClickListener {
            if (binding.currencyNumber.text.isNotEmpty()) {
                val url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=$from_cur&to_currency=$to_cur&apikey=7EO11VQ5B9XEN4UW"
                val cache = DiskBasedCache(cacheDir, 1024 * 1024)
                val network = BasicNetwork(HurlStack())

                val requestQueue = RequestQueue(cache, network).apply {
                    start()
                }
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        val result =
                            response.getJSONObject( "Realtime Currency Exchange Rate").getDouble("5. Exchange Rate") * binding.currencyNumber.text.toString().toDouble()
                        val currency_converted = "%.4f".format(result) + "    " + symbol_to
                        binding.toCurrencyview.text = currency_converted
                    },
                    {
                        if (it is NetworkError) {
                            Toast.makeText(this, "Нет соединения", Toast.LENGTH_LONG).show()

                        } else if (it!=null){
                            Toast.makeText(this, "Нет удалось подключиться к API", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                requestQueue.add(jsonObjectRequest)


            }
            else {
                Toast.makeText(this, "Введите число", Toast.LENGTH_LONG).show()

            }
        }

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return url != "https://www.tradingview.com"
            }
        }
    }

}
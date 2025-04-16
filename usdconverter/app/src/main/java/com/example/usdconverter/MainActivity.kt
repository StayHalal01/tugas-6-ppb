package com.example.usdconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.usdconverter.ui.theme.USDConverterTheme
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            USDConverterTheme {
                USDConverterUI()
            }
        }
    }
}

@Composable
fun USDConverterUI() {
    var inputAmount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("IDR") }
    var conversionResult by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val supportedCurrencies = listOf("IDR", "EUR", "JPY", "GBP", "KRW")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "USD Currency Converter",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = inputAmount,
                onValueChange = { inputAmount = it },
                label = { Text("Amount in USD") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CurrencyDropdown(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it },
                currencyList = supportedCurrencies
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val amount = inputAmount.toDoubleOrNull()
                    if (amount != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val rate = getUSDRate(to = selectedCurrency)
                            withContext(Dispatchers.Main) {
                                if (rate != null) {
                                    val convertedValue = amount * rate
                                    val formatted = NumberFormat.getNumberInstance(Locale.US).format(convertedValue)
                                    conversionResult = "$formatted $selectedCurrency"
                                } else {
                                    conversionResult = "Failed to fetch rate"
                                }
                            }
                        }
                    } else {
                        conversionResult = "Invalid input amount"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Convert Currency",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = conversionResult,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    currencyList: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = "Convert to:")
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = selectedCurrency)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencyList.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun getUSDRate(to: String): Double? {
    return try {
        val apiKey = "87d9cb482589ab72b56af8bb"
        val response = URL("https://v6.exchangerate-api.com/v6/$apiKey/latest/USD").readText()
        val json = JSONObject(response)
        val rates = json.getJSONObject("conversion_rates")
        rates.getDouble(to)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

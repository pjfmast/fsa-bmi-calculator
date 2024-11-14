package avans.avd.bmicalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import avans.avd.bmicalc.ui.theme.BMICalcTheme
import avans.avd.bmicalc.ui.theme.PurpleGrey80
import java.math.RoundingMode
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BMICalcTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BmiLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun BmiLayout(modifier: Modifier = Modifier) {
    // no auto import suggestion in AS for extension function get/setValue
    // see: https://stackoverflow.com/questions/67360038/why-cant-i-use-mutablestate-as-a-property-delegate
    var lengthInput by rememberSaveable { mutableStateOf("170") }
    var weightInput by rememberSaveable { mutableStateOf("65") }

    val length = lengthInput.toIntOrNull() ?: 0
    val weight = weightInput.toIntOrNull() ?: 0
    val bmi = bmiText(length, weight)

    // Surface vs Box: https://stackoverflow.com/questions/65918835/when-should-i-use-android-jetpack-composes-surface-composable
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .background(PurpleGrey80)
                    .fillMaxWidth()
            ) {
                Text("BMI Calculator", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = stringResource(R.string.enter_height_and_weight),
                modifier = modifier
                    .align(alignment = Alignment.Start)
                    .padding(top = 40.dp, bottom = 20.dp)
            )

            EditNumberField(
                value = lengthInput,
                onValueChanged = { lengthInput = it },
                label = stringResource(R.string.height)
            )

            Spacer(modifier = Modifier.height(16.dp))
            EditNumberField(
                value = weightInput,
                onValueChanged = { weightInput = it },
                isDone = true,
                label = stringResource(R.string.weight)
            )

            Spacer(Modifier.height(80.dp))

            if (bmi.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.your_bmi, bmi),
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
fun EditNumberField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDone: Boolean = false,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {

    TextField(
        value = value,
        singleLine = true,
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Number,
            imeAction = if (isDone) ImeAction.Done else ImeAction.Next
        ),
        onValueChange = onValueChanged,
        label = { Text(label) },
        modifier = modifier
    )
}

private fun bmiText(height: Int, weight: Int): String {
    val bmi = calculateBMI(height, weight)
    val roundedBMI = bmi
        ?.toBigDecimal()
        ?.setScale(1, RoundingMode.HALF_UP)
        ?.toString()
        ?: "BMI cannot be calculated"

    return roundedBMI
}

private fun calculateBMI(height: Int, weight: Int): Double? {
    return if (height > 0 && weight > 0) {
        val lengthInMeter = height / 100.0
        weight / (lengthInMeter.pow(2))
    } else null
}

@PreviewLightDark()
@Preview(showBackground = true, locale = "nl")
@Composable
fun GreetingPreview() {
    BMICalcTheme {
        BmiLayout()
    }
}


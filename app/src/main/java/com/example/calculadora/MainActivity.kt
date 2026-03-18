package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    // Gerenciamento de Estado (A Lógica)
    var displayText by remember { mutableStateOf("0") }
    var operand1 by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var isNewOperand by remember { mutableStateOf(true) }

    fun onAction(action: String) {
        when (action) {
            "C" -> {
                displayText = "0"
                operand1 = ""
                operator = ""
                isNewOperand = true
            }
            "+", "-", "*", "/" -> {
                if (operand1.isNotEmpty() && !isNewOperand) {
                    // Permite encadear operações sem apertar '='
                    val result = calculate(operand1, displayText, operator)
                    displayText = formatResult(result)
                    operand1 = displayText
                } else {
                    operand1 = displayText
                }
                operator = action
                isNewOperand = true
            }
            "=" -> {
                if (operand1.isNotEmpty() && operator.isNotEmpty()) {
                    val result = calculate(operand1, displayText, operator)
                    displayText = formatResult(result)
                    operand1 = ""
                    operator = ""
                    isNewOperand = true
                }
            }
            else -> { // É um número
                if (isNewOperand) {
                    displayText = action
                    isNewOperand = false
                } else {
                    displayText += action
                }
            }
        }
    }

    // Camada Visual (UI)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF222222)) // Cor de fundo escura
            .padding(16.dp)
    ) {
        // Display da Calculadora
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = displayText,
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        // Teclado da Calculadora
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", "C", "=", "+")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { symbol ->
                        CalculatorButton(
                            symbol = symbol,
                            modifier = Modifier.weight(1f),
                            onClick = { onAction(symbol) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(35.dp), // Formato de pílula arredondada
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4C5B92) // Azul/Roxo do layout
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = symbol,
            fontSize = 32.sp,
            color = Color.White
        )
    }
}

// Funções Auxiliares de Lógica
fun calculate(op1: String, op2: String, operator: String): Double {
    val num1 = op1.toDoubleOrNull() ?: 0.0
    val num2 = op2.toDoubleOrNull() ?: 0.0
    return when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0.0) num1 / num2 else 0.0 // Evita divisão por zero
        else -> 0.0
    }
}

fun formatResult(result: Double): String {
    // Remove o ".0" se for um número inteiro para ficar mais bonito no display
    return if (result % 1.0 == 0.0) {
        result.toInt().toString()
    } else {
        result.toString()
    }
}
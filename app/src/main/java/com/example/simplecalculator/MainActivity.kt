package com.example.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {
    private var displayText : TextView? = null
    var resultShown: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.displayText)
    }

    fun onDigit(myView: View){
        val btn = myView as Button
        val btnText = btn.text.toString()

        if (resultShown){
            onClear(myView)
        }

        appendText(btnText)
    }

    fun onOperator(myView: View){
        val btn = myView as Button
        val btnText = btn.text.toString()
        val startingOperators = listOf("+", "-")

        val displayTextAsString = displayText?.text.toString() ?: ""

        if (displayTextAsString.isEmpty() && btnText !in startingOperators){//only + and - can precede first number
            return
        }

        if (displayTextAsString.length == 1 && displayTextAsString in startingOperators){//only + and - can precede first number
            if (displayTextAsString.isOperator())
                return
        }


        if (isLastCharacterAnOperator(displayTextAsString)){
            displayText?.text = displayText?.text?.dropLast(1) //consecutive operators are not allowed
        }

        appendText(btnText)
    }

    private fun appendText(myText: String){
        if (resultShown){
            resultShown = false
        }
        displayText?.append(myText)
    }

    fun onDecimalPoint(myView: View){
        var dt = displayText?.text?.toString() ?: ""

        if(isDecimalPointExistsInNumber(dt))
            return

        if (dt.isEmpty() || "${dt.last()}".isOperator()){
            appendText("0")
        }
        appendText(".")
    }

    fun onClear(myView: View){
        displayText?.text = ""
    }

    fun onEquals(myView: View){
        if ("${displayText?.text?.last()}".isNumeric()){
            val argumentString = "${displayText?.text}"
            var result: String? = null

            try{
                result = findResult(argumentString)
            }catch(e: ArithmeticException){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                return
            }

            displayText?.text = normalizeResult(result!!)
            resultShown = true
        }
        else{
            Toast.makeText(this, "Operation should end with a number", Toast.LENGTH_SHORT).show()
        }
    }

    fun onDelete(myView: View){
        displayText?.text = displayText?.text?.dropLast(1)
    }
}


package com.example.simplecalculator

import android.widget.Toast
import java.lang.ArithmeticException

fun String.isOperator(): Boolean{
    val operatorList = listOf("*", "/", "+", "-")
    return this in operatorList
}

fun String.isNotOperator(): Boolean{
    return !this.isOperator()
}

fun String.isNumeric(): Boolean{
    val numberPattern = "(\\+|-)?\\d+(\\.\\d+)?".toRegex()
    return this.matches((numberPattern))
}

fun String.isNotNumeric(): Boolean{
    return !this.isNumeric()
}

fun isLastCharacterAnOperator(someString: String) :Boolean{
    if (someString.isNotEmpty()){
        val lastCharacter = someString.last().toString()
        return lastCharacter.isOperator()
    }
    return false
}

fun findResult(_argumentString: String): String{
    var argumentString = _argumentString

    while(true){
        val argumentList = convertToArrayList(argumentString)
        if (argumentList.size == 1)
            break

        val expressionList = findExpressionList(argumentList)
        val expressionResult = findExpressionResult(expressionList)

        argumentString = argumentList.joinToString(separator = "")
        val expressionListAsString = expressionList.joinToString(separator = "")
        val expressionResultAsString = expressionResult.toString()

        argumentString = argumentString.replace(expressionListAsString, expressionResultAsString)
    }

    return argumentString
}

fun convertToArrayList(argumentString: String): ArrayList<String>{
    // the argumentString is a sequence of ..dodod.. where d is digit, o is operator
    val argumentList :ArrayList<String> = arrayListOf()
    var digitString = ""

    for ((index, _ch) in argumentString.withIndex()){
        val ch = _ch.toString()

        if (ch.isOperator() && index > 0){ //operators at the start of argument are regarded as part of the number
            if(digitString.isNotEmpty()){
                argumentList.add(digitString)
                digitString = ""
            }
            argumentList.add(ch)
        }
        else
            digitString += ch
    }
    if (digitString.isNotEmpty())
        argumentList.add(digitString)

    return argumentList
}

fun findExpressionList(argumentList: ArrayList<String>): ArrayList<String> {
    val orderOfOperators = arrayListOf(listOf("*", "/"), listOf("+", "-"))

    for (operatorSet in orderOfOperators){
        for((argIndex, arg) in argumentList.withIndex()){
            if (arg in operatorSet && argIndex > 0){
                val leftOperand = argumentList[argIndex - 1]
                val rightOperand = argumentList[argIndex + 1]
                return arrayListOf(leftOperand, arg, rightOperand)
            }
        }
    }
    return arrayListOf()
}

fun findExpressionResult(expressionList: ArrayList<String>): Float{
    val (_leftOperand, operator, _rightOperand) = expressionList
    val leftOperand = _leftOperand.toFloat()
    val rightOperand = _rightOperand.toFloat()

    return when(operator){
        "*" -> leftOperand * rightOperand
        "/" -> {
            if (rightOperand == 0.0f){
                throw ArithmeticException("Can't Divide by zero")
            }
            leftOperand/rightOperand
        }
        "+" -> leftOperand + rightOperand
        "-" -> leftOperand - rightOperand
        else -> throw Exception("Expression contains wrong operator")
    }
}

fun normalizeResult(_result: String): String{
    // if the fractional part of result is 0, only the integer should be returned
    // else result should be rounded to 2 decimal places
    val decimalPoint = "."
    if (decimalPoint in _result){
        val (integer, fractionalPart) = _result.split(decimalPoint)

        if (fractionalPart.toFloat() == 0.0F){
            return integer
        }

        val result = _result.toFloat()
        return "%.2f".format(result)
    }
    return _result
}

fun isDecimalPointExistsInNumber(argumentString: String): Boolean{
    if (argumentString.isNotEmpty() && "${argumentString.last()}".isNotOperator() ){
        val decimalPoint = "."
        val reversedArgumentString = argumentString.reversed()
        var digitString = ""

        for (ch in reversedArgumentString){
            if ("$ch".isOperator()){
                break
            }
            digitString +=  ch
        }
        return decimalPoint in digitString
    }
    return false
}
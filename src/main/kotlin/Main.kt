package com.carcade.tests.examples

import com.carcade.tests.examples.Coffee.*
import com.carcade.tests.examples.CoffeeMachine.State.*
import java.util.*
import kotlin.system.exitProcess

const val START_MESSAGE = "Write action (buy, fill, take, remaining, exit):"

const val BUY_COFFEE_MESSAGE = "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:"

const val SHUTDOWN_MESSAGE = "Coffee machine turns off"

const val DEFAULT_RESOURCE = "water"

enum class Coffee(val water: Int, val milk: Int = 0, val beans: Int, val cost: Int) {

    ESPRESSO(water = 250, beans = 16, cost = 4),
    LATTE(water = 350, milk = 75, beans = 20, cost = 7),
    CAPPUCCINO(water = 200, milk = 100, beans = 12, cost = 6),
}

fun main() {

    val coffeeMachine = CoffeeMachine()

    val userInput = Scanner(System.`in`)

    while (userInput.hasNext()) {
        coffeeMachine.run { action(userInput.next()) }
    }
}

class CoffeeMachine(
    private var money: Int = 550,
    private var water: Int = 400,
    private var milk: Int = 540,
    private var beans: Int = 120,
    private var cups: Int = 9,
) {

    private var state: State = CHOOSE

    enum class State(var property: String) {
        CHOOSE(START_MESSAGE),
        BUYING(BUY_COFFEE_MESSAGE),
        FILLING(DEFAULT_RESOURCE),
    }

    init {
        printStateMessage()
    }

    fun action(action: String) {
        when (state) {
            CHOOSE -> selectAction(action)
            BUYING -> serve(action)
            FILLING -> acceptResources(action)
        }
    }

    private fun shutdown() {
        println(SHUTDOWN_MESSAGE)
        exitProcess(0)
    }


    private fun selectAction(action: String) {
        when (action) {
            "buy" -> {
                state = BUYING
                printStateMessage()
            }

            "fill" -> {
                state = FILLING
                println("Write how many ${state.property} you want to add:")
            }

            "remaining" -> printResources()
            "take" -> outputMoney()
            "exit" -> shutdown()
        }
    }

    private fun serve(action: String) {
        if (action == "back") {
            state = CHOOSE
            printStateMessage()
            return
        }
        state = BUYING

        when (action) {
            "1" -> make(ESPRESSO)
            "2" -> make(LATTE)
            "3" -> make(CAPPUCCINO)
        }
    }

    private fun make(coffee: Coffee) {
        if (isEnoughResources(coffee)) {
            water -= coffee.water
            milk -= coffee.milk
            beans -= coffee.beans
            money += coffee.cost
            cups--
        }
        state = CHOOSE
        printStateMessage()
        return
    }

    private fun acceptResources(resource: String) {

        if (state == FILLING) {

            when (state.property) {
                "water" -> {
                    water += resource.toInt(); state.property = "milk"
                }

                "milk" -> {
                    milk += resource.toInt(); state.property = "beans"
                }

                "beans" -> {
                    beans += resource.toInt(); state.property = "cups"
                }

                "cups" -> {
                    cups += resource.toInt(); state.property; state.property = "finish"
                }
            }

            println("Write how many ${state.property} you want to add:")

            if (state.property == "finish") {
                state.property = DEFAULT_RESOURCE
                state = CHOOSE
                printStateMessage()
                return
            }
        }
    }

    private fun outputMoney() {
        println("I gave you $$money")
        money = 0
        printStateMessage()
        return
    }

    private fun isEnoughResources(coffee: Coffee): Boolean {
        val resourceLack: String = when {
            coffee.water > water -> "water"
            coffee.milk > milk -> "milk"
            coffee.beans > beans -> "beans"
            cups == 0 -> "cups"
            else -> ""
        }
        if (resourceLack.isNotEmpty()) {
            println("Sorry, not enough $resourceLack!")
            return false
        } else {
            println("I have enough resources, making you a coffee!")
        }
        return true
    }


    private fun printResources() {
        println(
            """
            The coffee machine has:
            $water ml of water
            $milk ml of milk
            $beans g of coffee beans
            $cups disposable cups
            ${'$'}$money of money
        """.trimIndent()
        )
        printStateMessage()
        return
    }

    private fun printStateMessage() {
        println(state.property)
    }

}
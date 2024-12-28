package com.project.samay.domain.model

import androidx.compose.ui.graphics.Color


fun main(){
    println(Color(-5475746))
    println(Color(-3118236))
    println(Color(-509406))
    println(Color(-370884))
    println(Color(-35529))
    println(Color(-21178))
    println(Color(-12396910))
    println(Color(-15292571))
    println(Color(-8662712))
    println(Color(-4989844))
    println(Color(-2350809))
    println(Color(-3490369))
    println(Color(-3365204))

}

enum class CalendarColor(val key: Int, val color: Long) {
    KEY_1(1, -5475746),
    KEY_2(2, -3118236),
    KEY_3(3, -509406),
    KEY_4(4, -370884),
    KEY_5(5, -35529),
    KEY_6(6, -21178),
    KEY_7(7, -12396910),
    KEY_8(8, -15292571),
    KEY_9(9, -8662712),
    KEY_10(10, -4989844),
    KEY_11(11, -2350809),

    KEY_12(12, -339611),
    KEY_13(13, -7151168),
    KEY_14(14, -6299161),
    KEY_15(15, -6306073),
    KEY_16(16, -11958553),
    KEY_17(17, -6644481),
    KEY_18(18, -4613377),
    KEY_19(19, -4013374),


    KEY_20(20, -3490369),
    KEY_21(21, -3365204),

    KEY_22(22, -12396910),
    KEY_23(23, -618062),
    KEY_24(24, -5997854);


    companion object {
        fun getRandomColor(): CalendarColor {
            return entries.toTypedArray().random()
        }
        fun fromKey(key: Int): CalendarColor? {
            return entries.find { it.key == key }
        }

        fun fromColor(color: Long): CalendarColor? {
            return entries.find { it.color == color }
        }
    }
}

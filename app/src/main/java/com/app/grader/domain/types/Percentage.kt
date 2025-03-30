package com.app.grader.domain.types

data class Percentage(
    private var percentage: Double
) {
    init {
        require(percentage in 0.0..100.0) { "Percentage must be between 0 and 100" }
    }
    fun setPercentage(percentage:Double){
        if (percentage < 0.0) this.percentage = 0.0
        else if (percentage > 100.0) this.percentage = 100.0
        else this.percentage = percentage
    }
    fun setPercentage(percentage:Int){
        setPercentage(percentage.toDouble())
    }
    fun getPercentage(): Double {
        return percentage
    }

    override fun toString(): String {
        return percentage.toString()
    }

    companion object {
        fun check(percentage: Double): Boolean {
            return percentage in 0.0..100.0
        }
        fun check(percentage: Int): Boolean {
            return percentage in 0..100
        }
    }
}
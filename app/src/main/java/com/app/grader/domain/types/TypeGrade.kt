package com.app.grader.domain.types

enum class TypeGrade() {
    NUMERIC_20,        // Grade from 0 to 20 (Peru, Venezuela)
    NUMERIC_10,        // Grade from 0 to 10 (Mexico)
    NUMERIC_100,       // Grade from 0 to 100 (some countries)
    //LETTER_A_E,        // Letters: A, B, C, D, E (Ecuador, Panama)
    //LETTER_A_F,        // Letters: A, B, C, D, E, F (Colombia, international)
    //CONCEPTUAL,        // Concepts: Excellent, Good, Regular, Insufficient, etc.
    //PASS_FAIL,         // Only Pass/Fail
}
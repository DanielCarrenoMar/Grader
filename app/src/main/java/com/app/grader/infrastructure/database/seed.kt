package com.app.grader.infrastructure.database

import androidx.sqlite.db.SupportSQLiteDatabase

fun seedTypeGrade(db: SupportSQLiteDatabase) {
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, title, max, min_to_pass, is_from_system, is_direct_percentage, active) VALUES (1, 'Base 7 (0-7)', 7, 4.0, 1, 0, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, title, max, min_to_pass, is_from_system, is_direct_percentage, active) VALUES (2, 'Base 10 (0-10)', 10, 4.0, 1, 0, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, title, max, min_to_pass, is_from_system, is_direct_percentage, active) VALUES (3, 'Base 20 (0-20)', 20, 9.5, 1, 0, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, title, max, min_to_pass, is_from_system, is_direct_percentage, active) VALUES (4, 'Base 100 (0-100)', 100, 50.0, 1, 0, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, title, max, min_to_pass, is_from_system, is_direct_percentage, active) VALUES (5, 'Porcentual (0-100%)', 100, 50.0, 1, 1, 1)")
}
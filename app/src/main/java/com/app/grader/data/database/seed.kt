package com.app.grader.data.database

import androidx.sqlite.db.SupportSQLiteDatabase

fun seedTypeGrade(db: SupportSQLiteDatabase) {
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, base_at, active) VALUES (1, 7, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, base_at, active) VALUES (2, 10, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, base_at, active) VALUES (3, 20, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, base_at, active) VALUES (4, 100, 1)")
    db.execSQL("INSERT OR IGNORE INTO type_grade (id, base_at, active) VALUES (5, NULL, 1)")
}
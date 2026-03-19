package com.app.grader.data.database.converters

import androidx.room.TypeConverter
import java.util.Date

@Suppress("unused")
class DateConverter {

	@TypeConverter
	fun fromTimestamp(value: Long?): Date? {
		return value?.let { Date(it) }
	}

	@TypeConverter
	fun dateToTimestamp(date: Date?): Long? {
        return date?.time
	}
}
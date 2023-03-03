package com.github.siela1915.bootcamp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BoredActivity::class], version = BoredDatabase.VERSION)
abstract class BoredDatabase : RoomDatabase() {
    abstract fun boredDao(): BoredDao

    companion object {
        const val VERSION = 1
        const val NAME = "bored.sqlite"
    }
}
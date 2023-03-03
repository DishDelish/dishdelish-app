package com.github.siela1915.bootcamp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface BoredDao {
    @get:Query("SELECT * FROM BoredActivity ORDER BY activity DESC")
    val allActivities: List<BoredActivity>

    @Insert(onConflict = REPLACE)
    fun insertAll(vararg activities: BoredActivity?)

    @Query("DELETE FROM BoredActivity")
    fun clear()
}
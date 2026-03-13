package com.example.oppurtunityscanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OpportunityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opportunity: OpportunityEntity)

    @Query("SELECT * FROM opportunities ORDER BY timestamp DESC")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunities WHERE id = :id")
    suspend fun getOpportunityById(id: Int): OpportunityEntity?
}

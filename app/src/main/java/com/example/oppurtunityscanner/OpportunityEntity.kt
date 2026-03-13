package com.example.oppurtunityscanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val company: String,
    val role: String,
    val deadline: String,
    val link: String,
    val sourceApp: String,
    val fullMessage: String = "",
    val riskLevel: String = "VERIFY",
    val riskReasons: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

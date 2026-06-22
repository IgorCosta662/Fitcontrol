package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val dateEpoch: Long,
    val description: String,
    val accountType: String = "Conta Corrente"
)

enum class TransactionType {
    INCOME, EXPENSE
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Candidate
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.data.AppDatabase
import com.example.data.Transaction
import com.example.data.TransactionRepository
import com.example.data.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(db.transactionDao())
    }

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = repository.totalIncome
        .combine(MutableStateFlow(0.0)) { dbVal, _ -> dbVal ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = repository.totalExpense
        .combine(MutableStateFlow(0.0)) { dbVal, _ -> dbVal ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = combine(totalIncome, totalExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight
    
    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi

    fun addTransaction(amount: Double, type: TransactionType, category: String, description: String) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                dateEpoch = System.currentTimeMillis(),
                description = description
            )
            repository.insert(transaction)
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun analyzeFinances() {
        viewModelScope.launch {
            _isLoadingAi.value = true
            try {
                val currentTx = transactions.value
                if (currentTx.isEmpty()) {
                    _aiInsight.value = "Adicione mais transações para que eu possa gerar análises precisas e dicas de economia."
                    return@launch
                }
                
                val promptText = StringBuilder().apply {
                    append("Aqui estão as minhas transações financeiras recentes:\n")
                    currentTx.take(10).forEach { tx ->
                        val date = java.util.Date(tx.dateEpoch).toString()
                        append("- ${if (tx.type == TransactionType.INCOME) "Receita" else "Despesa"}: R$ ${tx.amount} (${tx.category}) em $date. ${tx.description}\n")
                    }
                    append("\nAnalise esses gastos em português e me dê uma sugestão de economia ou insight financeiro curto e amigável (no máximo 3 parágrafos).")
                }.toString()

                val response = withContext(Dispatchers.IO) {
                    val request = GenerateContentRequest(
                        contents = listOf(
                            Content(
                                parts = listOf(Part(text = promptText))
                            )
                        ),
                        systemInstruction = Content(
                            parts = listOf(Part(text = "Você é um consultor financeiro amigável e especialista chamado FinControl AI. Analise e responda sempre em português do Brasil."))
                        )
                    )
                    RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                }

                _aiInsight.value = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Não foi possível gerar um insight corporativo no momento."

            } catch (e: Exception) {
                _aiInsight.value = "Erro ao conectar-se à inteligência artificial: ${e.message}"
            } finally {
                _isLoadingAi.value = false
            }
        }
    }
}

class FinanceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

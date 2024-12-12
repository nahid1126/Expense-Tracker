package com.nahid.expensetracker.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.R
import com.nahid.expensetracker.model.data.Expanse
import com.nahid.expensetracker.model.local.db.LocalDatabase
import com.nahid.expensetracker.model.repository.ExpanseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ExpanseRepository) : ViewModel() {
    var expanseList: List<Expanse> = arrayListOf()

    init {
        viewModelScope.launch {
            getAllExpanse().collectLatest {
                if (it.isNotEmpty()) {
                    setExpanse(it)
                }
            }
        }
    }

    private fun setExpanse(expanseList: List<Expanse>) {
        this.expanseList = expanseList
    }

    private fun getAllExpanse() = repository.getAllExpanse()!!.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun getBalance(): String {
        var balance = 0.0
        expanseList.forEach {
            if (it.type == "Income") {
                balance += it.amount
            } else {
                balance -= it.amount
            }
        }
        return "৳ $balance"
    }

    fun getIncome(): String {
        var income = 0.0
        expanseList.forEach {
            if (it.type == "Income") {
                income += it.amount
            }
        }
        return "৳ $income"
    }

    fun getExpanse(): String {
        var expanse = 0.0
        expanseList.forEach {
            if (it.type == "Expanse") {
                expanse += it.amount
            }
        }
        return "৳ $expanse"
    }

    fun getIcon(expanse: Expanse): Int {
        return when (expanse.category) {
            "Salary" -> {
                R.drawable.ic_up
            }
            "Basa", "Kalamoni" -> {
                R.drawable.fixed
            }
            "Transport" -> {
                R.drawable.transport
            }
            else -> {
                R.drawable.others
            }
        }
    }

}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(ExpanseRepository(LocalDatabase.getDataBase(context))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
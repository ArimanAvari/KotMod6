package com.example.kotmod6.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotmod6.data.remote.NetworkModule
import com.example.kotmod6.data.repository.ApiNobelRepository
import com.example.kotmod6.domain.model.PrizeCategory
import com.example.kotmod6.domain.usecase.GetLaureateDetailUseCase
import com.example.kotmod6.domain.usecase.GetLaureatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NobelViewModel(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val getLaureateDetailUseCase: GetLaureateDetailUseCase
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _listState = MutableStateFlow<LaureateListState>(LaureateListState.Loading)
    val listState: StateFlow<LaureateListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<LaureateDetailState>(LaureateDetailState.Idle)
    val detailState: StateFlow<LaureateDetailState> = _detailState.asStateFlow()

    init {
        loadLaureates()
    }

    fun changeYear(value: String) {
        val filtered = value.filter { it.isDigit() }.take(4)
        _filterState.update { it.copy(year = filtered) }
    }

    fun changeCategory(category: PrizeCategory) {
        _filterState.update { it.copy(selectedCategory = category) }
    }

    fun loadLaureates() {
        val filters = _filterState.value
        viewModelScope.launch {
            _listState.value = LaureateListState.Loading
            _listState.value = try {
                val year = filters.year.takeIf { it.length == 4 }
                val category = filters.selectedCategory.apiCode.takeIf { it.isNotBlank() }
                val items = getLaureatesUseCase(year = year, categoryCode = category)
                LaureateListState.Success(items)
            } catch (error: Exception) {
                LaureateListState.Error(error.messageForUser())
            }
        }
    }

    fun loadLaureateDetail(id: String, year: String, categoryCode: String) {
        viewModelScope.launch {
            _detailState.value = LaureateDetailState.Loading
            _detailState.value = try {
                LaureateDetailState.Success(
                    getLaureateDetailUseCase(id = id, year = year, categoryCode = categoryCode)
                )
            } catch (error: Exception) {
                LaureateDetailState.Error(error.messageForUser())
            }
        }
    }

    private fun Throwable.messageForUser(): String {
        return localizedMessage?.takeIf { it.isNotBlank() } ?: "Не получилось загрузить данные"
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            val repository = ApiNobelRepository(NetworkModule.nobelApi)
            val getLaureatesUseCase = GetLaureatesUseCase(repository)
            val getLaureateDetailUseCase = GetLaureateDetailUseCase(repository)

            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NobelViewModel(
                        getLaureatesUseCase = getLaureatesUseCase,
                        getLaureateDetailUseCase = getLaureateDetailUseCase
                    ) as T
                }
            }
        }
    }
}

package com.likeminds.chatmm.di.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
class ReportViewModelFactory @Inject constructor(
    private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = viewModelProviders[modelClass]
            ?: viewModelProviders.entries.first { modelClass.isAssignableFrom(it.key) }.value

        return provider.get() as T
    }
}
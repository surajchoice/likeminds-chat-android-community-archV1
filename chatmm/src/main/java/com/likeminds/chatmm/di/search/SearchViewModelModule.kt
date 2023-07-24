package com.likeminds.chatmm.di.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.search.viewmodel.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchViewModelModule {

    @Binds
    abstract fun searchViewModelFactory(factory: SearchViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @SearchViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel
}
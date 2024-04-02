package com.likeminds.chatmm.di.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.chat.viewmodel.ChatViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatViewModelModule {

    @Binds
    abstract fun bindChatViewModelFactory(factory: ChatViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ChatViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(chatViewModel: ChatViewModel): ViewModel
}
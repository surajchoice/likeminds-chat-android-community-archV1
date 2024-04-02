package com.likeminds.chatmm.di.media

import com.likeminds.chatmm.media.view.*
import dagger.Subcomponent

@Subcomponent(modules = [MediaViewModelModule::class])
interface MediaComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MediaComponent
    }

    fun inject(conversationAudioSendEditFragment: ConversationAudioSendEditFragment)
    fun inject(conversationDocumentSendFragment: ConversationDocumentSendFragment)
    fun inject(conversationGifSendFragment: ConversationGifSendFragment)
    fun inject(conversationMediaEditFragment: ConversationMediaEditFragment)
    fun inject(mediaHorizontalListFragment: MediaHorizontalListFragment)
    fun inject(mediaPickerAudioFragment: MediaPickerAudioFragment)
    fun inject(mediaPickerDocumentFragment: LMChatMediaPickerDocumentFragment)
    fun inject(mediaPickerFolderFragment: LMChatMediaPickerFolderFragment)
    fun inject(mediaPickerItemFragment: LMChatMediaPickerItemFragment)
    fun inject(mediaVerticalListFragment: MediaVerticalListFragment)
    fun inject(playVideoFragment: PlayVideoFragment)
    fun inject(mediaPickerActivity: LMChatMediaPickerActivity)
    fun inject(mediaActivity: MediaActivity)
}
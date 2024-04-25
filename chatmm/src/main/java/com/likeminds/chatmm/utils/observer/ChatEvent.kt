package com.likeminds.chatmm.utils.observer

class ChatEvent {
    companion object {
        private var chatEvent: ChatEvent? = null

        @JvmStatic
        fun getPublisher(): ChatEvent {
            if (chatEvent == null) {
                chatEvent = ChatEvent()
            }
            return chatEvent!!
        }
    }

    // maintains the set of all the observers
    private var observers = hashSetOf<ChatObserver>()

    // subscribes the observer to listen to the changes
    fun subscribe(chatObserver: ChatObserver) {
        observers.add(chatObserver)
    }

    // unsubscribes the observer
    fun unsubscribe(chatObserver: ChatObserver) {
        observers.remove(chatObserver)
    }

    // notifies all the observers with the new data
    fun notify(postData: Any) {
        for (listener in observers) {
            listener.update(postData)
        }
    }

    interface ChatObserver {
        /*
        * called whenever post changes are notified the observer
        * postData - Pair of postId and Post data
        * */
        fun update(postData: Any)
    }
}
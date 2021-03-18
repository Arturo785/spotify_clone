package com.example.spotifyclone.exoplayer

import com.example.spotifyclone.exoplayer.State.*

class FirebaseMusicSource {
    // a list of lambdas
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            // the state we receive
            if(value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) { // means that no other thread can access the list at the same time
                    //                               they all access at their own turn
                    field = value // sets a new value
                    onReadyListeners.forEach { listener ->
                        // sets if initialized or not with the value defined a lines ago
                        listener(state == STATE_INITIALIZED)
                    }
                }
            }
            // creating or initializing
            else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if(state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action // adds the action to be prepared
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}
package com.comuto.pocpusher

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.pusher.chatkit.*
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.util.Result
import elements.Subscription
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatManager: ChatManager
    private lateinit var userId: String
    private lateinit var room: String
    private lateinit var subscription: Subscription
    private lateinit var currentUser: CurrentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        getExtras()
        setChatManager()
        button2.setOnClickListener {
            currentUser.sendMessage(
                roomId = room,
                messageText = editText.text.toString(),
                callback = {
                    Log.e("message", "result: $it")
                }
            )
        }

        chatManager.connect(
            listeners = ChatListeners(
                onAddedToRoom = {
                    Log.e("Added to room", " : " + it.id)
                }
            ),
            callback = { result ->
                when (result) {
                    is com.pusher.util.Result.Success -> {
                        currentUser = result.value
                        Log.e("success", "user: $currentUser") // CurrentUser
                        if (intent.extras.getBoolean("create")) {
                            createRoom(room)
                        } else {
                            subscribeToRoom()
                        }
                    }

                    is com.pusher.util.Result.Failure -> {
                        // Failure
                        Log.e("failure", "")
                    }
                }
            }
        )
    }

    private fun subscribeToRoom() {
        currentUser.subscribeToRoom(
            roomId = room,
            listeners = RoomListeners(
                onMessage = {
                    Log.e("Message received", ": ${it.text}")
                    textView.text = textView.text.toString().plus("" + it.text)
                }
            ),
            messageLimit = 100,
            callback = {
            }
        )
    }

    private fun getExtras() {
        userId = intent.extras.getString("name")
        room = intent.extras.getString("room")
    }

    private fun createRoom(roomName: String) {
        currentUser.createRoom(
            roomName,
            isPrivate = false,
            callback = {
                when (it) {
                    is Result.Success -> {
                        Log.e("Roome created", ": ${it.value.name}")
                        currentUser.joinRoom(
                            roomId = room,
                            callback = { result ->
                                Log.e("Roome created", ": $result")
                                subscribeToRoom()
                            }
                        )
                    }
                }
            }
        )
    }

    private fun setChatManager() {
        chatManager = ChatManager(
            instanceLocator = "v1:us1:6da85fed-cac7-46dd-a65d-fd5ce7b04470",
            userId = userId,
            dependencies = AndroidChatkitDependencies(
                tokenProvider = ChatkitTokenProvider(
                    endpoint = "https://us1.pusherplatform.io/services/chatkit_token_provider/v1/6da85fed-cac7-46dd-a65d-fd5ce7b04470/token",
                    userId = userId
                )
            )
        )
    }

    override fun onStop() {
        super.onStop()
        subscription.unsubscribe()
        chatManager.close {}
    }
}

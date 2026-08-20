package kr.hahaha98757.killchain.server

import kotlinx.coroutines.delay
import kr.hahaha98757.killchain.common.LeavePacket
import kr.hahaha98757.killchain.common.PingPacket
import kotlin.time.Duration.Companion.seconds

object ClientObserver {
    val clientsPingStatus = mutableMapOf<String, Boolean>()

    suspend fun start() {
        while (true) {
            clients.forEach { (_, client) ->
                clientsPingStatus[client.name] = false
                client.send(PingPacket)
            }
            delay(10.seconds)
            clients.forEach { (name, client) ->
                if (clientsPingStatus[name] == false) {
                    println("$name 님이 10초 동안 응답하지 않아 연결을 끊었습니다.")
                    client.close()
                    sendAll(LeavePacket(name))
                    printAndSendAll(userListMsgPacket)
                }
            }
        }
    }

    fun onPong(name: String) {
        clientsPingStatus[name] = true
    }
}
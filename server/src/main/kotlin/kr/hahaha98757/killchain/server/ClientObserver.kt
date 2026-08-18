package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.common.LeavePacket
import kr.hahaha98757.killchain.common.PingPacket

object ClientObserver: Runnable {
    val clientsPingStatus = mutableMapOf<String, Boolean>()

    override fun run() {
        while (true) {
            clients.forEach { (_, client) ->
                clientsPingStatus[client.name] = false
                client.send(PingPacket)
            }
            Thread.sleep(10000)
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
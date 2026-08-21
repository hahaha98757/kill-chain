package kr.hahaha98757.killchain.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.hahaha98757.killchain.common.*

object InputHandler {
    suspend fun start() {
        while (true) {
            val command = withContext(Dispatchers.IO) { readln() }
            when (command.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("유저를 내보내는 중...")
                    clients.values.toList().forEach { it.close() }
                    exit()
                }
                "HELP" -> help()
                "KILL" -> {
                    sendAll(KillPacket("server"))
                    println("접속한 모든 유저에게 강제 종료 신호를 전달했습니다.")
                }
                "LIST" -> println(userListMsgPacket.message)
                "PORT" -> println("포트: $port")
                "TEST" -> {
                    sendAll(TestPacket("server"))
                    println("접속한 모든 유저에게 테스트 신호를 전달했습니다.")
                }
                else -> printErr("'$command'은(는) 명령어가 아닙니다.")
            }
        }
    }
}
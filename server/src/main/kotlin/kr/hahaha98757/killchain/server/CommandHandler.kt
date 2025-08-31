package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.common.cls
import kr.hahaha98757.killchain.common.exit
import kr.hahaha98757.killchain.common.help
import kr.hahaha98757.killchain.common.printErr

class CommandHandler: Runnable {
    override fun run() {
        while (true) {
            val command = readln()
            when (command.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("유저를 내보내는 중...")
                    clients.forEach { it.value.close() }
                    exit()
                }
                "HELP" -> help()
                "KILL" -> {
                    sendAll("signal;Kill:server", false)
                    println("접속한 모든 유저에게 강제 종료 신호를 전달했습니다.")
                }
                "LIST" -> println(getUserList())
                "PORT" -> println("포트: $port")
                "TEST" -> {
                    sendAll("signal;Test:server", false)
                    println("접속한 모든 유저에게 테스트 신호를 전달했습니다.")
                }
                else -> printErr("'$command'은(는) 명령어가 아닙니다.")
            }
        }
    }
}
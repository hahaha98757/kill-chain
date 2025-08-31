package kr.hahaha98757.killchain.client

import kr.hahaha98757.killchain.common.IClient
import kr.hahaha98757.killchain.common.beep
import kr.hahaha98757.killchain.common.cls
import kr.hahaha98757.killchain.common.exit
import kr.hahaha98757.killchain.common.help
import kr.hahaha98757.killchain.common.printErr

class CommandHandler(private val client: IClient): Runnable {
    override fun run() {
        while (true) {
            val command = readln()
            when (command.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("서버를 떠나는 중...")
                    client.close()
                    exit()
                }
                "HELP" -> help()
                "KILL" -> {
                    println("강제종료를 시도합니다.")
                    kill()
                    client.send("signal;Kill:${client.name}")
                    println("서버에 강제 종료 신호를 전달했습니다.")
                }
                "LIST" -> client.send("signal;List")
                "PORT" -> client.send("signal;Port")
                "TEST" -> {
                    println("테스트를 시도합니다.")
                    beep()
                    client.send("signal;Test:${client.name}")
                    println("서버에 테스트 신호를 전달했습니다.")
                }
                else -> printErr("'$command'은(는) 명령어가 아닙니다.")
            }
        }
    }
}
package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.hahaha98757.killchain.common.*

object InputHandler {
    suspend fun start(connection: Connection) {
        while (true) {
            val command = withContext(Dispatchers.IO) { readln() }
            when (command.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("서버를 떠나는 중...")
                    connection.close()
                    GlobalScreen.unregisterNativeHook()
                    exit()
                }
                "HELP" -> help()
                "KILL" -> {
                    println("강제종료를 시도합니다.")
                    kill()
                    if (!connection.running) {
                        printErr("서버와의 연결이 끊어져 있습니다.")
                        continue
                    }
                    connection.send(KillPacket(connection.name))
                    println("서버에 강제 종료 신호를 전달했습니다.")
                }
                "LIST" -> {
                    if (!connection.running) {
                        printErr("서버와의 연결이 끊어져 있습니다.")
                        continue
                    }
                    connection.send(ListPacket)
                }
                "PORT" -> {
                    if (!connection.running) {
                        printErr("서버와의 연결이 끊어져 있습니다.")
                        continue
                    }
                    connection.send(PortPacket)
                }
                "TEST" -> {
                    println("테스트를 시도합니다.")
                    beep(1000.0)
                    if (!connection.running) {
                        printErr("서버와의 연결이 끊어져 있습니다.")
                        continue
                    }
                    connection.send(TestPacket(connection.name))
                    println("서버에 테스트 신호를 전달했습니다.")
                }
                else -> printErr("'$command'은(는) 명령어가 아닙니다.")
            }
        }
    }
}
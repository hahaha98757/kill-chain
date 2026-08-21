package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class Connection(name: String, socket: Socket, input: BufferedReader, output: PrintWriter): AbstractConnection(name, socket, input, output) {
    override fun onException(throwable: Throwable) {
        printErr("서버와 연결이 끊겼습니다.", throwable)
        beep(500.0, 1000, 1.0)
    }

    override fun processSignal(packet: SignalPacket) {
        when (packet) {
            is TestPacket -> {
                println("${packet.sender} 님으로부터 테스트 신호를 받았습니다.")
                beep(1000.0)
            }
            is KillPacket -> {
                println("${packet.sender} 님으로부터 강제 종료 신호를 받았습니다.")
                kill()
            }
            is PingPacket -> send(PongPacket)
            is PongPacket -> ServerObserver.onPong()
            is LeavePacket -> {
                println("${packet.client} 님의 연결이 끊겼습니다.")
                beep(500.0, 1000, 1.0)
            }
            is ClosePacket -> {
                println("서버가 연결을 끊었습니다.")
                close()
                GlobalScreen.unregisterNativeHook()
                exit()
            }
            else -> {
                printErr("잘못된 패킷을 받았습니다. (패킷: $packet)")
            }
        }
    }
}
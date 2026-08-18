package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class Client(name: String, socket: Socket, input: BufferedReader, output: PrintWriter): AbstractClient(name, socket, input, output) {
    init {
        send(NamePacket(name))
        when (val packet = PacketCodec.decode(input.readLine())) {
            is DuplicatePacket -> {
                printErr("'$name'은(는) 중복된 이름입니다.")
                close()
                exit(1)
            }
            is AcceptPacket -> {
                println("서버에 접속했습니다.")
                println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")
                println("'F2'를 눌러 테스트를 할 수 있습니다. 'ESC + F1'을 눌러 강제 종료를 할 수 있습니다.")
                start()
            }
            else -> {
                printErr("잘못된 패킷을 받았습니다. (패킷: $packet)")
                close()
                exit(-1)
            }
        }
    }

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
                println("${packet.sender} 님으로 부터 강제 종료 신호를 받았습니다.")
                kill()
            }
            is PingPacket -> send(PongPacket)
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
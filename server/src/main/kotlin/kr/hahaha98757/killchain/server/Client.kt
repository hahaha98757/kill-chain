package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class Client(name: String, socket: Socket, input: BufferedReader, output: PrintWriter): AbstractConnection(name, socket, input, output) {
    init {
        sendAll(MessagePacket("$name 님이 접속했습니다."))
        printAndSendAll(userListMsgPacket)
    }

    override fun onException(throwable: Throwable) {
        printErr("$name 님의 연결이 끊겼습니다.", throwable)
        sendAll(LeavePacket(name))
        sendAll(userListMsgPacket)
    }

    override fun processSignal(packet: SignalPacket) {
        when (packet) {
            is ListPacket -> printAndSendAll(userListMsgPacket)
            is PortPacket -> send(MessagePacket("포트: $port"))
            is TestPacket -> {
                println("${packet.sender} 님이 테스트를 시도했습니다.")
                sendAll(packet, packet.sender)
                println("접속한 모든 유저에게 신호를 전달했습니다.")
            }
            is KillPacket -> {
                println("${packet.sender} 님이 강제 종료를 시도했습니다.")
                sendAll(packet, packet.sender)
                println("접속한 모든 유저에게 강제 종료 신호를 전달했습니다.")
            }
            is PongPacket -> ClientObserver.onPong(name)
            is ClosePacket -> {
                sendAll(MessagePacket("$name 님이 서버를 떠났습니다."))
                close()
                printAndSendAll(userListMsgPacket)
            }
            else -> printErr("$name 님으로부터 잘못된 패킷을 받았습니다. (패킷: $packet)")
        }
    }

    override fun close() {
        runCatching { super.close() }.onFailure {
            printErr("$name 님이 서버를 완전히 떠나는데 실패했습니다. (서버 동작에는 영향이 없지만, 잠재적인 문제가 발생할 수 있습니다.)", it)
        }
        if (clients[name] == this) clients -= name
    }
}
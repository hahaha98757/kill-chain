package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class Client(name: String, socket: Socket, input: BufferedReader, output: PrintWriter): AbstractClient(name, socket, input, output) {
    init {
        if (clients.containsKey(name)) {
            send("signal;Name:Duplicate")
            close()
            throw NameDuplicateException(name)
        } else {
            send("signal;Name:Accept")
            clients[name] = this
            sendAll("$name 님이 접속했습니다.")
            sendAll(getUserList())
            start()
        }
    }

    override fun onException(e: Throwable) {
        printErr("$name 님의 연결이 끊겼습니다.", e)
    }

    override fun processSignal(signal: Array<String>) {
        when (signal[0]) {
            "Test" -> {
                println("$name 님이 테스트를 시도했습니다.")
                sendAll("signal;Test:$name", false, name)
                println("접속한 모든 유저에게 신호를 전달했습니다.")
            }
            "Kill" -> {
                println("$name 님이 강제 종료를 시도했습니다.")
                sendAll("signal;Kill:$name", false, name)
                println("접속한 모든 유저에게 강제 종료 신호를 전달했습니다.")
            }
            "List" -> send(getUserList())
            "Close" -> {
                sendAll("$name 님이 서버를 떠났습니다.")
                close()
                sendAll(getUserList())
            }
            "Port" -> send("포트: $port")
        }
    }

    override fun close() {
        runCatching { super.close() }.onFailure {
            printErr("$name 님이 서버를 완전히 떠나는데 실패했습니다. (서버 동작에는 영향이 없지만, 잠재적인 문제가 발생할 수 있습니다.)", it)
        }
        if (clients[name] == this) clients.remove(name)
    }
}
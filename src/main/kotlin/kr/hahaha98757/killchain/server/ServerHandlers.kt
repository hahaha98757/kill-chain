package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.utils.*
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import kotlin.system.exitProcess

class ClientHandler(private val name: String, private val socket: Socket, private val input: BufferedReader, val output: PrintWriter): AutoCloseable, Runnable {
    private var stop = false

    init {
        if (clients.containsKey(name)) {
            output.println("signal;NameDuplicate")
            close()
            throw NameDuplicateException(name)
        } else {
            clients[name] = this
            Thread(this).start()
        }
    }

    override fun run() {
        try {
            sendAll("$name 님이 접속했습니다.")
            sendAll(getUserList())
            output.println("\'HELP\'를 입력해 명령어 목록을 볼 수 있습니다.")
            output.println("\'F2\'를 눌러 테스트를 할 수 있습니다. \'ESC + F1\'을 눌러 강제 종료를 할 수 있습니다.")

            var var1: String?
            while (input.readLine().also { var1 = it } != null) {
                val message = var1!!
                if (message.startsWith("signal;")) {
                    val signal = message.split(";")[1]
                    processSignal(signal.split(":").toTypedArray())
                }
            }
        } catch (e: Exception) {
            if (!stop) printErr("$name 님의 연결이 끊겼습니다.", e)
            sendAll("$name 님이 서버를 떠났습니다.", false)
        } finally {
            close()
        }
    }

    private fun processSignal(signal: Array<String>) {
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
            "List" -> output.println(getUserList())
            "Leave" -> {
                sendAll("$name 님이 서버를 떠났습니다.")
                close()
                sendAll(getUserList())
            }
            "Port" -> output.println("포트: $port")
        }
    }

    override fun close() {
        if (stop) return
        output.println("signal;Close")
        stop = true
        var isThrow = false
        if (clients[name] == this) clients.remove(name)
        output.close()
        try {
            input.close()
        } catch (e: IOException) {
            isThrow = true
            e.printStackTrace()
        }

        try {
            socket.close()
        } catch (e: IOException) {
            isThrow = true
            e.printStackTrace()
        }
        if (isThrow) System.err.println("$name 님이 서버를 완전히 떠나는데 실패했습니다. (서버 동작에는 영향이 없지만, 잠재적인 문제가 발생할 수 있습니다.)")
    }
}

class ServerHandler: Runnable {
    override fun run() {
        while (true) {
            val command = readlnOrNull() ?: continue
            when (command.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("유저를 내보내는 중...")
                    clients.forEach { it.value.close() }
                    println("서버를 종료합니다.")
                    exitProcess(0)
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
                else -> System.err.println("\'$command\'은(는) 명령어가 아닙니다.")
            }
        }
    }
}
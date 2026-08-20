package kr.hahaha98757.killchain.server

import kotlinx.coroutines.*
import kr.hahaha98757.killchain.common.*
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

val clients = ConcurrentHashMap<String, Client>()
var port = 0
    private set

val userListMsgPacket get() = MessagePacket("현재 유저 목록: ${clients.keys().toList()}")

fun main() = runBlocking {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (server) v2.0.0")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    delay(1.seconds)

    while (true) {
        println("포트를 설정하세요. (1-65535 사이의 정수)")
        port = readln().toIntOrNull() ?: continue
        if (port in 1..65535) break
    }

    println()
    val serverSocket = runCatching { ServerSocket(port) }.getOrElse {
        printErr("서버를 여는데 실패했습니다.", it)
        exit(-1)
    }

    cls()

    println("서버를 열었습니다. (포트: $port)")
    println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")

    launch { InputHandler.start() }
    launch { ClientObserver.start() }

    while (true) try {
        val socket = serverSocket.accept()
        launch {
            try {
                val input = socket.getInputStream().bufferedReader()
                val output = PrintWriter(socket.getOutputStream().writer(), true)

                var name: String
                while (true) {
                    name = withContext(Dispatchers.IO) { input.readLine() }
                    if (name in clients.keys) {
                        output.println(false)
                        continue
                    } else output.println(true)
                    break
                }

                Client(name, socket, input, output).also { clients[name] = it }.start()
            } catch (e: Exception) {
                printErr("클라이언트와의 연결 중 오류가 발생했습니다.", e)
            }
        }
    } catch (e: Exception) {
        printErr("서버에서 오류가 발생했습니다.", e)
    }
}

fun sendAll(packet: Packet, vararg exclude: String) {
    clients.forEach { (name, client) -> if (name !in exclude) client.send(packet) }
}

fun printAndSendAll(packet: MessagePacket, vararg exclude: String) {
    println(packet.message)
    sendAll(packet, *exclude)
}

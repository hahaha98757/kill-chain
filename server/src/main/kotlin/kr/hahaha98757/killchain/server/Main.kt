package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

val clients = ConcurrentHashMap<String, IClient>()
var port = 0
    private set

fun main() {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (server) v2.0.0")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    Thread.sleep(1000)

    do {
        println("포트를 설정하세요. (1-65535 사이의 정수)")
        port = readln().toIntOrNull() ?: 0
    } while (port !in 1..65535)

    println()
    val serverSocket = runCatching { ServerSocket(port) }.getOrElse {
        printErr("서버를 여는데 실패했습니다.", it)
        exit(-1)
    }
    port = serverSocket.localPort

    cls()

    println("서버를 열었습니다. (포트: $port)")
    println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")

    Thread(CommandHandler).start()
    Thread(ClientObserver).start()

    while (true) try {
        val socket = serverSocket.accept()
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
        val name = input.readLine().split(";")[1].split(":")[2]

        Client(name, socket, input, output)
    } catch (e: NameDuplicateException) {
        printErr("유저가 서버 접속에 실패했습니다. (중복된 이름: ${e.message})")
    } catch (e: Exception) {
        printErr("유저가 서버 접속에 실패했습니다.", e)
    }
}

fun sendAll(packet: Packet, isPrint: Boolean = true, without: String? = null) {
    if (isPrint && packet is MessagePacket) println(packet.message)
    clients.forEach { (name, client) -> if (name != without) client.send(packet) }
}

fun getUserList() = "현재 유저 목록: ${clients.keys().toList()}"
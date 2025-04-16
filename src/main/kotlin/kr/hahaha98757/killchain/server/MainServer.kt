package kr.hahaha98757.killchain.server

import kr.hahaha98757.killchain.utils.NameDuplicateException
import kr.hahaha98757.killchain.utils.printErr
import java.io.*
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.exitProcess

var clients = ConcurrentHashMap<String, ClientHandler>()
var port = 0

fun main() {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (server) v1.0.1")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    Thread.sleep(1000)
    while (true) try {
        println("포트를 설정하세요.(0-65535 사이의 정수, 0은 임의의 포트로 설정.)")
        port = readlnOrNull()!!.toInt()
        if (port in 0..65535) break
    } catch (_: Exception) {}

    println()
    val serverSocket = try {
        ServerSocket(port)
    } catch (e: Exception) {
        printErr("서버를 여는데 실패했습니다.", e)
        exitProcess(1)
    }
    port = serverSocket.localPort
    println("서버를 열었습니다.(포트: ${serverSocket.localPort})")
    println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")

    Thread(ServerHandler()).start()

    while (true) try {
        val socket = serverSocket.accept()
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
        val name = input.readLine()

        ClientHandler(name, socket, input, output)
    } catch (e: NameDuplicateException) {
        System.err.println("유저가 서버 접속에 실패했습니다. (중복된 이름: ${e.message})")
    } catch (e: Exception) {
        printErr("유저가 서버 접속에 실패했습니다.", e)
    }
}

fun sendAll(message: String, isPrint: Boolean = true, without: String? = null) {
    if (isPrint) println(message)
    clients.forEach { (name, client) -> if (name != without) client.output.println(message) }
}

fun getUserList() = "현재 유저 목록: ${clients.keys().toList()}"
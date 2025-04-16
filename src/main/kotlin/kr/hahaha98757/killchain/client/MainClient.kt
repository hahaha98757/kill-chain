package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.utils.*
import java.io.*
import java.net.Socket
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

fun main() {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (client) v1.0.1")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    Thread.sleep(1000)
    var name: String
    var host = "127.0.0.1"
    var port = 0
    input@do {
        while (true) try {
            println("닉네임을 입력하세요. (중복 불가, 'server' 사용 불가, 'client'를 입력하여 싱글모드 사용.)")
            name = readlnOrNull()!!
            if (name.isNotEmpty() && name != "server") if (name == "client") break@input
            else break
        } catch (_: Exception) {}

        println()
        while (true) try {
            println("호스트의 IP를 입력하세요.")
            host = readlnOrNull()!!
            if (host.isNotEmpty()) break
        } catch (_: Exception) {}

        println()
        while (true) try {
            println("포트를 입력하세요. (1-65535 사이의 정수.)")
            port = readlnOrNull()!!.toInt()
            if (port in 1..65535) break
        } catch (_: Exception) {}
    } while (false)

    cls()

    if (name == "client") singleMode()
    else try {
        println("서버 접속 중...")
        val socket = Socket(host, port)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
        output.println(name)
        Thread(ReceptionHandler(name, socket, input, output)).start()
        Thread {
            LogManager.getLogManager().reset()
            Logger.getLogger(GlobalScreen::class.java.packageName).level = Level.OFF

            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(TransmissionHandler(name, socket, input, output))
        }.start()
        Thread(CommandHandler(name, socket, input, output)).start()
    } catch (e: Exception) {
        printErr("서버 접속에 실패했습니다.", e)
    }
}
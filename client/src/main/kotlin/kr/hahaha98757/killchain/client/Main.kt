package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.common.*
import java.io.*
import java.net.Socket
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

fun main() {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (client) v1.1.2")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    Thread.sleep(1000)

    var name: String
    do {
        println("닉네임을 입력하세요. (중복 불가, 'server' 사용 불가, 'client'를 입력하여 싱글모드 사용.)")
        name = readln()
    } while (name.isEmpty() || name == "server")
    if (name == "client") {
        cls()
        singleMode()
        return
    }

    println()
    println("호스트의 IP를 입력하세요. (빈칸일 경우 루프백 IP로 접속합니다.)")
    val str = readln()
    val host = str.ifEmpty { "127.0.0.1" }


    println()
    var port: Int
    do {
        println("포트를 입력하세요. (1-65535 사이의 정수.)")
        port = readln().toIntOrNull() ?: 0
    } while (port !in 1..65535)

    cls()

    println("서버 접속 중...")

    runCatching {
        val socket = Socket(host, port)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)

        val client = Client(name, socket, input, output)

        Thread {
            LogManager.getLogManager().reset()
            Logger.getLogger(GlobalScreen::class.java.packageName).level = Level.OFF

            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(object: AbstractKeyInputListener() {
                override fun doKill() {
                    println("강제종료를 시도합니다.")
                    kill()
                    client.send("signal;Kill:$name")
                    println("서버에 강제 종료 신호를 전달했습니다.")
                }

                override fun doTest() {
                    println("테스트를 시도합니다.")
                    beep(1000.0)
                    client.send("signal;Test:$name")
                    println("서버에 테스트 신호를 전달했습니다.")
                }
            })
        }.start()

        Thread(CommandHandler(client)).start()
    }.onFailure {
        printErr("서버 접속에 실패했습니다.", it)
        GlobalScreen.unregisterNativeHook()
        exit(-1)
    }
}

fun kill() {
    Runtime.getRuntime().exec("CMD /C TASKKILL /F /IM GTA5_Enhanced.exe")
    Runtime.getRuntime().exec("CMD /C TASKKILL /F /IM GTA5.exe")
}
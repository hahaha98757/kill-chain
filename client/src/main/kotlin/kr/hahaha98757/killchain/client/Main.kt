package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.hahaha98757.killchain.common.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = runBlocking {
    println("Copyright (c) 2025 hahaha98757 (MIT License)")
    println("Kill Chain (client) v2.0.0")
    println("공식 사이트: https://github.com/hahaha98757/kill-chain")
    println()
    delay(1.seconds)

    var name: String
    while (true) {
        println("닉네임을 입력하세요. (중복 불가, 'server' 사용 불가, 'client'를 입력하여 싱글모드 사용.)")
        name = readln()
        if (!name.isEmpty() && name != "server") break
    }
    if (name == "client") {
        cls()
        singleMode()
        exit()
    }

    println()
    println("호스트의 IP를 입력하세요. (빈칸일 경우 루프백 IP로 접속합니다.)")
    val str = readln()
    val host = str.ifEmpty { "127.0.0.1" }


    println()
    var port: Int
    while (true) {
        println("포트를 입력하세요. (1-65535 사이의 정수.)")
        port = readln().toIntOrNull() ?: continue
        if (port in 1..65535) break
    }

    cls()

    println("서버 접속 중...")

    try {
        val socket = Socket(host, port)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)

        output.println(name)
        if (!input.readLine().toBoolean()) {
            printErr("중복된 닉네임입니다.")
            exit(-1)
        }

        val client = Connection(name, socket, input, output)

        launch { client.start() }
        launch { InputHandler.start(client) }

        registerKeyListener(
            killBlock = {
                println("강제종료를 시도합니다.")
                kill()
                client.send(KillPacket(name))
                println("서버에 강제 종료 신호를 전달했습니다.")
            },
            testBlock = {
                println("테스트를 시도합니다.")
                beep(1000.0)
                client.send(TestPacket(name))
                println("서버에 테스트 신호를 전달했습니다.")
            }
        )
    } catch (e: Exception) {
        printErr("서버 접속에 실패했습니다.", e)
        GlobalScreen.unregisterNativeHook()
        exit(-1)
    }
}

fun kill() {
    ProcessBuilder(
        "taskkill",
        "/F",
        "/IM", "GTA5.exe",
        "/IM", "GTA5_Enhanced.exe"
    ).start()
}

fun registerKeyListener(killBlock: () -> Unit, testBlock: () -> Unit) {
    LogManager.getLogManager().reset()
    Logger.getLogger(GlobalScreen::class.java.packageName).level = Level.OFF

    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(object: AbstractKeyInputListener() {
        override fun doKill() = killBlock()
        override fun doTest() = testBlock()
    })
}
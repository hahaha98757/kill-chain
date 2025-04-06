package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.utils.*
import java.io.*
import java.net.Socket
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.system.exitProcess

fun main() {
    var name: String
    var host = "127.0.0.1"
    var port = 0
    input@do {
        while (true) try {
            println("닉네임을 입력하세요. (중복 불가, \'server\' 사용 불가, \'client\'를 입력하여 싱글모드 사용.)")
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

    if (name == "client") {
        Thread {
            LogManager.getLogManager().reset()
            Logger.getLogger(GlobalScreen::class.java.packageName).level = Level.OFF

            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(object: INativeKeyListener {
                override val pressedKeys = mutableSetOf<Int>()
                override fun doKill() {
                    println("강제종료를 시도합니다.")
                    kill()
                }
                override fun doTest() {
                    println("테스트를 시도합니다.")
                    beep()
                }
            })
        }.start()
        println("싱글모드를 사용합니다.")
        println("\'HELP\'를 입력해 명령어 목록을 볼 수 있습니다.")
        println("\'F2\'를 눌러 테스트를 할 수 있습니다. \'ESC + F1\'을 눌러 강제 종료를 할 수 있습니다.")
        while (true) readlnOrNull().let {
            if (!it.isNullOrEmpty()) when (it.uppercase()) {
                "CLS" -> cls()
                "EXIT" -> {
                    println("클라이언트를 종료합니다.")
                    exitProcess(0)
                }
                "HELP" -> help()
                "KILL" -> {
                    println("강제종료를 시도합니다.")
                    kill()
                }
                "LIST", "PORT" -> System.err.println("싱글모드에서 사용할 수 없습니다.")
                "TEST" -> {
                    println("테스트를 시도합니다.")
                    beep()
                }
                else -> System.err.println("\'$it\'은(는) 명령어가 아닙니다.")
            }
        }
    } else try {
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
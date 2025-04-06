package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kr.hahaha98757.killchain.utils.*
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import kotlin.system.exitProcess

@Volatile
var stop = false

class ReceptionHandler(private val name: String, socket: Socket, private val input: BufferedReader, output: PrintWriter): ClientHandlerBase(socket, input, output), Runnable {
    override fun run() {
        try {
            var var1: String?
            while (input.readLine().also { var1 = it } != null) {
                val message = var1!!
                if (message.startsWith("signal;")) {
                    val signal = message.split(";")[1]
                    processSignal(signal.split(":").toTypedArray())
                } else println(message)
            }
        } catch (e: Exception) {
            if (!stop) printErr("서버의 연결이 끊겼습니다.", e)
        } finally {
            close()
            exitProcess(0)
        }
    }

    private fun processSignal(signal: Array<String>) {
        when (signal[0]) {
            "NameDuplicate" -> {
                System.err.println("\'$name\'은(는) 중복된 이름입니다.")
                close()
                exitProcess(2)
            }
            "Close" -> {
                println("서버가 연결을 끊었습니다.")
                close()
                exitProcess(0)
            }
            "Test" -> {
                println("${signal[1]} 님으로부터 테스트 신호를 받았습니다.")
                beep()
            }
            "Kill" -> {
                println("${signal[1]} 님으로 부터 강제 종료 신호를 받았습니다.")
                kill()
            }
        }
    }
}

class TransmissionHandler(private val name: String, socket: Socket, input: BufferedReader, private val output: PrintWriter): ClientHandlerBase(socket, input, output), INativeKeyListener {
    override val pressedKeys = mutableSetOf<Int>()

    override fun doKill() {
        println("강제종료를 시도합니다.")
        kill()
        output.println("signal;Kill:$name")
        println("서버에 강제 종료 신호를 전달했습니다.")
    }

    override fun doTest() {
        println("테스트를 시도합니다.")
        beep()
        output.println("signal;Test:$name")
        println("서버에 테스트 신호를 전달했습니다.")
    }
}

interface INativeKeyListener: NativeKeyListener {
    val pressedKeys: MutableSet<Int>
    fun doKill()
    fun doTest()
    override fun nativeKeyPressed(event: NativeKeyEvent) {
        if (pressedKeys.add(event.keyCode)) {
            if ((event.keyCode == NativeKeyEvent.VC_F1 && pressedKeys.contains(NativeKeyEvent.VC_ESCAPE)) || (event.keyCode == NativeKeyEvent.VC_ESCAPE && pressedKeys.contains(NativeKeyEvent.VC_F1)))
                doKill()
            if (event.keyCode == NativeKeyEvent.VC_F2) doTest()
        }
    }
    override fun nativeKeyReleased(event: NativeKeyEvent) {
        pressedKeys.remove(event.keyCode)
    }
}

class CommandHandler(private val name: String, socket: Socket, input: BufferedReader, private val output: PrintWriter): ClientHandlerBase(socket, input, output), Runnable {
    override fun run() {
        while (!stop) readlnOrNull().let { if (!it.isNullOrEmpty()) executeCommand(it) }
    }

    private fun executeCommand(command: String) = when (command.uppercase()) {
        "CLS" -> cls()
        "EXIT" -> {
            println("서버를 떠나는 중...")
            close()
            println("클라이언트를 종료합니다.")
            exitProcess(0)
        }
        "HELP" -> help()
        "KILL" -> {
            println("강제종료를 시도합니다.")
            kill()
            output.println("signal;Kill:$name")
            println("서버에 강제 종료 신호를 전달했습니다.")
        }
        "LIST" -> output.println("signal;List")
        "PORT" -> output.println("signal;Port")
        "TEST" -> {
            println("테스트를 시도합니다.")
            beep()
            output.println("signal;Test:$name")
            println("서버에 테스트 신호를 전달했습니다.")
        }
        else -> System.err.println("\'$command\'은(는) 명령어가 아닙니다.")
    }
}

open class ClientHandlerBase(private val socket: Socket, private val input: BufferedReader, private val output: PrintWriter): AutoCloseable {
    override fun close() {
        if (stop) return
        output.println("signal;Leave")
        stop = true
        var isThrow = false
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
        try {
            GlobalScreen.unregisterNativeHook()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isThrow) System.err.println("서버를 완전히 떠나는데 실패했습니다. (서버 동작에는 영향이 없지만, 잠재적인 문제가 발생할 수 있습니다.)")
    }
}

fun kill() {
    Runtime.getRuntime().exec("CMD /C TASKKILL /F /IM GTA5_Enhanced.exe")
    Runtime.getRuntime().exec("CMD /C TASKKILL /F /IM GTA5.exe")
}
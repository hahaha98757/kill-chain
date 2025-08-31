package kr.hahaha98757.killchain.client

import kr.hahaha98757.killchain.common.AbstractClient
import kr.hahaha98757.killchain.common.beep
import kr.hahaha98757.killchain.common.exit
import kr.hahaha98757.killchain.common.printErr
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

class Client(name: String, socket: Socket, input: BufferedReader, output: PrintWriter): AbstractClient(name, socket, input, output) {
    init {
        send("signal;Name:Send:$name")
        when (val message = input.readLine().split(";")[1].split(":")[1]) {
            "Duplicate" -> {
                printErr("'$name'은(는) 중복된 이름입니다.")
                close()
                exit(1)
            }
            "Accept" -> {
                println("서버에 접속했습니다.")
                println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")
                println("'F2'를 눌러 테스트를 할 수 있습니다. 'ESC + F1'을 눌러 강제 종료를 할 수 있습니다.")
                start()
            }
            else -> {
                printErr("알 수 없는 응답을 받았습니다. (응답: $message)")
                close()
                exit(-1)
            }
        }
    }

    override fun onException(e: Throwable) {
        printErr("서버와 연결이 끊겼습니다.", e)
        exit(-1)
    }

    override fun processSignal(signal: Array<String>) {
        when (signal[0]) {
            "Close" -> {
                println("서버가 연결을 끊었습니다.")
                close()
                exit()
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
package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.GlobalScreen
import kr.hahaha98757.killchain.common.beep
import kr.hahaha98757.killchain.common.cls
import kr.hahaha98757.killchain.common.exit
import kr.hahaha98757.killchain.common.help
import kr.hahaha98757.killchain.common.printErr
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

fun singleMode() {
    Thread {
        LogManager.getLogManager().reset()
        Logger.getLogger(GlobalScreen::class.java.packageName).level = Level.OFF

        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(object: AbstractKeyInputListener() {
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
    println("'HELP'를 입력해 명령어 목록을 볼 수 있습니다.")
    println("'F2'를 눌러 테스트를 할 수 있습니다. 'ESC + F1'을 눌러 강제 종료를 할 수 있습니다.")
    while (true) readln().takeUnless { it.isEmpty() }?.let {
        when (it.uppercase()) {
            "CLS" -> cls()
            "EXIT" -> exit()
            "HELP" -> help()
            "KILL" -> {
                println("강제종료를 시도합니다.")
                kill()
            }
            "LIST", "PORT" -> printErr("싱글모드에서 사용할 수 없습니다.")
            "TEST" -> {
                println("테스트를 시도합니다.")
                beep()
            }
            else -> printErr("'$it'은(는) 명령어가 아닙니다.")
        }
    }
}
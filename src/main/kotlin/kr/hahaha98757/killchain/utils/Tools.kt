package kr.hahaha98757.killchain.utils

import javax.sound.sampled.AudioSystem

fun cls() = repeat(50) { println() }

fun help() {
    println("""
        CLS        출력된 텍스트를 위로 올립니다.
        EXIT       프로그램을 종료합니다.
        HELP       사용 가능한 명령어 목록을 출력합니다.
        KILL       강제 종료 신호를 전달합니다.
        LIST       서버에 접속한 유저 목록을 출력합니다. (싱글모드에서 사용 불가.)
        PORT       서버의 포트를 확인합니다. (싱글모드에서 사용 불가.)
        TEST       테스트 신호를 전달합니다.
    """.trimIndent())
}

fun printErr(text: String, e: Throwable) {
    e.printStackTrace()
    System.err.println(text)
}

fun beep() = Thread {
    val audioInputStream = AudioSystem.getAudioInputStream(object {}::class.java.classLoader.getResource("beep.wav")!!)
    val clip = AudioSystem.getClip()

    clip.open(audioInputStream)
    clip.start()
    Thread.sleep(clip.microsecondLength / 1000)
    clip.close()
}.start()

class NameDuplicateException(message: String): RuntimeException(message)
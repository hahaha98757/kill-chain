package kr.hahaha98757.killchain.common

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.sin
import kotlin.system.exitProcess

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
fun printErr(text: String) = System.err.println(text)

fun exit(code: Int = 0): Nothing = if (code == 0) {
    println("1초 뒤, 프로그램이 종료됩니다.")
    Thread.sleep(1000)
    exitProcess(0)
} else {
    println("5초 뒤, 프로그램이 종료됩니다. (종료 코드: $code)")
    Thread.sleep(5000)
    exitProcess(code)
}

fun beep() = Thread {
    val frequency = 1000.0
    val durationMs = 200
    val volume = 0.5

    val sampleRate = 44100f
    val samples = (durationMs / 1000.0 * sampleRate).toInt()
    val buffer = ByteArray(samples)

    for (i in buffer.indices) {
        val angle = 2.0 * Math.PI * i * frequency / sampleRate
        buffer[i] = (sin(angle) * 127 * volume).toInt().toByte()
    }

    val format = AudioFormat(sampleRate, 8, 1, true, false) // 8bit, mono, signed, little endian
    val line = AudioSystem.getSourceDataLine(format)
    line.open(format)
    line.start()
    line.write(buffer, 0, buffer.size)
    line.drain()
    line.stop()
    line.close()
}.start()
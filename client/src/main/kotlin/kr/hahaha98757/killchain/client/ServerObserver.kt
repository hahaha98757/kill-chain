package kr.hahaha98757.killchain.client

import kotlinx.coroutines.delay
import kr.hahaha98757.killchain.common.PingPacket
import kr.hahaha98757.killchain.common.beep
import kotlin.time.Duration.Companion.seconds

object ServerObserver {
    private var status = false

    suspend fun start(connection: Connection) {
        while (true) {
            status = false
            connection.send(PingPacket)
            delay(10.seconds)
            if (!status) {
                println("서버가 10초 동안 응답하지 않아 연결을 끊었습니다.")
                connection.close()
                beep(500.0, 1000, 1.0)
                break
            }
        }
    }

    fun onPong() {
        status = true
    }
}
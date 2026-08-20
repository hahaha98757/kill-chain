package kr.hahaha98757.killchain.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

abstract class AbstractConnection(
    val name: String,
    private val socket: Socket,
    private val input: BufferedReader,
    private val output: PrintWriter
): AutoCloseable {
    @Volatile
    var running = true
        private set

    fun send(packet: Packet) {
        if (running) output.println(PacketCodec.encode(packet))
    }

    suspend fun start() {
        var throwable: Throwable? = null
        try {
            while (running) {
                val received = withContext(Dispatchers.IO) { input.readLine() } ?: break
                val packet = PacketCodec.decode(received) ?: run {
                    printErr("알 수 없는 데이터 수신: $received")
                    continue
                }
                when (packet) {
                    is MessagePacket -> println(packet.message)
                    is SignalPacket -> processSignal(packet)
                }
            }
        } catch (t: Throwable) {
            if (running) throwable = t
        } finally {
            runCatching { close() }.onFailure { if (throwable != null) throwable.addSuppressed(it) else throwable = it }
            throwable?.let { onException(it) }
        }
    }

    protected abstract fun onException(throwable: Throwable)
    protected abstract fun processSignal(packet: SignalPacket)

    override fun close() {
        if (!running) return
        send(ClosePacket)
        running = false
        socket.close()
    }

    override fun equals(other: Any?) = (other as? AbstractConnection)?.name == name
    override fun hashCode() = name.hashCode()
}
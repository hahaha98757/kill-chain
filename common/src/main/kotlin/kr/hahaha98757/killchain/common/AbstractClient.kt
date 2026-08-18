package kr.hahaha98757.killchain.common

import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

abstract class AbstractClient(override val name: String, private val socket: Socket, private val input: BufferedReader, private val output: PrintWriter): IClient {
    @Volatile
    private var running = true

    protected abstract fun processSignal(packet: SignalPacket)
    protected abstract fun onException(t: Throwable)

    override fun start() = Thread(this).start()

    override fun send(packet: Packet) {
        if (!running) return
        output.println(PacketCodec.encode(packet))
    }

    override fun run() {
        var throwable: Throwable? = null
        try {
            while (running) {
                val received = input.readLine() ?: break
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

    override fun close() {
        if (!running) return
        send(ClosePacket)
        running = false
        socket.close()
    }

    override fun isClosed() = !running
}
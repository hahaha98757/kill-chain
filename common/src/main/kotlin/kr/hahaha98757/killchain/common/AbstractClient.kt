package kr.hahaha98757.killchain.common

import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

abstract class AbstractClient(override val name: String, private val socket: Socket, private val input: BufferedReader, private val output: PrintWriter): IClient {
    @Volatile
    private var stop = false

    protected abstract fun processSignal(packet: SignalPacket)
    protected abstract fun onException(e: Throwable)

    override fun start() = Thread(this).start()

    override fun send(packet: Packet) = output.println(PacketCodec.encode(packet))

    override fun run() {
        var throwable: Throwable? = null
        try {
            while (!stop) {
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
        } catch (e: Throwable) {
            if (!stop) throwable = e
        } finally {
            runCatching { close() }.onFailure { if (throwable != null) throwable.addSuppressed(it) else throwable = it }
            throwable?.let { onException(it) }
        }
    }

    override fun close() {
        if (stop) return
        stop = true
        send(ClosePacket)
        var isThrown = false
        val e = IOException("Failed to close.")
        output.close()
        runCatching { input.close() }.onFailure {
            isThrown = true
            e.addSuppressed(it)
        }
        runCatching { socket.close() }.onFailure {
            isThrown = true
            e.addSuppressed(it)
        }
        if (isThrown) throw e
    }

    override fun isClosed() = stop
}
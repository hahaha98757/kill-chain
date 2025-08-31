package kr.hahaha98757.killchain.common

import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

abstract class AbstractClient(override val name: String, private val socket: Socket, private val input: BufferedReader, private val output: PrintWriter): IClient {
    @Volatile
    private var stop = false

    protected abstract fun processSignal(signal: Array<String>)
    protected abstract fun onException(e: Throwable)

    override fun start() = Thread(this).start()

    override fun send(message: String) = output.println(message)

    override fun run() {
        var throwable: Throwable? = null
        try {
            while (!stop) {
                val message = input.readLine() ?: break
                if (message.startsWith("signal;")) {
                    val signal = message.split(";")[1]
                    processSignal(signal.split(":").toTypedArray())
                } else println(message)
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
        send("signal;Close")
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
}
package kr.hahaha98757.killchain.common

interface IClient: AutoCloseable, Runnable {
    val name: String
    fun start()
    fun send(packet: Packet)
    fun isClosed(): Boolean
}
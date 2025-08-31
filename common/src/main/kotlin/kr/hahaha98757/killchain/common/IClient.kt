package kr.hahaha98757.killchain.common

interface IClient: AutoCloseable, Runnable {
    val name: String
    fun start()
    fun send(message: String)
}
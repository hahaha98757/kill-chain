package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener

abstract class AbstractKeyInputListener: NativeKeyListener {
    private val pressedKeys = mutableSetOf<Int>()

    abstract fun doKill()
    abstract fun doTest()

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        if (pressedKeys.add(event.keyCode)) {
            if ((event.keyCode == NativeKeyEvent.VC_F1 && NativeKeyEvent.VC_ESCAPE in pressedKeys) ||
                (event.keyCode == NativeKeyEvent.VC_ESCAPE && NativeKeyEvent.VC_F1 in pressedKeys))
                doKill()
            if (event.keyCode == NativeKeyEvent.VC_F2) doTest()
        }
    }
    override fun nativeKeyReleased(event: NativeKeyEvent) {
        pressedKeys -= event.keyCode
    }
}
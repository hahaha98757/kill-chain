package kr.hahaha98757.killchain.client

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener

abstract class AbstractKeyInputListener: NativeKeyListener {
    private val pressedKeys = mutableSetOf<Int>()

    abstract fun doKill()
    abstract fun doTest()

    override fun nativeKeyPressed(event: NativeKeyEvent?) {
        if (event == null) return
        if (pressedKeys.add(event.keyCode)) {
            if ((event.keyCode == NativeKeyEvent.VC_F1 && pressedKeys.contains(NativeKeyEvent.VC_ESCAPE))
                || (event.keyCode == NativeKeyEvent.VC_ESCAPE && pressedKeys.contains(NativeKeyEvent.VC_F1)))
                doKill()
            if (event.keyCode == NativeKeyEvent.VC_F2) doTest()
        }
    }
    override fun nativeKeyReleased(event: NativeKeyEvent?) {
        if (event == null) return
        pressedKeys.remove(event.keyCode)
    }
}
package com.glissando.whistleribbon.presentation.app

import androidx.lifecycle.ViewModel
import com.glissando.whistleribbon.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val history = ArrayDeque<Screen>()
    private val current = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = current.asStateFlow()

    fun navigate(target: Screen) {
        val from = current.value
        if (from != Screen.Splash) history.addLast(from)
        current.value = target
    }

    fun replace(target: Screen) {
        current.value = target
    }

    fun back(): Boolean {
        val previous = history.removeLastOrNull() ?: return false
        current.value = previous
        return true
    }

    fun toMenu() {
        history.clear()
        current.value = Screen.Menu
    }
}

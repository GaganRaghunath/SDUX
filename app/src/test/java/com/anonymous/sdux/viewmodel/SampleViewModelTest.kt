package com.anonymous.sdux.viewmodel

import com.anonymous.sdux.base.state.BaseUiEvent
import com.anonymous.sdux.base.state.BaseUiState
import com.anonymous.sdux.base.viewmodel.BaseViewModel
import com.anonymous.testshared.base.BaseViewModelTest
import com.anonymous.testshared.extensions.testCollect
import com.anonymous.testshared.extensions.testStates
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

class SampleViewModelTest : BaseViewModelTest() {

    // ── Inline state / event / viewmodel ──────────────────────────────────────

    data class SampleState(
        override val isLoading: Boolean = false,
        override val error: String? = null,
        val count: Int = 0,
        val message: String = ""
    ) : BaseUiState

    sealed class SampleEvent : BaseUiEvent {
        data object Incremented : SampleEvent()
        data class MessageSent(val text: String) : SampleEvent()
    }

    private inner class SampleViewModel : BaseViewModel<SampleState, SampleEvent>(SampleState()) {

        fun increment() {
            updateState { copy(count = count + 1) }
            sendEvent(SampleEvent.Incremented)
        }

        fun decrement() {
            updateState { copy(count = count - 1) }
        }

        fun sendMessage(text: String) {
            updateState { copy(message = text) }
            sendEvent(SampleEvent.MessageSent(text))
        }

        fun setLoading() {
            updateState { copy(isLoading = true, error = null) }
        }

        fun setError(msg: String) {
            updateState { copy(isLoading = false, error = msg) }
        }

        fun reset() {
            updateState { SampleState() }
        }
    }

    private val vm = SampleViewModel()

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state - has default values`() {
        with(vm.uiState.value) {
            isLoading shouldBe false
            error shouldBe null
            count shouldBe 0
            message shouldBe ""
        }
    }

    // ── State transitions ─────────────────────────────────────────────────────

    @Test
    fun `increment - increases count by one`() {
        vm.increment()
        vm.uiState.value.count shouldBe 1
    }

    @Test
    fun `increment - accumulates across multiple calls`() {
        repeat(5) { vm.increment() }
        vm.uiState.value.count shouldBe 5
    }

    @Test
    fun `decrement - decreases count`() {
        vm.increment()
        vm.increment()
        vm.decrement()
        vm.uiState.value.count shouldBe 1
    }

    @Test
    fun `setLoading - sets flag and clears error`() {
        vm.setError("old error")
        vm.setLoading()
        with(vm.uiState.value) {
            isLoading shouldBe true
            error shouldBe null
        }
    }

    @Test
    fun `setError - clears loading and stores message`() {
        vm.setLoading()
        vm.setError("Something failed")
        with(vm.uiState.value) {
            isLoading shouldBe false
            error shouldBe "Something failed"
        }
    }

    @Test
    fun `reset - restores initial state`() {
        vm.increment()
        vm.sendMessage("hello")
        vm.setLoading()
        vm.reset()
        vm.uiState.value shouldBe SampleState()
    }

    // ── uiState flow (Turbine) ────────────────────────────────────────────────

    @Test
    fun `uiState - emits updated state after increment`() = runTest {
        vm.uiState.testStates {
            awaitItem().count shouldBe 0           // initial
            vm.increment()
            awaitItem().count shouldBe 1
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState - emits each loading and error transition`() = runTest {
        vm.uiState.testStates {
            awaitItem()                            // initial
            vm.setLoading()
            awaitItem().isLoading shouldBe true
            vm.setError("oops")
            awaitItem().error shouldBe "oops"
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── events channel (Turbine) ──────────────────────────────────────────────

    @Test
    fun `events - emits Incremented on increment`() = runTest {
        vm.events.testCollect {
            vm.increment()
            awaitItem() shouldBe SampleEvent.Incremented
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `events - emits MessageSent with correct text`() = runTest {
        vm.events.testCollect {
            vm.sendMessage("hello world")
            awaitItem() shouldBe SampleEvent.MessageSent("hello world")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `events - preserves order across multiple operations`() = runTest {
        vm.events.testCollect {
            vm.increment()
            vm.sendMessage("ping")
            awaitItem() shouldBe SampleEvent.Incremented
            awaitItem() shouldBe SampleEvent.MessageSent("ping")
            cancelAndIgnoreRemainingEvents()
        }
    }
}

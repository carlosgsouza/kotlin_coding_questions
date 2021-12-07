import org.junit.jupiter.api.assertThrows
import kotlin.IllegalStateException
import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ProbabilityCalculatorTest {
    /**
     * QUESTION
     * --------
     * Design a data structure that, given a sequence of events and their probabilities, can calculate the probability
     * of:
     * - All events happening
     * - No events happening
     * - At least one event happening
     */
    @Test
    fun test() {
        val pc = ProbabilityCalculator()
        assertThrows<IllegalStateException> { pc.atLeastOneEventHappening }
        assertThrows<IllegalStateException> { pc.allEventsHappening }
        assertThrows<IllegalStateException> { pc.noEventsHappening }

        pc.register(0.1)
        assertEquals(0.1, pc.atLeastOneEventHappening, 0.01)
        assertEquals(0.1, pc.allEventsHappening, 0.01)
        assertEquals(0.9, pc.noEventsHappening, 0.01)

        pc.register(0.2)
        assertEquals(1 - 0.9 * 0.8, pc.atLeastOneEventHappening, 0.01)
        assertEquals(0.1 * 0.2, pc.allEventsHappening, 0.01)
        assertEquals(0.9 * 0.8, pc.noEventsHappening, 0.01)

        pc.register(0.0)
        assertEquals(1 - 0.9 * 0.8 * 1, pc.atLeastOneEventHappening, 0.01)
        assertEquals(0.0, pc.allEventsHappening, 0.01)
        assertEquals(0.9 * 0.8 * 1, pc.noEventsHappening, 0.01)

    }

    class ProbabilityCalculator() {
        var isInitialized = false

        var allEventsHappening: Double by ProbabilityFieldDelegate()
        var noEventsHappening: Double by ProbabilityFieldDelegate()
        var atLeastOneEventHappening: Double by ProbabilityFieldDelegate()


        fun register(probability: Double) {
            if (!isInitialized) {
                isInitialized = true
            }

            allEventsHappening *= probability
            noEventsHappening *= 1 - probability
            atLeastOneEventHappening = 1 - noEventsHappening
        }

        internal fun getIfInitialized(value: Double): Double {
            if (!isInitialized) throw IllegalStateException()
            return value

        }

        class ProbabilityFieldDelegate(var value: Double = 1.0) {
            operator fun getValue(thisRef: ProbabilityCalculator, property: KProperty<*>): Double {
                if (!thisRef.isInitialized) throw IllegalStateException() else return value
            }

            internal operator fun setValue(thisRef: ProbabilityCalculator, property: KProperty<*>, newValue: Double) {
                value = newValue
            }

        }
    }
}

internal class WindowedProbabilityCalculatorTest {
    /**
     * FOLLOW-UP QUESTION
     * ------------------
     * Now the ProbabilityCalculator takes an windowSize in the constructor. The probability fields should only consider
     * the last windowSize events.
     */
    @Test
    fun test() {
        val pc = ProbabilityCalculator(2)
        assertThrows<IllegalStateException> { pc.allEventsHappening }
        assertThrows<IllegalStateException> { pc.noEventsHappening }

        pc.register(0.1)
        assertEquals(0.1, pc.allEventsHappening, 0.01)
        assertEquals(0.9, pc.noEventsHappening, 0.01)

        pc.register(0.2)
        assertEquals(0.1 * 0.2, pc.allEventsHappening, 0.01)
        assertEquals(0.9 * 0.8, pc.noEventsHappening, 0.01)

        pc.register(0.0)
        assertEquals(0.2 * 0.0, pc.allEventsHappening, 0.01)
        assertEquals(0.8 * 1.0, pc.noEventsHappening, 0.01)

        pc.register(0.0)
        assertEquals(0.0 * 0.0, pc.allEventsHappening, 0.01)
        assertEquals(1.0 * 1.0, pc.noEventsHappening, 0.01)

        pc.register(0.5)
        assertEquals(0.0 * 0.5, pc.allEventsHappening, 0.01)
        assertEquals(1.0 * 0.5, pc.noEventsHappening, 0.01)

        pc.register(0.3)
        assertEquals(0.5 * 0.3, pc.allEventsHappening, 0.01)
        assertEquals(0.5 * 0.7, pc.noEventsHappening, 0.01)

        pc.register(1.0)
        assertEquals(0.3 * 1.0, pc.allEventsHappening, 0.01)
        assertEquals(0.7 * 0.0, pc.noEventsHappening, 0.01)

        pc.register(1.0)
        assertEquals(1.0 * 1.0, pc.allEventsHappening, 0.01)
        assertEquals(0.0 * 0.0, pc.noEventsHappening, 0.01)

        pc.register(0.3)
        assertEquals(1.0 * 0.3, pc.allEventsHappening, 0.01)
        assertEquals(0.0 * 0.7, pc.noEventsHappening, 0.01)

    }

    class ProbabilityCalculator(
        val windowSize: Int,

        private val probabilities: MutableList<Double> = mutableListOf(),
        private var allEventsHappeningProbability: CumulativeProbability = CumulativeProbability(),
        private var noEventsHappeningProbability: CumulativeProbability = CumulativeProbability()
    ) {
        val allEventsHappening: Double
            get() = allEventsHappeningProbability.get()

        val noEventsHappening: Double
            get() = noEventsHappeningProbability.get()

        fun register(addedProbability: Double) {
            probabilities.add(addedProbability)

            if (probabilities.size > windowSize) {
                val removedProbability = probabilities.removeAt(0)
                allEventsHappeningProbability.removeProbability(removedProbability)
                noEventsHappeningProbability.removeProbability(1 - removedProbability)
            }
            allEventsHappeningProbability.addProbability(addedProbability)
            noEventsHappeningProbability.addProbability(1 - addedProbability)

            println("""
                probabilities=$probabilities
                allEventsHappeningProduct=$allEventsHappeningProbability
                noEventsHappeningProduct=$noEventsHappeningProbability""".trimIndent())
        }

        data class CumulativeProbability(
            var zeroCount: Int = 0,
            private var isInitialized: Boolean = false,
            private var value: Double = 1.0
        ) {
            fun get(): Double {
                if (!isInitialized) throw IllegalStateException()
                if (zeroCount > 0) return 0.0 else return value
            }

            fun addProbability(probability: Double) {
                isInitialized = true
                if (probability == 0.0) {
                    zeroCount++
                } else {
                    value *= probability
                }
            }

            fun removeProbability(probability: Double) {
                if (probability == 0.0) {
                    zeroCount--
                } else {
                    value /= probability
                }
            }
        }
    }
}


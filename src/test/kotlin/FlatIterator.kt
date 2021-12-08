import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FlatIterator {

    @Test
    fun test() {
        val it = listOf(listOf(), listOf(11, 12), listOf(), listOf(21)).flatIterator()

        assertEquals(it.hasNext(), true)
        assertEquals(it.next(), 11)
        assertEquals(it.hasNext(), true)
        assertEquals(it.hasNext(), true)
        assertEquals(it.next(), 12)
        assertEquals(it.hasNext(), true)
        assertEquals(it.next(), 21)
        assertEquals(it.hasNext(), false)
        assertThrows<NoSuchElementException> { it.next() }
    }

    fun <T> List<List<T>>.flatIterator(): Iterator<T> = FlatIterator(this)

    class FlatIterator<T>(val list: List<List<T>>, private val topListIterator: Iterator<List<T>> = list.iterator()) :
        Iterator<T> {

        var currentListIterator: Iterator<T>? = getNextSublistIterator()

        override fun hasNext(): Boolean = currentListIterator?.hasNext() ?: false
        override fun next(): T {
            // it is a non-null local copy of the current list iterator. If we tried to use currentListIterator directly
            // we would get a compilation error unless we use null safe operator. This happens even if we check if
            // currentListIterator is null. That is because it is a var and its value could've been modified by another
            // thread.
            val it = currentListIterator ?: throw NoSuchElementException()

            val result = it.next()
            if(!it.hasNext()) {
                currentListIterator = getNextSublistIterator()
            }

            return result
        }

        private fun getNextSublistIterator(): Iterator<T>? {
            var it: Iterator<T>?
            while (topListIterator.hasNext()) {
                it = topListIterator.next().iterator()
                if (it.hasNext()) {
                    return it
                }
            }
            return null
        }
    }
}
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInAnyOrder
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Combinations {

    @Test
    fun test() {
        assertEquals(listOf(), getAllCombinations(listOf()))

        assertEquals(listOf(listOf(1)), getAllCombinations(listOf(1)))

        assertThat(
            getAllCombinations(listOf(0, 1, 2)),
            containsInAnyOrder(
                listOf(0, 1, 2),
                listOf(0, 2, 1),
                listOf(1, 0, 2),
                listOf(1, 2, 0),
                listOf(2, 0, 1),
                listOf(2, 1, 0),
            )
        )
    }

    fun getAllCombinations(elements: List<Int>): List<List<Int>> {
        val result: MutableList<List<Int>> = mutableListOf()

        if(elements.size == 0) {
            return result
        }

        val unusedElementIndexes = (0 until elements.size).toMutableSet()
        val partialList = mutableListOf<Int>()

        getAllCombinations(elements, unusedElementIndexes, partialList, result)

        return result.toList()
    }

    private fun getAllCombinations(
        elements: List<Int>,
        unusedElementIndexes: MutableSet<Int>,
        partialList: MutableList<Int>,
        result: MutableList<List<Int>>) {

        println("""
            elements = $elements
            unusedElementIndexes = $unusedElementIndexes
            partialList = $partialList
            result = $result
            """.trimIndent())

        if(partialList.size == elements.size) {
            result.add(partialList.toList())
            println("Added $partialList to $result")
        }

        println("-")

        unusedElementIndexes.toSet().forEach {
            partialList.add(elements[it])
            unusedElementIndexes.remove(it)

            getAllCombinations(elements, unusedElementIndexes, partialList, result)

            unusedElementIndexes.add(it)
            partialList.removeLast()
        }
    }

    /**
     *
     *     [F, F, F]
     *
     *     1
     *        [T, F, F]
     *        [1]
     *        2
     *            [T, T, F]
     *            [1, 2]
     *            3
     *                [T, T, T]
     *                [1, 2, 3]
     *       3
     *            [T, F, T]
     *            [1, 3]
     *     2
     *        [F, T, F]
     *     3
     *        [F, F, T]
     *     */
}
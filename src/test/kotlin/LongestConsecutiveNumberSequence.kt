import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LongestConsecutiveNumberSequence {

    @Test
    fun testList() {
        assertEquals(0, getLCNS(listOf()))
        assertEquals(1, getLCNS(listOf(5)))
        assertEquals(1, getLCNS(listOf(5, 4)))
        assertEquals(1, getLCNS(listOf(5, 12)))
        assertEquals(2, getLCNS(listOf(5, 6)))
        assertEquals(2, getLCNS(listOf(1, 5, 6, 1)))
        assertEquals(2, getLCNS(listOf(1, 5, 6, 8)))
    }

    fun getLCNS(numbers: List<Int>): Int {
        if(numbers.isEmpty()) return 0

        var maxLength = 1
        var currentSequenceLength = 1

        for (i in 1 until numbers.size) {
            if (numbers[i] == numbers[i - 1] + 1) {
                currentSequenceLength++
                maxLength = Math.max(currentSequenceLength, maxLength)
            } else {
                currentSequenceLength = 1
            }
        }

        return maxLength
    }

    @Test
    fun testTree() {
        assertEquals(1, getLCNS(Node(1)))

        assertEquals(
            1, getLCNS(
                Node(
                    1,
                    Node(0),
                    Node(-2)
                )
            )
        )

        assertEquals(
            1, getLCNS(
                Node(
                    1,
                    Node(3),
                    Node(4)
                )
            )
        )

        assertEquals(
            2, getLCNS(
                Node(
                    1,
                    Node(2),
                    Node(2)
                )
            )
        )

        assertEquals(
            3, getLCNS(
                Node(
                    1,
                    Node(
                        2,
                        Node(5)
                    ),
                    Node(
                        2,
                        Node(3)
                    )
                )
            )
        )

        assertEquals(
            3, getLCNS(
                Node(
                    1,
                    Node(
                        5,
                        Node(5)
                    ),
                    Node(
                        10,
                        null,
                        Node(11,
                            Node (12))
                    )
                )
            )
        )
    }

    data class Node(val value: Int, val left: Node? = null, val right: Node? = null)

    fun getLCNS(tree: Node): Int {
        return Math.max(
            1,
            Math.max(
                getLCNS(tree.left, tree.value, 1),
                getLCNS(tree.right, tree.value, 1))
        )
    }

    fun getLCNS(node: Node?, parentValue: Int, parentSequenceLength: Int): Int {
        if(node == null) return 0

        val currentSequenceLength = if (node.value == parentValue+1) parentSequenceLength + 1 else 1

        return Math.max(
            currentSequenceLength,
            Math.max(
                getLCNS(node.left, node.value, currentSequenceLength),
                getLCNS(node.right, node.value, currentSequenceLength)
            )
        )
    }
}
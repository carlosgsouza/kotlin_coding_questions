import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * Given a sorted array, find the # of occurrence of a number
 * for eg: [1, 1, 2, 3, 3, 3, 4, 5]
 * find # of occurrence of 3 in time better than O(n)
 * source: http://www.careercup.com/question?id=5647476964982784
 */
class NumberOfOccurrencesInSortedLists {

    @Test
    fun test() {
        assertEquals(3, listOf(1, 1, 3, 7, 7, 7, 9, 10).getNumberOfOccurrences(7))
        assertEquals(0, listOf<Int>().getNumberOfOccurrences(7))
        assertEquals(0, listOf<Int>(1, 2, 3).getNumberOfOccurrences(7))
        assertEquals(3, listOf<Int>(7, 7, 7).getNumberOfOccurrences(7))
    }

    fun List<Int>.getNumberOfOccurrences(value: Int): Int {
        var first = 0
        var last = this.size - 1

        while(first <= last) {
            val i: Int = first + (last - first) / 2
            if(this[i] == value) {
                var count = 1

                var iBefore = i - 1
                while(iBefore >= 0 && this[iBefore--] == value) count++
                var iAfter = i + 1
                while(iAfter < size && this[iAfter++] == value) count++

                return count
            }
            if(this[i] > value) {
                last = i - 1
            } else {
                first = i + 1
            }
        }
        return 0
    }
}

/*
 NOTES
 It has to be better than O(N). We can use something based on binary search.

 Example:
 list = [1, 1, 3, 7, 7, 7, 9, 10, 11, 12, 13, 14, 15, 16, 16]
 value = 7

 First, find one occurrence of value
   [1, 1, 3, 7, 7, 7, 9, 10, 11, 12, 13, 14, 15, 16, 16]
   |                  ^                               |
   [1, 1, 3, 7, 7, 7, 9, 10, 11, 12, 13, 14, 15, 16, 16]
   |      ^           |

   [1, 1, 3, 7, 7, 7, 9, 10, 11, 12, 13, 14, 15, 16, 16]
            |   ˆ     |

   Now, we just need to count the occurrencess immediately before and after the element.

 */
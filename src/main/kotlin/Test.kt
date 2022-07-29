import java.lang.Math.max
import java.lang.Math.min

fun main() {

    val cases = readln().toInt()

    for (i in 1..cases) {
        val result = process()
        println("Case #$i: $result")
    }
}


fun process(): Int {
    fun readArray(): List<Int> {
        readln()

        return readln().split(" ").map { it.toInt() }
    }

    val a = readArray()
    val b = readArray()
    val k = readln().toInt()
    var max = 0

    fun next(j: Int): Int {
        val av = if (j >= a.size) {
            a.sum() + getMax(b, j - a.size)
        } else {
            getMax(a, j)
        }

        val bp2 = k - j

        val bv = getMax(b, bp2)

        return av + bv
    }

    for (j in 0..k) {
        val v = next(j)

        if (v > max) {
            max = v
        }
    }

    return max
}

//15 10 12 5 1 10
fun getMax(array: List<Int>, limit: Int): Int {
    var max = 0
    var l = 0
    var r = array.lastIndex
    var k = min(limit, array.lastIndex)

    while (k > 0) {
        val left = array.subList(l, l + k).sum()
        val right = array.subList(r - k + 1, r + 1).sum()

        if (left > right) {
            max += array[l]
            l++
        } else {
            max += array[r]
            r--
        }

        k--
    }

    return max
}



/*
1
4
1 100 4 3
6
15 10 12 5 1 10
6

 */

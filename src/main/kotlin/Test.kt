fun main() {

    println(
        findMedianSortedArrays(nums1 = intArrayOf(1,2), nums2 = intArrayOf(3, 4))
    )
}

fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    val merged = nums1 + nums2
    merged.sort()

    val med = merged.size / 2

    return if (merged.size % 2 == 0) {
        (merged[med] + merged[med - 1]) / 2.0
    } else {
        merged[med].toDouble()
    }
}

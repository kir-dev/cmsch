package hu.bme.sch.cmsch.util

import java.security.MessageDigest

fun String.sha512() = hashString("SHA-512", this)

fun String.sha256() = hashString("SHA-256", this)

fun String.sha1() = hashString("SHA-1", this)

private fun hashString(type: String, input: String): String {
    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}

package utils

import models.tables.records.RequestRecord

fun toJumpUrl(guild: Long, channel: Long): String {
    return "https://discord.com/channels/$guild/$channel"
}

fun getJumpUrl(request: RequestRecord): String {
    return toJumpUrl(request.guild!!, request.thread!!)
}
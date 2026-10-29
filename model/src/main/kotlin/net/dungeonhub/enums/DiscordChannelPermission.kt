package net.dungeonhub.enums

/**
 * The Discord channel permissions that can be overwritten for ticket channels.
 * [bit] is the matching Discord permission flag.
 */
enum class DiscordChannelPermission(val bit: Long) {
    ViewChannel(1L shl 10),
    SendMessages(1L shl 11),
    ManageMessages(1L shl 13),
    EmbedLinks(1L shl 14),
    AttachFiles(1L shl 15),
    ReadMessageHistory(1L shl 16),
    MentionEveryone(1L shl 17)
}

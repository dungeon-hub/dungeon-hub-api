package net.dungeonhub.enums

enum class TranscriptTarget(
    val sendToUser: Boolean,
    val sendToTranscriptChannel: Boolean
) {
    None(false, false),
    User(true, false),
    TranscriptChannel(false, true),
    Both(true, true)
}
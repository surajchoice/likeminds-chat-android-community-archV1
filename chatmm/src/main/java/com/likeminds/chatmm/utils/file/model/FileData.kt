package com.likeminds.chatmm.utils.file.model

class FileData private constructor(
    val type: String,
    val path: String
) {
    class Builder {
        private var type = ""
        private var path = ""

        fun type(type: String) = apply { this.type = type }
        fun path(path: String) = apply { this.path = path }

        fun build() = FileData(type, path)
    }

    fun toBuilder(): Builder {
        return Builder().type(type).path(path)
    }
}
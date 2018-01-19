package io.scal.ambi.entity.base

data class ServerFile(val id: String,
                      val url: String,
                      val fileType: String,
                      val fileSize: Long) {

    val isImage: Boolean = imageFileTypes.contains(fileType.toLowerCase())

    companion object {

        private val imageFileTypes = listOf("jpg", "jpeg", "png", "svg")
    }
}
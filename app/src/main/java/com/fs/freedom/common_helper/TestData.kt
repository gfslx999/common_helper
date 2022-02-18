package com.fs.freedom.common_helper

data class TestData(
    val id: Int,
    val name: String,
    val childList: List<ChildData>
) {
    fun copyOf(newId: Int? = null, newName: String? = null, newList: List<ChildData>? = null) : TestData {
        return TestData(
            newId ?: this.id,
            newName ?: this.name,
            newList ?: this.childList
        )
    }
}

data class ChildData(
    val useTime: String
)
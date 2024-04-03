class Document(
    val rootTag: Tag
)

interface Tag {
    val name: String
    val attributes: Map<String, String>

    fun addTag(tag: Tag)
    fun removeTag(tag: Tag)
}

data class CompositeTag(
    override val name: String,
    override val attributes: Map<String, String>  = emptyMap(),
    val children: MutableList<Tag> = mutableListOf() // Mundei para mutable, pode ser problemaa
) : Tag{
    override fun addTag(tag: Tag) {
        children.add(tag)
    }

    override fun removeTag(tag: Tag) {
        children.remove(tag)
    }

}

data class StringTag(
    override val name: String,
    override val attributes: Map<String, String> = emptyMap(),
    val content : String //caso não tenha nada, é CompositeTag
) : Tag{
    override fun addTag(tag: Tag) {
        throw UnsupportedOperationException("StringTag cannot have tag inside.")
    }

    override fun removeTag(tag: Tag) {
        throw UnsupportedOperationException("StringTag cannot have tag inside.")
    }
}
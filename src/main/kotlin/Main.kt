class Document(
    val rootTag: Tag
)

interface Tag {
    val name: String
    val attributes: Map<String, String>
}

data class CompositeTag(
    override val name: String,
    override val attributes: Map<String, String>  = emptyMap(),
    val children: List<Tag> = emptyList()
) : Tag

data class StringTag(
    override val name: String,
    override val attributes: Map<String, String> = emptyMap(),
    val content : String //caso não tenha nada, é CompositeTag
) : Tag
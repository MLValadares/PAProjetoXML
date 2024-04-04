class Document(
    val rootTag: Tag
){
    override fun toString(): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${rootTag}"
    }
}

interface Tag {
    val name: String
    val attributes: MutableMap<String, String> //possivel problemaa, por mais abstrato?

    fun addTag(tag: Tag)
    fun removeTag(tag: Tag)
    fun addAttribute(key: String, value: String) {
        attributes[key] = value
    }

    fun removeAttribute(key: String) {
        attributes.remove(key)
    }

    fun modifyAttribute(key: String, value: String) {
        attributes[key] = value
    }
}

data class CompositeTag(
    override val name: String,
    override val attributes: MutableMap<String, String> = mutableMapOf(),
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
    override val attributes: MutableMap<String, String> = mutableMapOf(),
    val content : String //caso não tenha nada, é CompositeTag
) : Tag{
    override fun addTag(tag: Tag) {
        throw UnsupportedOperationException("StringTag cannot have tag inside.")
    }

    override fun removeTag(tag: Tag) {
        throw UnsupportedOperationException("StringTag cannot have tag inside.")
    }
}

fun main(){
    val a = Document(StringTag("ah", content = "coisas"))
    print(a)
}
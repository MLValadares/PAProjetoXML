class Document(
    val rootTag: Tag
){
    override fun toString(): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${rootTag.toString()}"
    }
}

interface Tag {
    val name: String
    val attributes: MutableMap<String, String> //possivel problema, por mais abstrato?

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

    fun toString(sb: StringBuilder, depth: Int) {
        val indent = "  ".repeat(depth)
        sb.append("$indent<$name")
        for ((key, value) in attributes) {
            sb.append(" $key=\"$value\"")
        }
        if (this is CompositeTag && children.isNotEmpty()) {
            sb.append(">\n")
            for (child in children) {
                child.toString(sb, depth + 1)
            }
            sb.append("$indent</$name>\n")
        } else if (this is StringTag) {
            sb.append(">$content</$name>\n")
        } else {
            sb.append(" />\n")
        }
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

    override fun toString(): String {
        val sb = StringBuilder()
        toString(sb, 0)
        return sb.toString()
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

    override fun toString(): String {
        val sb = StringBuilder()
        toString(sb, 0)
        return sb.toString()
    }
}

//fun main(){
//    val a = Document(StringTag("ah", content = "coisas"))
//    print(a)
//}

fun main() {
    val document = Document(
        CompositeTag("html").apply {
            addTag(
                CompositeTag("head").apply {
                    addTag(StringTag("title", content = "Document Title"))
                }
            )
            addTag(
                CompositeTag("body").apply {
                    addTag(StringTag("h1", content = "Hello, world!"))
                    addTag(StringTag("p", content = "This is a paragraph."))
                }
            )
        }
    )
    println(document)
}
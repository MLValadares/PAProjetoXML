import java.io.File

class Document(val rootTag: Tag){
    override fun toString(): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${rootTag.toString()}"
    }
    fun writeToFile(filePath: String) {
        val xmlString = this.toString()
        try { //completar testes
            File(filePath).writeText(xmlString)
            println("XML document written to file: $filePath")
        } catch (e: Exception) {
            println("Error writing XML document to file: $e")
        }
    }
}

interface Tag {
    val name: String
    val attributes: MutableMap<String, String> //possivel problema, por mais abstrato?
    var parent: CompositeTag?

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
            sb.append("/>\n")
        }
    }
}

data class CompositeTag(
    override val name: String,
    override val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<Tag> = mutableListOf(), // Mundei para mutable, pode ser problema
    override var parent: CompositeTag? = null
) : Tag{

    init {
        parent?.children?.add(this)
    }

    override fun addTag(tag: Tag) {
        children.add(tag)
        tag.parent = this
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
    val content : String, //caso não tenha nada, é CompositeTag
    override var parent: CompositeTag? = null
) : Tag{
    init {
        parent?.children?.add(this)
    }

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
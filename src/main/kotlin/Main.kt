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
    fun addAttributes(tagName: String, attributeKey: String, attributeValue: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName) {
                tag.addAttribute(attributeKey, attributeValue)
            }
            true
        }
        rootTag.accept(visitor)
    }

    fun renameTags(oldName: String, newName: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == oldName) {
                tag.rename(newName)
            }
            true
        }
        rootTag.accept(visitor)
    }
    fun renameAttributes(tagName: String, oldKey: String, newKey: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName && tag.attributes.containsKey(oldKey)) {
                tag.renameAttribute(oldKey, newKey)
            }
            true
        }
        rootTag.accept(visitor)
    }
    fun removeTags(tagName: String) {
        val list = mutableListOf<Tag>()
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName) {
//                tag.parent?.removeTag(tag)
                list.add(tag)
            }
            true
        }
        rootTag.accept(visitor)
        list.forEach {
            it.parent?.removeTag(it)
        }
    }
    fun removeAttributes(tagName: String, attributeKey: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName && tag.attributes.containsKey(attributeKey)) {
                tag.removeAttribute(attributeKey)
            }
            true
        }
        rootTag.accept(visitor)
    }
}

interface Tag {
    var name: String //o que fazer aqui?
    val attributes: Map<String, String> //possivel problema, por mais abstrato? //protected??
    var parent: CompositeTag?
//remover do proprio do pai

    fun addToParent(parent: CompositeTag) {//testes
        parent.addTag(this)
    }  
    fun removeFromParent() {//testes
        parent?.removeTag(this)
    }
    fun addAttribute(key: String, value: String) {
        (attributes as MutableMap)[key] = value
    }

    fun removeAttribute(key: String) {
        (attributes as MutableMap).remove(key)
    }

    fun renameAttribute(oldKey: String, newKey: String) {
        val value = (attributes as MutableMap).remove(oldKey)
        if (value != null) {
            (attributes as MutableMap)[newKey] = value
        }
    }

    fun modifyAttribute(key: String, value: String) {
        (attributes as MutableMap)[key] = value
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
    fun accept(visitor: (Tag) -> Boolean) {
        visitor(this)
    }

    fun rename(newName: String) {
        this.name = newName
    }
}

data class CompositeTag(
    override var name: String, //mudar para val
    override val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<Tag> = mutableListOf(), // Mundei para mutable, pode ser problema
    //val children: MutableList<Tag> = mutableListOf().filterNotNull(), // Filtra qualquer valor null da lista
    override var parent: CompositeTag? = null
) : Tag{

    init {
        require(name.isNotBlank()) { "Nome não deve ficar em branco" } //fazer mais
        parent?.children?.add(this)
    }
    //override
    fun addTag(tag: Tag) {
        children.add(tag)
        tag.parent = this
    }
    fun removeTag(tag: Tag) {
        children.remove(tag)
    }
    override fun toString(): String {
        val sb = StringBuilder()
        toString(sb, 0)
        return sb.toString()
    }
    override fun accept(visitor: (Tag) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }
}

data class StringTag(
    override var name: String,
    override val attributes: MutableMap<String, String> = mutableMapOf(),
    val content : String, //caso não tenha nada, é CompositeTag
    override var parent: CompositeTag? = null
) : Tag{
    init {
        require(name.isNotBlank()) { "Nome não deve ficar em branco" } //fazer mais //adicionr regex
        parent?.children?.add(this)
    }


    override fun toString(): String {
        val sb = StringBuilder()
        toString(sb, 0)
        return sb.toString()
    }
}
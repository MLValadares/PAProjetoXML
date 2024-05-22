import java.io.File
import kotlin.reflect.*
import kotlin.reflect.full.*

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

    fun toXPath(expression: String): List<Tag> {
        val expressionParts = expression.split('/')
        var currentTags: List<Tag> = listOf(rootTag)

        for (part in expressionParts) {
            currentTags = currentTags.flatMap { tag ->
                when (tag) {
                    is CompositeTag -> tag.children.filter { it.name == part }
                    else -> emptyList()
                }
            }
        }

        return currentTags
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
        parent = null
    }
    fun addAttribute(key: String, value: String) {
        require(key.isNotBlank()) { "Attribute name cannot be blank" }
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
//        require(name.matches(Regex("^[a-zA-Z0-9]+$"))) { "Nome deve conter apenas letras e números" }
        require(attributes.keys.none { it.isBlank() }) { "Attribute name cannot be blank" }
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
//        require(name.matches(Regex("^[a-zA-Z0-9]+$"))) { "Nome deve conter apenas letras e números" }
        require(attributes.keys.none { it.isBlank() }) { "Attribute name cannot be blank" }
        parent?.children?.add(this)
    }


    override fun toString(): String {
        val sb = StringBuilder()
        toString(sb, 0)
        return sb.toString()
    }
}

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class NameChanger(val newName: String)
@Target(AnnotationTarget.PROPERTY)
annotation class Exclude
@Target(AnnotationTarget.PROPERTY)
annotation class AsTextTag
@Target(AnnotationTarget.PROPERTY)
annotation class FowardTags
@Target(AnnotationTarget.PROPERTY)
annotation class ModifyString(val transformer: KClass<out StringTransformer>)

// Interface para transformações de String
interface StringTransformer {
    fun transform(value: String): String
}

// Anotação para adaptadores
@Target(AnnotationTarget.CLASS)
annotation class Adapter(val adapter: KClass<out TagAdapter>)

// Interface para adaptadores
interface TagAdapter {
    fun adapt(entity: Tag): Tag
}

fun Any.toTag(): Tag {
    val clazz = this::class
    val className = if(clazz.hasAnnotation<NameChanger>()){
        require(clazz.findAnnotation<NameChanger>()!!.newName.isNotBlank()) { "New name cannot be blank" }
        clazz.findAnnotation<NameChanger>()!!.newName
    }else{ clazz.simpleName!! }
    val attributes = mutableMapOf<String, String>()
    val children = mutableListOf<Tag>()
    clazz.declaredMemberProperties.forEach { prop ->
        if (!prop.hasAnnotation<Exclude>()) {
            val name = if (prop.hasAnnotation<NameChanger>()) {
                require(prop.findAnnotation<NameChanger>()!!.newName.isNotBlank()) { "New name cannot be blank" }
                prop.findAnnotation<NameChanger>()!!.newName
            } else {
                prop.name
            }
            val value = prop.call(this)
            if (prop.hasAnnotation<AsTextTag>()){
                children.add(StringTag(name, mutableMapOf(), value.toString()))
            }else if(value is List<*>){ //por mais abstrato
                if (prop.hasAnnotation<FowardTags>()) {
                    value.filterNotNull().forEach { element ->
                        children.add(element.toTag())
                    }
                } else {
                    val listTag = CompositeTag(name)
                    value.filterNotNull().forEach { element ->
                        listTag.addTag(element.toTag())
                    }
                    children.add(listTag)
                }
            }else{
                if(prop.hasAnnotation<ModifyString>()){
                    val transformer = prop.findAnnotation<ModifyString>()!!.transformer
                    val transformerInstance = transformer.objectInstance ?: transformer.createInstance()
                    val transformedValue = transformerInstance.transform(value.toString())
                    attributes[name] = transformedValue
                }else {
                    attributes[name] = value.toString()
                }
            }
        }
    }

    if(clazz.hasAnnotation<Adapter>()){
        val adapter = clazz.findAnnotation<Adapter>()!!.adapter
        val adapterInstance = adapter.objectInstance ?: adapter.createInstance()
        return adapterInstance.adapt(CompositeTag(className, attributes, children))
    }
        return CompositeTag(className, attributes, children)
}

class xmlBuilder(private val name: String) {
    private val children = mutableListOf<xmlBuilder>()
    private val attributes = mutableMapOf<String, String>()
    private var textContent: String? = null

    fun tag(name: String, init: xmlBuilder.() -> Unit): xmlBuilder {
        val child = xmlBuilder(name)
        child.init()
        children.add(child)
        return this
    }

    fun atr(name: String, value: String): xmlBuilder {
        attributes[name] = value
        return this
    }

    fun textTag(text: String): xmlBuilder {
        textContent = text
        return this
    }

    fun build(): Tag {
        return if (textContent != null) {
            StringTag(name, attributes, textContent!!, parent = null)
        } else {
            val tagChildren = children.map { it.build() }.toMutableList()
            CompositeTag(name, attributes, tagChildren, parent = null)
        }
    }
}


fun main(){
    val document2 = Document(
        CompositeTag(
            "plano",
            children = mutableListOf(
                StringTag("curso", content = "Mestrado em Engenharia Informática"),
                CompositeTag("fuc", mutableMapOf(("codigo" to "M4310")), mutableListOf(
                    StringTag("nome", content = "Programação Avançada"),
                    StringTag("ects", content = "6.0"),
                    CompositeTag("avaliacao", children = mutableListOf(
                        CompositeTag("componente", mutableMapOf(("nome" to "Quizzes"), ("peso" to "20%"))),
                        CompositeTag("componente", mutableMapOf(("nome" to "Projeto"), ("peso" to "80%")))
                    ))
                )),
                CompositeTag("fuc", mutableMapOf(("codigo" to "03782")), mutableListOf(
                    StringTag("nome", content = "Dissertação"),
                    StringTag("ects", content = "42.0"),
                    CompositeTag("avaliacao", children = mutableListOf(
                        CompositeTag("componente", mutableMapOf(("nome" to "Dissertação"), ("peso" to "60%"))),
                        CompositeTag("componente", mutableMapOf(("nome" to "Apresentação"), ("peso" to "20%"))),
                        CompositeTag("componente", mutableMapOf(("nome" to "Discussão"), ("peso" to "20%")))
                    ))
                ))
            )
        )
    )
    val matchingTags = document2.toXPath("fuc/avaliacao/componente")
    for (tag in matchingTags) {
        println(tag)
    }
}
import java.io.File
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Classe que representa um documento XML.
 * Permite manipular o documento de forma global, como: adicionar atributos, renomear tags e os seus atributos e remover tags e atributos.
 * Também permite a conversão de um documento XML para uma expressão XPath.
 *
 * @property rootTag a tag raiz do documento
 * @constructor cria um documento XML com a tag raiz especificada
 * @throws IllegalArgumentException se o nome da tag raiz estiver em branco
 *
 */

class Document(val rootTag: Tag){

    init {
        require(rootTag.name.isNotBlank()) { "Root tag name cannot be blank" }
    }

    /**
     * Retorna uma representação em string do documento XML.
     *
     * @return uma string que representa o documento XML
     */
    fun prettyString(): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${rootTag.prettyString()}"
    }

    /**
     * Escreve o documento XML num ficheiro.
     *
     * @param filePath o caminho do ficheiro onde o documento XML será escrito
     */
    fun writeToFile(filePath: String) {
        val xmlString = this.toString()
        try { //completar testes
            File(filePath).writeText(xmlString)
            println("XML document written to file: $filePath")
        } catch (e: Exception) {
            println("Error writing XML document to file: $e")
        }
    }

    /**
     * Adiciona um atributo a todas as tags com um determinado nome.
     *
     * @param tagName o nome da tag
     * @param attributeKey o nome do atributo
     * @param attributeValue o valor do atributo
     */
    fun addAttributes(tagName: String, attributeKey: String, attributeValue: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName) {
                tag.addAttribute(attributeKey, attributeValue)
            }
            true
        }
        rootTag.accept(visitor)
    }

    /**
     * Renomeia todas as tags com um determinado nome.
     *
     * @param oldName o nome antigo da tag
     * @param newName o novo nome da tag
     */
    fun renameTags(oldName: String, newName: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == oldName) {
                tag.rename(newName)
            }
            true
        }
        rootTag.accept(visitor)
    }

    /**
     * Renomeia todos os atributos de todas as tags com um determinado nome.
     *
     * @param tagName o nome da tag
     * @param oldKey o nome antigo do atributo
     * @param newKey o novo nome do atributo
     */
    fun renameAttributes(tagName: String, oldKey: String, newKey: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName && tag.attributes.containsKey(oldKey)) {
                tag.renameAttribute(oldKey, newKey)
            }
            true
        }
        rootTag.accept(visitor)
    }

    /**
     * Remove todas as tags com um determinado nome.
     *
     * @param tagName o nome da tag
     */
    fun removeTags(tagName: String) {
        val list = mutableListOf<Tag>()
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName) {
                list.add(tag)
            }
            true
        }
        rootTag.accept(visitor)
        list.forEach {
            it.parent?.removeTag(it)
        }
    }

    /**
     * Remove todos os atributos de todas as tags com um determinado nome.
     *
     * @param tagName o nome da tag
     * @param attributeKey o nome do atributo
     */
    fun removeAttributes(tagName: String, attributeKey: String) {
        val visitor: (Tag) -> Boolean = { tag ->
            if (tag.name == tagName && tag.attributes.containsKey(attributeKey)) {
                tag.removeAttribute(attributeKey)
            }
            true
        }
        rootTag.accept(visitor)
    }

    /**
     * Converte uma expressão XPath numa lista de tags, incluindo o seu conteúdo.
     *
     * @param expression a expressão XPath
     * @return uma lista de tags que correspondem à expressão XPath
     */
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


/**
 * Interface que representa uma tag de um documento XML.
 * Permite manipular tags de forma individual, como: adicionar e remover atributos, renomear tags e atributos e converter uma tag para uma representação em string.
 *
 * @property name o nome da tag
 * @property attributes os atributos da tag
 * @property parent a tag pai
 * @constructor cria uma tag com o nome especificado
 *
 */

interface Tag {
    var name: String //o que fazer aqui?
    val attributes: Map<String, String> //possivel problema, por mais abstrato? //protected??
    var parent: CompositeTag?


    /**
     * Adiciona a tag a um pai.
     *
     * @param parent a tag pai
     */
    fun addToParent(parent: CompositeTag) {//testes
        parent.addTag(this)
    }

    /**
     * Remove a tag do pai.
     */
    fun removeFromParent() {//testes
        parent?.removeTag(this)
        parent = null
    }

    /**
     * Adiciona um atributo à tag.
     *
     * @param key o nome do atributo
     * @param value o valor do atributo
     */
    fun addAttribute(key: String, value: String) {
        require(key.isNotBlank()) { "Attribute name cannot be blank" }
        (attributes as MutableMap)[key] = value
    }

    /**
     * Remove um atributo da tag.
     *
     * @param key o nome do atributo
     */
    fun removeAttribute(key: String) {
        (attributes as MutableMap).remove(key)
    }

    /**
     * Renomeia um atributo da tag.
     *
     * @param oldKey o nome antigo do atributo
     * @param newKey o novo nome do atributo
     */
    fun renameAttribute(oldKey: String, newKey: String) {
        val value = (attributes as MutableMap).remove(oldKey)
        if (value != null) {
            (attributes as MutableMap)[newKey] = value
        }
    }

    /**
     * Modifica um atributo da tag.
     *
     * @param key o nome do atributo
     * @param value o valor do atributo
     */
    fun modifyAttribute(key: String, value: String) {
        (attributes as MutableMap)[key] = value
    }

    /**
     * Retorna uma representação em string da tag.
     *
     * @param indent a indentação da tag
     * @return uma string que representa a tag
     */
    fun prettyString(indent: String = ""): String

    /**
     * Aceita um visitante que processa a tag.
     *
     * @param visitor o visitante
     */
    fun accept(visitor: (Tag) -> Boolean) {
        visitor(this)
    }

    /**
     * Renomeia a tag.
     *
     * @param newName o novo nome da tag
     */
    fun rename(newName: String) {
        this.name = newName
    }
}


/**
 * Classe que representa uma tag composta de um documento XML, que consiste numa tag com outras tag insiridas ou uma tag sem conteúdo.
 * Permite manipular tags compostas de forma individual, como: adicionar e remover tags, renomear tags e atributos e converter uma tag composta para uma representação em string.
 *
 * @property name o nome da tag
 * @property attributes os atributos da tag
 * @property children as tags filhas
 * @property parent a tag pai
 * @constructor cria uma tag composta com o nome especificado
 * @throws IllegalArgumentException se o nome da tag estiver em branco
 * @throws IllegalArgumentException se o nome da tag contiver caracteres inválidos
 * @throws IllegalArgumentException se o nome do atributo estiver em branco
 */
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

    /**
     * Adiciona uma tag filha à tag composta.
     *
     * @param tag a tag filha
     */
    fun addTag(tag: Tag) {
        children.add(tag)
        tag.parent = this
    }

    /**
     * Remove uma tag filha da tag composta.
     *
     * @param tag a tag filha
     */
    fun removeTag(tag: Tag) {
        children.remove(tag)
    }

    /**
     * Retorna uma representação em string da tag composta.
     *
     * @param indent a indentação da tag
     * @return uma string que representa a tag composta
     */
    override fun prettyString(indent: String): String {
        val sb = StringBuilder()
        sb.append("$indent<$name")
        for ((key, value) in attributes) {
            sb.append(" $key=\"$value\"")
        }
        if (children.isNotEmpty()) {
            sb.append(">\n")
            for (child in children) {
                sb.append(child.prettyString("$indent  "))
            }
            sb.append("$indent</$name>\n")
        } else {
            sb.append("/>\n")
        }
        return sb.toString()
    }

    /**
     * Aceita um visitante que processa a tag composta.
     *
     * @param visitor o visitante
     */
    override fun accept(visitor: (Tag) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }
}

/**
 * Classe que representa uma tag de texto de um documento XML, que consiste numa tag com um conteúdo de texto.
 * Permite manipular tags de texto de forma individual, como: adicionar e remover atributos, renomear tags e atributos e converter uma tag de texto para uma representação em string.
 *
 * @property name o nome da tag
 * @property attributes os atributos da tag
 * @property content o conteúdo da tag
 * @property parent a tag pai
 * @constructor cria uma tag de texto com o nome e conteúdo especificados
 * @throws IllegalArgumentException se o nome da tag estiver em branco
 * @throws IllegalArgumentException se o nome da tag contiver caracteres inválidos
 * @throws IllegalArgumentException se o nome do atributo estiver em branco
 */

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

    /**
     * Retorna uma representação em string da tag de texto.
     *
     * @param indent a indentação da tag
     * @return uma string que representa a tag de texto
     */

    override fun prettyString(indent: String): String {
        val sb = StringBuilder()
        sb.append("$indent<$name")
        for ((key, value) in attributes) {
            sb.append(" $key=\"$value\"")
        }
        sb.append(">$content</$name>\n")
        return sb.toString()
    }

}

/**
 * Anotoções utilizadas para a conversão de objetos para tags de XML.
 */

/**
 * Anotação que permite alterar o nome da propriedade/classes para a conversão de objetos para tags de XML.
 *
 * @property newName o novo nome da propriedade/classe
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class NameChanger(val newName: String)

/**
 * Anotação que permite excluir propriedades da conversão de objetos para tags de XML.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Exclude

/**
 * Anotação que permite transforma no que seria um propriedade da tag root em um tag de texto.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class AsTextTag

/**
 * Anotação que permite transformar uma propriedade numa lista de tags, de forma a "pushar" a lista em frente.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class FowardTags

/**
 * Anotação que permite modificar o valor de uma propriedade antes de ser convertida para uma tag de XML.
 *
 * @property transformer a classe que implementa a transformação
 */
@Target(AnnotationTarget.PROPERTY)
annotation class ModifyString(val transformer: KClass<out StringTransformer>)


/**
 * Interface para transformações de String
 */
interface StringTransformer {

    /**
     * Transforma uma string.
     *
     * @param value a string a ser transformada
     * @return a string transformada
     */
    fun transform(value: String): String
}

/**
 * Anotação que permite adicionar um adaptador a uma classe para a conversão de objetos para tags de XML.
 *
 * @property adapter a classe do adaptador
 */
@Target(AnnotationTarget.CLASS)
annotation class Adapter(val adapter: KClass<out TagAdapter>)

/**
 * Interface para adaptadores de tags.
 */
interface TagAdapter {
    fun adapt(entity: Tag): Tag
}


/**
 * Classe que representa um adaptador de tags de XML.
 * Permite adaptar tags de XML para outras tags de XML.
 *
 * @constructor cria um adaptador de tags de XML
 */
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
            }else if(value is Collection<*>){ //More abstract
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

class XmlBuilder(private val name: String) {
    private val children = mutableListOf<XmlBuilder>()
    private val attributes = mutableMapOf<String, String>()
    private var textContent: String? = null

    fun tag(name: String, init: XmlBuilder.() -> Unit): XmlBuilder {
        require(name.isNotBlank()) { "Tag name cannot be null or blank" }
        val child = XmlBuilder(name)
        child.init()
        children.add(child)
        return this
    }

    fun atr(name: String, value: String): XmlBuilder {
        attributes[name] = value
        return this
    }

    fun textTag(text: String): XmlBuilder {
        textContent = text
        return this
    }

    fun build(): Document {
        val rootTag = buildTag()
        return Document(rootTag as Tag)
    }

    fun buildTag(): Tag {
        return if (textContent != null) {
            StringTag(name ?: throw IllegalStateException("Tag name cannot be null"), attributes, textContent!!, parent = null)
        } else {
            CompositeTag(name ?: throw IllegalStateException("Tag name cannot be null"), attributes,
                children.map { it.buildTag() }.toMutableList(), parent = null)
        }
    }

}




fun main(){
//    val document2 = Document(
//        CompositeTag(
//            "plano",
//            children = mutableListOf(
//                StringTag("curso", content = "Mestrado em Engenharia Informática"),
//                CompositeTag("fuc", mutableMapOf(("codigo" to "M4310")), mutableListOf(
//                    StringTag("nome", content = "Programação Avançada"),
//                    StringTag("ects", content = "6.0"),
//                    CompositeTag("avaliacao", children = mutableListOf(
//                        CompositeTag("componente", mutableMapOf(("nome" to "Quizzes"), ("peso" to "20%"))),
//                        CompositeTag("componente", mutableMapOf(("nome" to "Projeto"), ("peso" to "80%")))
//                    ))
//                )),
//                CompositeTag("fuc", mutableMapOf(("codigo" to "03782")), mutableListOf(
//                    StringTag("nome", content = "Dissertação"),
//                    StringTag("ects", content = "42.0"),
//                    CompositeTag("avaliacao", children = mutableListOf(
//                        CompositeTag("componente", mutableMapOf(("nome" to "Dissertação"), ("peso" to "60%"))),
//                        CompositeTag("componente", mutableMapOf(("nome" to "Apresentação"), ("peso" to "20%"))),
//                        CompositeTag("componente", mutableMapOf(("nome" to "Discussão"), ("peso" to "20%")))
//                    ))
//                ))
//            )
//        )
//    )
//    val matchingTags = document2.toXPath("fuc/avaliacao/componente")
//    for (tag in matchingTags) {
//        println(tag)
//    }

    print(StringTag("nome", content = "Programação Avançada"))
    print(StringTag("nome", content = "Programação Avançada"))
}

//fazer regex
//toDocument

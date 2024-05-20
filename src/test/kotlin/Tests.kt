import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File


//Adicionar dependência JUnit
//org.junit.jupiter:junit-jupiter:5.10.2
class Tests {

    private val document = Document(
        CompositeTag(
            "plano",
            children = mutableListOf(
                StringTag(
                    "Mestrado em Engenharia Informática",
                    content = "olá"
                )
            )
        )
    )
    private val document1 = Document(
        CompositeTag(
            "plano",
            children = mutableListOf(
                StringTag("curso", content = "Mestrado em Engenharia Informática"),
                CompositeTag("fuc", mutableMapOf(("code" to "M4310")), mutableListOf(
                    StringTag("nome", content = "Programação Avançada"),
                    StringTag("ects", content = "6.0"),
                    CompositeTag("avaliacao", children = mutableListOf(
                        CompositeTag("componente", mutableMapOf(("nome" to "Quizzes"),("Projeto" to "80%")))
                    ))
                ))
            )
        )
    )

    private val document2 = Document(
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


    @Test
    fun addToParentTest() {
        val parentTag = CompositeTag("parent")
        val childTag = CompositeTag("child")

        childTag.addToParent(parentTag)

        assertTrue(parentTag.children.contains(childTag))
        assertEquals(parentTag, childTag.parent)
    }
    @Test
    fun removeFromParentTest() {
        val parentTag = CompositeTag("parent")
        val childTag = CompositeTag("child")

        childTag.addToParent(parentTag)
        assertTrue(parentTag.children.contains(childTag))
        assertEquals(parentTag, childTag.parent)

        childTag.removeFromParent()
        assertFalse(parentTag.children.contains(childTag))
        assertNull(childTag.parent)
    }
    @Test
    fun CompositeTagNotEmptyTest() {
        assertThrows(IllegalArgumentException::class.java) {
            CompositeTag(" ")
        }
    }

//    @Test
//    fun CompositeTagRegex() {
//        assertThrows(IllegalArgumentException::class.java) {
//            CompositeTag("invalid-name!")
//        }
//    }

    @Test
    fun StringTagNotEmptyTest() {
        assertThrows(IllegalArgumentException::class.java) {
            StringTag(" ", content = "content")
        }
    }

//    @Test
//    fun StringTagRegex() {
//        assertThrows(IllegalArgumentException::class.java) {
//            StringTag("invalid-name!", content = "content")
//        }
//    }

    @Test
    fun inicioTest(){
        assertTrue(true)
    }

    //testes base
    @Test
    fun test1(){
        assertEquals("plano", document.rootTag.name)
        assertEquals(1, (document.rootTag as CompositeTag).children.size)
    }

    @Test
    fun test2(){
        val childTag = (document.rootTag as CompositeTag).children[0] as StringTag
        assertEquals("Mestrado em Engenharia Informática", childTag.name)
        assertEquals("olá", childTag.content)
    }

    //addEntity

    @Test
    fun addTagTest(){
        val parentTag = CompositeTag("parent")
        val childTag1 = StringTag("child1", content = "Child1 content")
        val childTag2 = StringTag("child2", content = "Child2 content")
        parentTag.addTag(childTag1)

        assertEquals(1, parentTag.children.size)
        assertEquals(childTag1, parentTag.children[0])

        parentTag.addTag(childTag2)

        assertEquals(2, parentTag.children.size)
        assertEquals(childTag1, parentTag.children[0])
        assertEquals(childTag2, parentTag.children[1])
    }

    //removeEntity
    @Test
    fun removeTagTest() {
        val parentTag = CompositeTag("parent")
        val childTag1 = StringTag("child1", content = "Child1 content")
        val childTag2 = StringTag("child2", content = "Child2 content")
        parentTag.addTag(childTag1)
        parentTag.addTag(childTag2)

        assertTrue(parentTag.children.contains(childTag1))
        assertTrue(parentTag.children.contains(childTag2))

        parentTag.removeTag(childTag1)

        assertFalse(parentTag.children.contains(childTag1))

        parentTag.removeTag(childTag2)
        assertFalse(parentTag.children.contains(childTag2))
    }

    //addAttribute
    @Test
    fun addAttributeTest(){
        val compositeTag = CompositeTag("teste")

        compositeTag.addAttribute("atributo", "novo")
        compositeTag.addAttribute("atributos", "velho")

        assertEquals("novo", compositeTag.attributes["atributo"])
        assertNotEquals("velho", compositeTag.attributes["atributo"])

    }


    //modifyAttribute
    @Test
    fun modifyAttributeTest(){
        val compositeTag = CompositeTag("teste")

        compositeTag.addAttribute("atributo", "novo")

        compositeTag.modifyAttribute("atributo", "novato")

        assertEquals("novato", compositeTag.attributes["atributo"])
        assertNotEquals("novo", compositeTag.attributes["atributo"])
    }
    //removeAttribute
    @Test
    fun removeAttribute(){
        val compositeTag = CompositeTag("teste")

        compositeTag.addAttribute("atributo", "novo")
        compositeTag.addAttribute("atributo2", "novo")

        assertTrue(compositeTag.attributes.containsKey("atributo"))
        assertTrue(compositeTag.attributes.containsKey("atributo2"))

        compositeTag.removeAttribute("atributo")

        assertFalse(compositeTag.attributes.containsKey("atributo"))
        assertTrue(compositeTag.attributes.containsKey("atributo2"))
    }

    //check parent
    @Test
    fun checkParentTest() {
        val html = CompositeTag("html")
        val head = CompositeTag("head")
        val body = CompositeTag("body")
        val title = StringTag("title", content =  "Document Title")

        html.addTag(head)
        html.addTag(body)
        head.addTag(title)

        assertNotEquals(head.parent,null)
        assertNotEquals(body.parent,null)
        assertNotEquals(title.parent,null)
        assertNotEquals(title.parent,body.parent)
        assertNotEquals(title.parent,head.parent)

        assertEquals(html, head.parent)
        assertEquals(html, body.parent)
        assertEquals(body.parent, head.parent)
        assertEquals(html.parent,null)
        assertEquals(head, title.parent)
    }

    //pretty print
    @Test
    fun test10(){
        val expected = """
            <?xml version="1.0" encoding="UTF-8"?>
            <plano>
              <curso>Mestrado em Engenharia Informática</curso>
              <fuc code="M4310">
                <nome>Programação Avançada</nome>
                <ects>6.0</ects>
                <avaliacao>
                  <componente nome="Quizzes" Projeto="80%"/>
                </avaliacao>
              </fuc>
            </plano>
            
        """.trimIndent()
        val output = document1.toString()
        assertEquals(expected, output)
    }
    //escrita para ficheiro
    @Test
    fun test11(){
        document1.writeToFile("document.xml")
        val file = File("document.xml")
        assertTrue(file.exists())
        assertEquals(document1.toString(), file.readText())
    }

    //visitor

    //adicionar atributos globalmente
    @Test
    fun addAttributesTest() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        val childTag2 = CompositeTag("child")
        val grandChildTag = CompositeTag("grandChild")

        rootTag.addTag(childTag1)
        rootTag.addTag(childTag2)

        childTag1.addTag(grandChildTag)

        val document = Document(rootTag)

        document.addAttributes("child", "attributeKey1", "attributeValue1")
        document.addAttributes("child", "attributeKey2", "attributeValue2")
        document.addAttributes("grandChild", "attributeKey3", "attributeValue3")
        document.addAttributes("grandChild", "attributeKey4", "attributeValue4")

        assertEquals("attributeValue1", childTag1.attributes["attributeKey1"])
        assertEquals("attributeValue1", childTag2.attributes["attributeKey1"])
        assertEquals("attributeValue2", childTag1.attributes["attributeKey2"])
        assertEquals("attributeValue2", childTag2.attributes["attributeKey2"])
        assertEquals("attributeValue3", grandChildTag.attributes["attributeKey3"])
        assertEquals("attributeValue4", grandChildTag.attributes["attributeKey4"])
    }

    //renomeação de entidades globalmente
    @Test
    fun renameTagsTest() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        val childTag2 = CompositeTag("child")
        rootTag.addTag(childTag1)
        rootTag.addTag(childTag2)
        val document = Document(rootTag)

        document.renameTags("child", "newChild")

        assertEquals("newChild", childTag1.name)
        assertEquals("newChild", childTag2.name)
    }

    //renomeação de atributos globalmente
    @Test
    fun renameAttributesTest() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        val childTag2 = CompositeTag("child")
        childTag1.addAttribute("oldAttribute", "value1")
        childTag2.addAttribute("oldAttribute", "value2")
        rootTag.addTag(childTag1)
        rootTag.addTag(childTag2)
        val document = Document(rootTag)

        document.renameAttributes("child", "oldAttribute", "newAttribute")

        assertFalse(childTag1.attributes.containsKey("oldAttribute"))
        assertEquals("value1", childTag1.attributes["newAttribute"])
        assertFalse(childTag2.attributes.containsKey("oldAttribute"))
        assertEquals("value2", childTag2.attributes["newAttribute"])
    }

    //remoção de entidades globalmente
    @Test
    fun removeTagsTest() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        val childTag2 = CompositeTag("child")
        rootTag.addTag(childTag1)
        rootTag.addTag(childTag2)
        val document = Document(rootTag)

        document.removeTags("child")

        assertFalse(rootTag.children.contains(childTag1))
        assertFalse(rootTag.children.contains(childTag2))
    }
    //remoção de atributos globalmente
    @Test
    fun removeAttributesTest() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        val childTag2 = CompositeTag("child")
        childTag1.addAttribute("oldAttribute", "value1")
        childTag2.addAttribute("oldAttribute", "value2")
        rootTag.addTag(childTag1)
        rootTag.addTag(childTag2)
        val document = Document(rootTag)

        document.removeAttributes("child", "oldAttribute")

        assertFalse(childTag1.attributes.containsKey("oldAttribute"))
        assertFalse(childTag2.attributes.containsKey("oldAttribute"))
    }


    //X-Path
//    @Test
//    fun toXPathTest() {
//        val res = document1.toXPath("plano/fuc/ects")
//        assertEquals(1, res.size)
//    }
    @Test
    fun toXPathTest() {
        val res = document2.toXPath("fuc/avaliacao/componente")
        assertEquals(5, res.size)

        // Check if the tag names match the expected value
        assertEquals("componente", (res[0] as? CompositeTag)?.name)
        assertEquals("componente", (res[1] as? CompositeTag)?.name)
        assertEquals("componente", (res[2] as? CompositeTag)?.name)
        assertEquals("componente", (res[3] as? CompositeTag)?.name)
        assertEquals("componente", (res[4] as? CompositeTag)?.name)
    }

    @Test
    fun addAttributeThrowException() {
        val tag = CompositeTag("test")

        val exception = assertThrows(IllegalArgumentException::class.java) {
            tag.addAttribute("", "value")
        }

        assertEquals("Attribute name cannot be blank", exception.message)
    }

    @Test
    fun compositeTagThrowException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            CompositeTag("test", mutableMapOf(Pair("", "value")))
        }

        assertEquals("Attribute name cannot be blank", exception.message)
    }

    @Test
    fun stringTagThrowException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            StringTag("test", mutableMapOf(Pair("", "value")), "content")
        }

        assertEquals("Attribute name cannot be blank", exception.message)
    }
}
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
    fun test0(){
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
    fun test7(){
        val compositeTag = CompositeTag("div")
        compositeTag.addAttribute("class", "main")
        compositeTag.modifyAttribute("class", "container")
        assertEquals("container", compositeTag.attributes["class"])
    }
    //removeAttribute
    @Test
    fun test8(){
        val compositeTag = CompositeTag("div")
        compositeTag.addAttribute("id", "container")
        assertTrue(compositeTag.attributes.containsKey("id"))
        compositeTag.removeAttribute("id")
        assertFalse(compositeTag.attributes.containsKey("id"))
    }

    //check parent
    @Test
    fun checkParentTest() {
        val html = CompositeTag("html")
        val head = CompositeTag("head")
        val body = CompositeTag("body")

        html.addTag(head)
        html.addTag(body)

        assertEquals(html, head.parent)
        assertEquals(html, body.parent)

        val title = StringTag("title", content =  "Document Title")
        head.addTag(title)

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
}
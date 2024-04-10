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
    fun test3(){
        val parentTag = CompositeTag("parent")
        val childTag = StringTag("child", content = "Child content")
        parentTag.addTag(childTag)

        assertEquals(1, parentTag.children.size)
        assertEquals(childTag, parentTag.children[0])
    }

//    @Test
//    fun test4() {
//        val stringTag = StringTag("string", content = "String content")
//        val childTag = CompositeTag("child")
//        assertFailsWith<UnsupportedOperationException>(stringTag.addTag(childTag))
//    }

    //removeEntity
    @Test
    fun test5() {
        val parentTag = CompositeTag("parent")
        val childTag = StringTag("child", content = "Child content")
        parentTag.addTag(childTag)

        assertTrue(parentTag.children.contains(childTag))

        parentTag.removeTag(childTag)

        assertFalse(parentTag.children.contains(childTag))
    }

//    @Test
//    fun test6() {
//        val stringTag = StringTag("string", content = "String content")
//        val childTag = CompositeTag("child")
//        assertFailsWith<UnsupportedOperationException>(stringTag.removeTag(childTag))
//    }

    //addAttribute
    @Test
    fun test6(){
        val compositeTag = CompositeTag("div")
        compositeTag.addAttribute("id", "container")
        assertEquals("container", compositeTag.attributes["id"])
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
    fun test9() {
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

    //check children

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
    fun test12() {
        val rootTag = CompositeTag("root")
        val childTag = CompositeTag("child")
        rootTag.addTag(childTag)
        val document = Document(rootTag)

        document.addAttributes("child", "attributeKey", "attributeValue")

        assertEquals("attributeValue", childTag.attributes["attributeKey"])
    }

    //renomeação de entidades globalmente
    @Test
    fun test13() {
        val rootTag = CompositeTag("root")
        val childTag = CompositeTag("child")
        rootTag.addTag(childTag)
        val document = Document(rootTag)

        document.renameTags("child", "newChild")

        assertEquals("newChild", childTag.name)
    }

    //renomeação de atributos globalmente
    @Test
    fun test14() {
        val rootTag = CompositeTag("root")
        val childTag = CompositeTag("child")
        childTag.addAttribute("oldAttribute", "value")
        rootTag.addTag(childTag)
        val document = Document(rootTag)

        document.renameAttributes("child", "oldAttribute", "newAttribute")

        assertFalse(childTag.attributes.containsKey("oldAttribute"))
        assertEquals("value", childTag.attributes["newAttribute"])
    }

    //remoção de entidades globalmente
    @Test
    fun test15() {
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
    fun test16() {
        val rootTag = CompositeTag("root")
        val childTag1 = CompositeTag("child")
        childTag1.addAttribute("attributeToRemove", "value")
        rootTag.addTag(childTag1)
        val document = Document(rootTag)

        document.removeAttributes("child", "attributeToRemove")

        assertFalse(childTag1.attributes.containsKey("attributeToRemove"))
    }
    //X-Path
}
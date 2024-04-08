import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


//Adicionar dependência JUnit
//org.junit.jupiter:junit-jupiter:5.10.2
class Tests {

    val document = Document(
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
    val document1 = Document(
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
        //falta adicionar para o caso em que não tem nada la dentro
    //escrita para ficheiro????

    //visitor
}
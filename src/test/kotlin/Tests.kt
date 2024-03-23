import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


//Adicionar dependência JUnit
//org.junit.jupiter:junit-jupiter:5.10.2
class Tests {

    val document = Document(
        CompositeTag(
            "plano",
            children = listOf(
                StringTag(
                    "Mestrado em Engenharia Informática",
                    content = "olá"
                )
            )
        )
    )

    @Test
    fun test0(){
        assertTrue(true)
    }

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
}
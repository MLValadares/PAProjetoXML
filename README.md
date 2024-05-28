# XML Manipulation Library
## Realizdo por:
Miguel Valadares - 98345

João Franco - 98975

## Overview

Esta biblioteca fornece uma API flexível e fácil de usar para gerar e manipular documentos XML em Kotlin.
A biblioteca suporta a criação de elementos XML, adição/remoção de atributos e elementos filhos, formatação de XML e muito mais.

## Características Principais

- **Criar e manipular elementos XML**
  - Adicionar/remover atributos e elementos filhos.
  - Aceder aos elementos pai e filho.
- **Pretty Print XML**
  - Gerar uma string XML bem formatada.
  - Escrever XML para um ficheiro.
- **Visitor Pattern**
  - Percorrer e manipular o documento XML utilizando visitantes.
- **Operações Globais ao Documento**
  - Adicionar, renomear e remover atributos e elementos globalmente.
- **XPath-like Queries**
  - Recupere fragmentos XML usando expressões simples do tipo XPaths.

## Estrutura da Biblioteca
### Classes e Interfaces Principais

- `Document`: Representa um documento XML e fornece métodos para manipulação global.
- `Tag`: Interface que representa uma tag de um documento XML.
- `CompositeTag`: Implementação de `Tag` que pode conter outras tags (tags compostas) e tags que não contenham nem outras tags, nem conteúdo de texto.
- `StringTag`: Implementação de `Tag` que contém um conteúdo de texto.
- `XmlBuilder`: Classe auxiliar para construção de documentos XML.
- Anotações (`NameChanger`, `Exclude`, `AsTextTag`, `FowardTags`, `ModifyString`, `Adapter`): Utilizadas para converter objetos Kotlin em tags XML.
- `StringTransformer` e `TagAdapter`: Interfaces para transformação de strings e adaptação de tags, respectivamente.

## Funcionalidades

### Document

- **`prettyString()`**: Retorna uma representação em string do documento XML.
- **`writeToFile(filePath: String)`**: Escreve o documento XML em um arquivo especificado.
- **`addAttributes(tagName: String, attributeKey: String, attributeValue: String)`**: Adiciona um atributo a todas as tags com um determinado nome.
- **`renameTags(oldName: String, newName: String)`**: Renomeia todas as tags com um determinado nome.
- **`renameAttributes(tagName: String, oldKey: String, newKey: String)`**: Renomeia todos os atributos de todas as tags com um determinado nome.
- **`removeTags(tagName: String)`**: Remove todas as tags com um determinado nome.
- **`removeAttributes(tagName: String, attributeKey: String)`**: Remove todos os atributos de todas as tags com um determinado nome.
- **`toXPath(expression: String)`**: Converte uma expressão XPath numa lista de tags.

### Tag

- **`addToParent(parent: CompositeTag)`**: Adiciona a tag a um pai.
- **`removeFromParent()`**: Remove a tag do pai.
- **`addAttribute(key: String, value: String)`**: Adiciona um atributo à tag.
- **`removeAttribute(key: String)`**: Remove um atributo da tag.
- **`renameAttribute(oldKey: String, newKey: String)`**: Renomeia um atributo da tag.
- **`modifyAttribute(key: String, value: String)`**: Modifica um atributo da tag.
- **`prettyString()`**: Retorna uma representação em string da tag.
- **`accept(visitor: (Tag) -> Boolean)`**: Aceita um visitante que processa a tag.
- **`rename(newName: String)`**: Renomeia a tag.

### CompositeTag e StringTag

- **CompositeTag**: Pode conter outras tags e é representada como uma tag com conteúdo aninhado.
- **StringTag**: Contém um conteúdo de texto e é representada como uma tag com texto simples.

### XmlBuilder

- **`tag(name: String, init: XmlBuilder.() -> Unit)`**: Adiciona uma tag filha.
- **`atr(name: String, value: String)`**: Adiciona um atributo à tag.
- **`textTag(text: String)`**: Adiciona uma tag de texto.
- **`build()`**: Constrói um documento XML a partir do `XmlBuilder`.
- **`buildTag()`**: Constrói uma tag a partir do `XmlBuilder`.

## Usage

### Basic API Usage
Creating an XML Document

```kotlin
fun main() {
    val doc = Document(
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
}
```

Pretty Printing XML
```kotlin
println(doc.prettyPrint())
```
```xml
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
```

Adding and Removing Attributes
```kotlin
val element = CompositeTag("test")
element.addAttribute(XmlAttribute("key", "value"))
element.removeAttribute("key")
```

Adding and Removing Child Elements
```kotlin
val parent = CompositeTag("parent")
val child = CompositeTag("child")
parent.addChild(child)
parent.removeChild("child")
```

### Mapeamento Classes-XML
```kotlin
@NameChanger("componente")
class ComponenteAvaliacao(
  val nome: String,
  val peso: Int)

@NameChanger("fuc")
class FUC(
  val codigo: String,
  @AsTextTag
  val nome: String,
  @AsTextTag
  val ects: Double,
  @Exclude
  val observacoes: String,
  @FowardTags
  val avaliacao: List<ComponenteAvaliacao>
)

val f = FUC(
  "M4310", "Programação Avançada", 6.0, "la la...",
  listOf(
    ComponenteAvaliacao("Quizzes", 20),
    ComponenteAvaliacao("Projeto", 80)
  )
)
val tag = f.toTag()

val doc = f.toDocument()
```

### ModifyString - Example
```kotlin
class AddPercentage: StringTransformer{
    override fun transform(value: String): String {
        return "$value%"
    }
}

class ComponenteAvaliacao(
  val nome: String,
  @ModifyString(AddPercentage::class)
  val peso: Int)
```

#### Adapter - Example
```kotlin
class FUCAdapter : TagAdapter {
    override fun adapt(tag: Tag): Tag { 
        val newTag = CompositeTag(tag.name, children = (tag as CompositeTag).children.toMutableList())
        for ((key, value) in tag.attributes) { 
            if (key != "nome") { 
                newTag.addAttribute(key, value)
            }
        }
        if (tag.attributes.containsKey("nome")) {
            newTag.addAttribute("nome", tag.attributes["nome"]!!)
        }

        return newTag
    }
}

@Adapter(FUCAdapter::class)
class FUC(
  val codigo: String,
  val nome: String,
  val ects: Double,
  val observacoes: String,
  val avaliacao: List<ComponenteAvaliacao>
)
```

### DSL Interno
```kotlin
val builder = XmlBuilder("plano").apply{
  tag("curso"){
    textTag("Mestrado em Engenharia Informática")
  }
  tag("fuc"){
    atr("codigo", "M4310")
    tag("nome"){
      textTag("Programação Avançada")
    }
    tag("ects"){
      textTag("6.0")
    }
    tag("avaliacao"){
      tag("componente"){
        atr("nome", "Quizzes")
        atr("Projeto", "80%")
      }
    }
  }
}
val tag = builder.buildTag()

val doc = builder.build()
```


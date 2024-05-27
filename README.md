# XML Manipulation Library
# TEST

## Overview

This library provides a flexible and easy-to-use API for generating and manipulating XML documents in Kotlin. The library supports creating XML elements, adding/removing attributes and child elements, pretty-printing XML, and more.

## Features

- **Create and Manipulate XML Elements**
  - Add/remove attributes and child elements.
  - Access parent and child elements.
- **Pretty Print XML**
  - Generate a well-formatted XML string.
  - Write XML to a file.
- **Visitor Pattern**
  - Traverse and manipulate the XML document using visitors.
- **Global Operations**
  - Add, rename, and remove attributes and elements globally.
- **XPath-like Queries**
  - Retrieve XML fragments using simple XPath-like expressions.

## Installation

Add the following dependency to your Kotlin project:

```kotlin
dependencies {
    implementation("com.example:xml-manipulation:1.0.0")
}
```

## Usage
Creating an XML Document

```kotlin
import com.example.xml.*

fun main() {
    val doc = XmlDocument(
        XmlElement("plano").apply {
            addChild(XmlElement("curso", textContent = "Mestrado em Engenharia Informática"))
            addChild(XmlElement("fuc", mutableListOf(XmlAttribute("codigo", "M4310"))).apply {
                addChild(XmlElement("nome", textContent = "Programação Avançada"))
                addChild(XmlElement("ects", textContent = "6.0"))
                addChild(XmlElement("avaliacao").apply {
                    addChild(XmlElement("componente", mutableListOf(XmlAttribute("nome", "Quizzes"), XmlAttribute("peso", "20%"))))
                    addChild(XmlElement("componente", mutableListOf(XmlAttribute("nome", "Projeto"), XmlAttribute("peso", "80%"))))
                })
            })
        }
    )
    println(doc.prettyPrint())
}
```

Adding and Removing Attributes
```kotlin
val element = XmlElement("test")
element.addAttribute(XmlAttribute("key", "value"))
element.removeAttribute("key")
```

Adding and Removing Child Elements
```kotlin
val parent = XmlElement("parent")
val child = XmlElement("child")
parent.addChild(child)
parent.removeChild("child")
```


Pretty Printing XML
```kotlin
val element = XmlElement("test").apply {
    addAttribute(XmlAttribute("key", "value"))
    textContent = "Hello, World!"
}
println(element.prettyPrint())
```


XML Document Example
```kotlin
val doc = XmlDocument(
    XmlElement("root").apply {
        addChild(XmlElement("child1", textContent = "Content1"))
        addChild(XmlElement("child2", textContent = "Content2"))
    }
)
println(doc.prettyPrint())
```

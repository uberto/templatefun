package com.ubertob

import com.ubertob.templatefun.RenderTemplate
import com.ubertob.templatefun.Tags
import com.ubertob.templatefun.Template
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TemplateTests {

    @Test
    fun `replace simple strings`() {
        val renderTemplate = RenderTemplate(
                Template("""{title} {surname}""")
            )

        renderTemplate.tagsToReplace.let { println(it) }


        val tags: Tags = { x ->
            when (x) {
                "{title}" -> "Mr"
                "{surname}" -> "Barbini"
                else -> null
            }
        }

        val text = renderTemplate(tags)

        val expected = "Mr Barbini"

        expectThat(text.value).isEqualTo(expected)
    }
}
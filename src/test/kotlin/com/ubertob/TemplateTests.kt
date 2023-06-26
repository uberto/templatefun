package com.ubertob

import com.ubertob.templatefun.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TemplateTests {

    @Test
    fun `regex tag test`() {

        val allTagsRegex = """\{(.*?)}""".toRegex()

        val templateStr = """{a} {b} {c} {d} {e}"""

        val matched = allTagsRegex.findAll(templateStr)
            .map { it.value.substring(1, 2) }
            .joinToString(separator = ",")

        expectThat(matched).isEqualTo("a,b,c,d,e")

    }

    @Test
    fun `regex block test`() {
        fun blockRegex(tagName: String) = """\{$tagName}(.*?)\{/$tagName}""".toRegex(RegexOption.DOT_MATCHES_ALL)

        val templateStr = """{a} {b} {c} {/b} {e}"""

        val matcher = blockRegex("b").find(templateStr)

        expectThat(matcher?.value).isEqualTo("{b} {c} {/b}")

    }


    @Test
    fun `regex all blocks test`() {
        fun blockRegex(tagName: String) = """\{$tagName}(.*?)\{/$tagName}""".toRegex(RegexOption.DOT_MATCHES_ALL)

        val templateStr = """{a} {b} {c} {/b} {e}"""

        val matcher = blockRegex("b").find(templateStr)

        expectThat(matcher?.value).isEqualTo("{b} {c} {/b}")

    }

    @Test
    fun `replace simple strings`() {
        val renderTemplate = RenderTemplate(
            Template("""{title} {surname}""")
        )

        val tags: Tags = { x ->
            when (x) {
                TagName("{title}") -> StringTag(TagName("{title}"), "Mr")
                TagName("{surname}") -> StringTag(TagName("{surname}"), "Barbini")
                else -> null.also { println("not found $x") }
            }
        }

        val text = renderTemplate(tags)

        val expected = "Mr Barbini"

        expectThat(text).isEqualTo(expected)
    }

    @Test
    fun `missing replacement tag`() {
        val fullNameTemplate = Template("""{title} {surname}""")

        val renderer = RenderTemplate(fullNameTemplate)

        val tags: Tags = { x ->
            when (x) {
                TagName("{title}") -> StringTag(TagName("{title}"), "Mr")
                else -> null
            }
        }

        val text = renderer(tags)

        val expected = "Mr "

        expectThat(text).isEqualTo(expected)
    }

}
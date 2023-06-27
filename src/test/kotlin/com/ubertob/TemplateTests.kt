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

    val titleTag = "title" tag "Mr"
    val surnameTag = "surname" tag "Barbini"

    @Test
    fun `replace simple strings`() {
        val renderTemplate = RenderTemplate(
            Template("""{title} {surname}""")
        )

        val tags = tags(titleTag, surnameTag)

        val text = renderTemplate(tags)

        val expected = "Mr Barbini"

        expectThat(text).isEqualTo(expected)
    }


    @Test
    fun `missing replacement tag`() {
        val fullNameTemplate = Template("""{title} {surname}""")

        val renderer = RenderTemplate(fullNameTemplate)

        val tags = tags(titleTag)

        val text = renderer(tags)

        val expected = "Mr "

        expectThat(text).isEqualTo(expected)
    }

    @Test
    fun `replace elements from a list`() {
        val renderer = RenderTemplate(
            """{title} {surname} order:
                |{items} {qty} of {itemname} {/items}
                |Total: {total} pieces
            """.trimMargin().asTemplate()
        )

        val itemsTags = listOf(
            " 4" to "glasses",
            "12" to "plates"
        ).map { (qty, name) ->
            tags(
                "qty" tag qty,
                "itemname" tag name
            )
        }

        val tags = tags(
            "title" tag "Mr",
            "surname" tag "Barbini",
            "total" tag "16",
            "items" tag itemsTags
        )

        val text = renderer(tags)

        val expected = """Mr Barbini order:
              |  4 of glasses 
              | 12 of plates 
              |Total: 16 pieces""".trimMargin()

        expectThat(text).isEqualTo(expected)
    }

    @Test
    fun `template with optional text`() {
        val renderTemplate = RenderTemplate(
            Template(
                """{title} {surname}, 
                |thanks for your order.
                |{isxmas}Merry Christmas!{/isxmas}""".trimMargin()
            )
        )

        val isxmas = "isxmas" tag true
        val tags = tags(titleTag, surnameTag, isxmas)

        val xmasText = renderTemplate(tags)

        val xmasExpected = """Mr Barbini, 
              |thanks for your order.
              |Merry Christmas!""".trimMargin()

        expectThat(xmasText).isEqualTo(xmasExpected)

        val isnotxmas = "isxmas" tag false

        val notXmasText = renderTemplate(tags(titleTag, surnameTag, isnotxmas))

        val notXmasExpected = """Mr Barbini, 
              |thanks for your order.
              |""".trimMargin()

        expectThat(notXmasText).isEqualTo(notXmasExpected)

    }

}


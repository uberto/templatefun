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

    val titleTag = StringTag("title".asTagName(), "Mr")
    val surnameTag = StringTag("surname".asTagName(), "Barbini")

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

    private fun tags(vararg tags: Tag): Tags =
        tags.map { it.name to it }.toMap()::get

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
        val renderTemplate = RenderTemplate(
            Template(
                """{title} {surname} order:
                |{items} {qty} of {itemname} {/items}
                |Total: {total}
            """.trimMargin()
            )
        )

        val items = listOf(
            4 to "glasses",
            12 to "plates"
        )

        val itemsTags = items.map { (qty, name) ->
            tags(
                StringTag("qty".asTagName(), qty.toString()),
                StringTag("itemname".asTagName(), name)
            )
        }
        val listTag = ListTag("items".asTagName(), itemsTags)
        val totalTag = StringTag("total".asTagName(), "16")


        val tags = tags(titleTag, surnameTag, totalTag, listTag)

        val text = renderTemplate(tags)

        val expected = """Mr Barbini order:
              | 4 of glasses 
              | 12 of plates 
              |Total: 16""".trimMargin()

        expectThat(text).isEqualTo(expected)
    }


}
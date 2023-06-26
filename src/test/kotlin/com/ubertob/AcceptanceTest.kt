package com.ubertob

import com.ubertob.templatefun.RenderTemplate
import com.ubertob.templatefun.Tags
import com.ubertob.templatefun.asTemplate
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class AcceptanceTest {

    @Test
    fun `template work as expected`() {
        val template = """
    Dear {title} {surname},
    we would like to bring to your attention these task due soon:
    {tasks}  {id} - {taskname} which is due by {due}{/tasks}
    Thank you very much {name}.
""".trimIndent() //the text template

        val tags: Tags = { null } //tags to be replaced like "title"= "Mr"...

        val text = template.renderWith(tags) //the actual magic

        val expected = """
    Dear Mr Barbini,
    we would like to bring to your attention these task due soon:
      1 - buy the paint which is due by today
      2 - paint the wall which is due by tomorrow
    Thank you very much Uberto.
""".trimIndent()

        expectThat(text).isEqualTo(expected) //test the result
    }
}

private fun String.renderWith(tags: Tags): String =
    RenderTemplate(this.asTemplate())(tags)

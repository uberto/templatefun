package com.ubertob

import com.ubertob.templatefun.*
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

        val tasks = listOf(
            tags("id" tag "1", "taskname" tag "buy the paint", "due" tag "today"),
            tags("id" tag "2", "taskname" tag "paint the wall", "due" tag "tomorrow")
        )
        val tags = tags(
            "title" tag "Mr",
            "surname" tag "Barbini",
            "name" tag "Uberto",
            "tasks" tag tasks
        )

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

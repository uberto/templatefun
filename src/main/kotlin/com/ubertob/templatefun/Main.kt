package com.ubertob.templatefun

import com.ubertob.kondor.outcome.Outcome
import com.ubertob.kondor.outcome.OutcomeError


data class Text(val value: String)
fun String.asText(): Text = Text(this)

data class Template(val value: String)

typealias Tags = (String) -> String?

typealias Renderer = (Tags) -> Text

data class RenderTemplate(val template: Template) : Renderer {

    val tagRegex = """\{(.*?)}""".toRegex()

    override fun invoke(tags: Tags): Text =
        tagRegex.replace(template.value){
            mr -> tags(mr.value).orEmpty()
        }.asText()

//        tagsToReplace.fold(
//            template.value
//        ) { text, tag -> text.replace(tag, tags(tag).orEmpty()) }
//            .asText()


    companion object {
        fun String.renderWith(tags: Tags): Text =
            RenderTemplate(Template(this))(tags)
    }
}

data class TemplateError(override val msg: String) : OutcomeError

typealias TemplateOutcome = Outcome<TemplateError, Template>

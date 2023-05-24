package com.ubertob.templatefun

import com.ubertob.kondor.outcome.Outcome
import com.ubertob.kondor.outcome.OutcomeError


data class Text(val value: String)
data class Template(val value: String)

typealias Tags = (String) -> String?

data class RenderTemplate(val template: Template) : (Tags) -> Text {

    val tagsToReplace = findAllTags(template)

    override fun invoke(tags: Tags): Text =

        tagsToReplace.fold(
            template.value
        ) { text, tag -> text.replace(tag, tags(tag).orEmpty()) }
            .asText()


    private fun String.asText(): Text = Text(this)

    companion object {
        fun String.renderWith(tags: Tags): Text =
            RenderTemplate(Template(this))(tags)
    }
}


val tagRegex = """\{(.*?)}""".toRegex()

data class TemplateError(override val msg: String) : OutcomeError

typealias TemplateOutcome = Outcome<TemplateError, Template>

//private fun Template.checkUnchanged(): TemplateOutcome =
//    failUnless(tagRegex.containsMatchIn(this)) {
//        TemplateError("Mappings missing for tags: ${findAllTags(this)}")
//    }

private fun findAllTags(text: Template): Set<String> =
    tagRegex.findAll(text.value).map(MatchResult::value).toSet()

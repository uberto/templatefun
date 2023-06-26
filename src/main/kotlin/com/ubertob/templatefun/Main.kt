package com.ubertob.templatefun

import com.ubertob.kondor.outcome.Outcome
import com.ubertob.kondor.outcome.OutcomeError


data class Template(val text: String)
data class TagName(val value: String)

typealias Renderer = (Tags) -> String

typealias Tags = (TagName) -> Tag?

fun String.asTemplate() = Template(this)

sealed class Tag : (Template) -> Template {
    abstract val name: TagName
}

data class StringTag(override val name: TagName, val text: String?) : Tag() {
    override fun invoke(template: Template): Template =
        template.text.replace(name.value, text ?: "")
            .asTemplate()
}

data class ListTag(override val name: TagName, val subTags: List<Tags>) : Tag() {
    override fun invoke(template: Template): Template = TODO()
}

//data class OptionalTag(val bool: Boolean) : Tag()


data class RenderTemplate(val template: Template) : Renderer {

    val tagRegex = """\{(.*?)}""".toRegex()

    val tagsToReplace =
        tagRegex.findAll(template.text)
            .map { TagName(it.value) }.toSet()

    override fun invoke(tags: Tags): String =
        tagsToReplace.fold(
            template
        ) { currTempl, tagName -> replaceTag(currTempl, tagName, tags(tagName)) }
            .text


    companion object {
        fun String.renderWith(tags: Tags): String =
            RenderTemplate(asTemplate())(tags)
    }
}

private fun replaceTag(currTempl: Template, tagName: TagName, tag: Tag?): Template =
    when (tag) {
        null -> removeMissingTag(currTempl, tagName)
        else -> tag(currTempl)
    }

private fun removeMissingTag(
    currTempl: Template,
    tagName: TagName
) = currTempl.text.replace(tagName.value, "").asTemplate()

data class TemplateError(override val msg: String) : OutcomeError

typealias TemplateOutcome = Outcome<TemplateError, Template>

package com.ubertob.templatefun

import com.ubertob.kondor.outcome.Outcome
import com.ubertob.kondor.outcome.OutcomeError


data class Template(val text: String)

fun String.asTemplate() = Template(this)
data class TagName(val value: String)

fun String.asTagName() = TagName("{$this}")


typealias Renderer = (Tags) -> String

typealias Tags = (TagName) -> Tag?

sealed class Tag : (Template) -> Template {
    abstract val name: TagName
}

data class StringTag(override val name: TagName, val text: String?) : Tag() {
    override fun invoke(template: Template): Template =
        template.text.replace(name.value, text ?: "")
            .asTemplate()
}

data class ListTag(override val name: TagName, val subTags: List<Tags>) : Tag() {

    val strippedTag = name.value.drop(1).dropLast(1)
    private fun generateMulti(subTemplate: Template): String =
        subTags.joinToString(separator = "\n", transform = {
            RenderTemplate(subTemplate)(it)
        })

    private val tagRegex =
        """\{$strippedTag}(.*?)\{/$strippedTag}""".toRegex(RegexOption.DOT_MATCHES_ALL)

    private fun String.stripTags(tagName: TagName): String =
        substring(tagName.value.length, length - tagName.value.length - 1)

    override fun invoke(template: Template): Template =
        template.text.replace(tagRegex) {
            generateMulti(it.value.stripTags(name).asTemplate())
        }.asTemplate()

}

//data class OptionalTag(val bool: Boolean) : Tag()


data class RenderTemplate(val template: Template) : Renderer {

    val tagRegex = """\{(.*?)}""".toRegex()

    val tagsToReplace =
        tagRegex.findAll(template.text)
            .map { TagName(it.value) }.toSet()

    private fun replaceTag(currTempl: Template, tagName: TagName, tag: Tag?): Template =
        when (tag) {
            null -> removeMissingTag(currTempl, tagName)
            else -> tag(currTempl)
        }

    private fun removeMissingTag(currTempl: Template, tagName: TagName) =
        currTempl.text.replace(tagName.value, "").asTemplate()

    override fun invoke(tags: Tags): String =
        tagsToReplace.fold(template) { currTempl, tagName ->
            replaceTag(currTempl, tagName, tags(tagName))
        }.text

}


data class TemplateError(override val msg: String) : OutcomeError

typealias TemplateOutcome = Outcome<TemplateError, Template>

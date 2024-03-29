package com.ubertob.templatefun

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

    private val strippedTag = name.value.drop(1).dropLast(1)
    private val tagRegex = """\{$strippedTag}(.*?)\{/$strippedTag}"""
        .toRegex(RegexOption.DOT_MATCHES_ALL)

    private fun generateMulti(subTemplate: Template): String =
        subTags.joinToString(separator = "\n") {
            RenderTemplate(subTemplate)(it)
        }

    private fun MatchResult.asSubtemplate(): Template =
        value.drop(name.value.length)
            .dropLast(name.value.length + 1).asTemplate()

    override fun invoke(template: Template): Template =
        template.text.replace(tagRegex) {
            generateMulti(it.asSubtemplate())
        }.asTemplate()

}


data class OptionalTag(override val name: TagName, val show: Boolean) : Tag() {
    private val strippedTag = name.value.drop(1).dropLast(1)
    private val tagRegex = """\{$strippedTag}(.*?)\{/$strippedTag}"""
        .toRegex(RegexOption.DOT_MATCHES_ALL)

    private fun MatchResult.stripTags(): String =
        value.drop(name.value.length)
            .dropLast(name.value.length + 1)

    override fun invoke(template: Template): Template =
        template.text.replace(tagRegex) {
            if (show) it.stripTags() else ""
        }.asTemplate()
}


data class RenderTemplate(val template: Template) : Renderer {

    val tagRegex = """\{(.*?)}""".toRegex()

    val tagsToReplace = tagRegex.findAll(template.text)
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

fun tags(vararg tags: Tag): Tags =
    tags.map { it.name to it }.toMap()::get

infix fun String.tag(value: String) = StringTag(this.asTagName(), value)
infix fun String.tag(value: List<Tags>) = ListTag(this.asTagName(), value)
infix fun String.tag(show: Boolean) = OptionalTag(this.asTagName(), show)


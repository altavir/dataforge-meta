package hep.dataforge.meta

import hep.dataforge.names.*

/**
 * Get all items matching given name. The index of the last element, if present is used as a [Regex],
 * against which indexes of elements are matched.
 */
public fun Meta.getIndexed(name: Name): Map<String?, MetaItem<*>> {
    val root = when (name.length) {
        0 -> error("Can't use empty name for 'getIndexed'")
        1 -> this
        else -> this[name.cutLast()].node ?: return emptyMap()
    }

    val (body, index) = name.lastOrNull()!!
    return if (index == null) {
        root.items.filter { it.key.body == body }.mapKeys { it.key.index }
    } else {
        val regex = index.toRegex()
        root.items.filter { it.key.body == body && (regex.matches(it.key.index ?: "")) }
            .mapKeys { it.key.index }
    }
}

public fun Meta.getIndexed(name: String): Map<String?, MetaItem<*>> = this@getIndexed.getIndexed(name.toName())

/**
 * Get all items matching given name.
 */
@Suppress("UNCHECKED_CAST")
public fun <M : TypedMeta<M>> M.getIndexed(name: Name): Map<String, MetaItem<M>> =
    (this as Meta).getIndexed(name) as Map<String, MetaItem<M>>

public fun <M : TypedMeta<M>> M.getIndexed(name: String): Map<String, MetaItem<M>> =
    getIndexed(name.toName())
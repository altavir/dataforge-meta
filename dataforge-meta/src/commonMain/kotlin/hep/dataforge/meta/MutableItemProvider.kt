package hep.dataforge.meta

import hep.dataforge.names.*
import hep.dataforge.values.Value

public interface MutableItemProvider : ItemProvider {
    public fun setItem(name: Name, item: MetaItem<*>?)
}

public operator fun MutableItemProvider.set(name: Name, item: MetaItem<*>?): Unit = setItem(name, item)

public operator fun MutableItemProvider.set(name: Name, value: Value?): Unit = set(name, value?.asMetaItem())

public operator fun MutableItemProvider.set(name: Name, meta: Meta?): Unit = set(name, meta?.asMetaItem())

public operator fun MutableItemProvider.set(key: String, item: MetaItem<*>?): Unit = set(key.toName(), item)

public operator fun MutableItemProvider.set(key: String, meta: Meta?): Unit = set(key, meta?.asMetaItem())

@Suppress("NOTHING_TO_INLINE")
public inline fun MutableItemProvider.remove(name: Name): Unit = setItem(name, null)

@Suppress("NOTHING_TO_INLINE")
public inline fun MutableItemProvider.remove(name: String): Unit = remove(name.toName())

/**
 * Universal unsafe set method
 */
public operator fun MutableItemProvider.set(name: Name, value: Any?) {
    when (value) {
        null -> remove(name)
        is MetaItem<*> -> set(name, value)
        is Meta -> set(name, value)
        is Configurable -> set(name, value.config)
        else -> set(name, Value.of(value))
    }
}

public operator fun MutableItemProvider.set(name: NameToken, value: Any?): Unit =
    set(name.asName(), value)

public operator fun MutableItemProvider.set(key: String, value: Any?): Unit =
    set(key.toName(), value)

public operator fun MutableItemProvider.set(key: String, index: String, value: Any?): Unit =
    set(key.toName().withIndex(index), value)


/* Same name siblings generation */

public fun MutableItemProvider.setIndexedItems(
    name: Name,
    items: Iterable<MetaItem<*>>,
    indexFactory: (MetaItem<*>, index: Int) -> String = { _, index -> index.toString() },
) {
    val tokens = name.tokens.toMutableList()
    val last = tokens.last()
    items.forEachIndexed { index, meta ->
        val indexedToken = NameToken(last.body, last.index + indexFactory(meta, index))
        tokens[tokens.lastIndex] = indexedToken
        set(Name(tokens), meta)
    }
}

public fun MutableItemProvider.setIndexed(
    name: Name,
    metas: Iterable<Meta>,
    indexFactory: (Meta, index: Int) -> String = { _, index -> index.toString() },
) {
    setIndexedItems(name, metas.map { MetaItem.NodeItem(it) }) { item, index -> indexFactory(item.node!!, index) }
}

public operator fun MutableItemProvider.set(name: Name, metas: Iterable<Meta>): Unit = setIndexed(name, metas)
public operator fun MutableItemProvider.set(name: String, metas: Iterable<Meta>): Unit =
    setIndexed(name.toName(), metas)

/**
 * Update existing mutable node with another node. The rules are following:
 *  * value replaces anything
 *  * node updates node and replaces anything but node
 *  * node list updates node list if number of nodes in the list is the same and replaces anything otherwise
 */
public fun MutableItemProvider.update(meta: Meta) {
    meta.valueSequence().forEach { (name, value) -> set(name, value) }
}

/**
 * Create a mutable item provider that uses given provider for default values if those are not found in this provider.
 * Changes are propagated only to this provider.
 */
public fun MutableItemProvider.withDefault(default: ItemProvider): MutableItemProvider =
    if (default is Meta && default.isEmpty()) {
        //Optimize for use with empty default
        this
    } else object : MutableItemProvider {
        override fun setItem(name: Name, item: MetaItem<*>?) {
            this@withDefault.setItem(name, item)
        }

        override fun getItem(name: Name): MetaItem<*>? = this@withDefault.getItem(name) ?: default.getItem(name)
    }

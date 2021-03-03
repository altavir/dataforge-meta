package space.kscience.dataforge.meta.descriptors

import space.kscience.dataforge.meta.*
import space.kscience.dataforge.names.NameToken

/**
 * A [Meta] that is constructed from [NodeDescriptor]
 */
private class DescriptorMeta(val descriptor: NodeDescriptor) : Meta, MetaBase() {
    override val items: Map<NameToken, MetaItem>
        get() = buildMap {
            descriptor.items.forEach { (token, descriptorItem) ->
                val item = descriptorItem.defaultItem()
                if (item != null) {
                    put(NameToken(token), item)
                }
            }
        }
}

/**
 * Generate a laminate representing default item set generated by this descriptor
 */
public fun NodeDescriptor.defaultMeta(): Laminate = Laminate(default, DescriptorMeta(this))

/**
 * Build a default [MetaItemNode] from this node descriptor
 */
internal fun NodeDescriptor.defaultItem(): MetaItemNode<*> =
    MetaItemNode(defaultMeta())

/**
 * Build a default [MetaItemValue] from this descriptor
 */
internal fun ValueDescriptor.defaultItem(): MetaItemValue? {
    return MetaItemValue(default ?: return null)
}

/**
 * Build a default [TypedMetaItem] from descriptor.
 */
public fun ItemDescriptor.defaultItem(): MetaItem? {
    return when (this) {
        is ValueDescriptor -> defaultItem()
        is NodeDescriptor -> defaultItem()
    }
}
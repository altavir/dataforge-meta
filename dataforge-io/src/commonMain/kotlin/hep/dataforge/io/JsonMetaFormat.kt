@file:Suppress("UNUSED_PARAMETER")

package hep.dataforge.io


import hep.dataforge.context.Context
import hep.dataforge.io.IOFormat.Companion.NAME_KEY
import hep.dataforge.meta.Meta
import hep.dataforge.meta.descriptors.NodeDescriptor
import hep.dataforge.meta.node
import hep.dataforge.meta.toJson
import hep.dataforge.meta.toMetaItem
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.readByteArray
import kotlinx.io.text.writeUtf8String
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * A Json format for Meta representation
 */
public class JsonMetaFormat(private val json: Json = DEFAULT_JSON) : MetaFormat {

    override fun writeMeta(output: Output, meta: Meta, descriptor: NodeDescriptor?) {
        val jsonObject = meta.toJson(descriptor)
        output.writeUtf8String(this.json.encodeToString(JsonObject.serializer(), jsonObject))
    }

    override fun toMeta(): Meta = Meta {
        NAME_KEY put name.toString()
    }

    override fun readMeta(input: Input, descriptor: NodeDescriptor?): Meta {
        val str = input.readByteArray().decodeToString()
        val jsonElement = json.parseToJsonElement(str)
        val item = jsonElement.toMetaItem(descriptor)
        return item.node ?: Meta.EMPTY
    }

    public companion object : MetaFormatFactory {
        public val DEFAULT_JSON: Json = Json { prettyPrint = true }

        override fun invoke(meta: Meta, context: Context): MetaFormat = default

        override val shortName: String = "json"
        override val key: Short = 0x4a53//"JS"

        private val default = JsonMetaFormat()

        override fun writeMeta(output: Output, meta: Meta, descriptor: NodeDescriptor?): Unit =
            default.run { writeMeta(output, meta, descriptor) }

        override fun readMeta(input: Input, descriptor: NodeDescriptor?): Meta =
            default.run { readMeta(input, descriptor) }
    }
}

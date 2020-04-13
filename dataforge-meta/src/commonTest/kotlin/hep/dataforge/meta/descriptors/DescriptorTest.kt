package hep.dataforge.meta.descriptors

import hep.dataforge.values.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DescriptorTest {

    val descriptor = NodeDescriptor {
        node("aNode") {
            info = "A root demo node"
            value("b") {
                info = "b number value"
                type(ValueType.NUMBER)
            }
            node("otherNode") {
                value("otherValue") {
                    type(ValueType.BOOLEAN)
                    default(false)
                    info = "default value"
                }
            }
        }
    }

    @Test
    fun testAllowedValues() {
        val child = descriptor["aNode.b"]
        assertNotNull(child)
        val allowed = descriptor.nodes["aNode"]?.values?.get("b")?.allowedValues
        assertEquals(emptyList(), allowed)
    }
}
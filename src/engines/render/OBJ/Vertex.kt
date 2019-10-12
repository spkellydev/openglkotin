package engines.render.OBJ

import org.lwjgl.util.vector.Vector3f

class Vertex(val index: Int, val position: Vector3f) {
    var textureIndex = NO_INDEX
    var normalIndex = NO_INDEX
    var duplicateVertex: Vertex? = null
    val length: Float

    val isSet: Boolean
        get() = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    init {
        this.length = position.length()
    }

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex
    }

    companion object {
        private val NO_INDEX = -1
    }
}
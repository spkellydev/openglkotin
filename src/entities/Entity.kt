package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

class Entity(model: TexturedModel, position: Vector3f, rotX: Float,
             rotY: Float, rotZ: Float, scale: Float) {
    val model: TexturedModel = model
    val position: Vector3f = position
    var rotX: Float = rotX
        private set(value) { field = value }
    var rotY: Float = rotY
        private set(value) { field = value }
    var rotZ: Float = rotZ
        private set(value) { field = value }
    var scale: Float = scale
        private set(value) { field = value }

    fun increasePosition(dx: Float, dy: Float, dz: Float) {
        with(position) {
            x += dx
            y += dy
            z += dz
        }
    }

    fun increaseRotation(dx: Float, dy: Float, dz: Float) {
        rotX += dx
        rotY += dy
        rotZ += dz
    }
}
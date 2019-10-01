package entities

import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f

class Camera {
    val position: Vector3f = Vector3f(0f,0f,0f)
    var pitch: Float = 0f
    var yaw: Float = 0f
    var roll: Float = 0f

    init {

    }

    fun move() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= 0.2f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += 0.2f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= 0.2f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z += 0.2f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            position.y += 0.2f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            position.y -= 0.2f
        }
    }
}
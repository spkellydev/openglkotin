package engineTester

import engines.render.*
import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import shaders.StaticShader
import textures.ModelTexture
import java.security.Key

class GameLoop {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            DisplayManager.createDisplay()
            Display.setTitle("First Display")
            val loader = Loader()

            val model = OBJLoader.loadModel("stall", loader)
            val texturedModel = TexturedModel(model, ModelTexture(loader.loadTexture("stallTexture")))
            val texture = texturedModel.texture
            texture.shineDampener = 10f
            texture.reflectivity = 1f

            val entity = Entity(texturedModel, Vector3f(0f, 0f, -50f),
                0f, 0f, 0f, 1f)
            val light = Light(Vector3f(200f, 200f, 100f), Vector3f(1f,1f,1f))
            val camera = Camera()
            val masterRenderer = MasterRenderer()

            while(!Display.isCloseRequested()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                    entity.increaseRotation(0f, 0.3f, 0f)

                camera.move()
                masterRenderer.processEntity(entity)
                masterRenderer.render(light, camera)
                DisplayManager.updateDisplay()

                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break
            }

            masterRenderer.dispose()
            loader.dispose()
            DisplayManager.closeDisplay()
        }
    }
}
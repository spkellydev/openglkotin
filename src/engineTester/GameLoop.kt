package engineTester

import engines.render.DisplayManager
import engines.render.Loader
import engines.render.OBJLoader
import engines.render.Renderer
import entities.Camera
import entities.Entity
import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import shaders.StaticShader
import textures.ModelTexture

class GameLoop {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            DisplayManager.createDisplay()
            Display.setTitle("First Display")
            val loader = Loader()
            val shader = StaticShader()
            val renderer = Renderer(shader)



            val model = OBJLoader.loadModel("stall", loader)
            val texture = ModelTexture(loader.loadTexture("stallTexture"))
            val texturedModel = TexturedModel(model, texture)

            val entity = Entity(texturedModel, Vector3f(0f, 0f, -50f),
                0f, 0f, 0f, 1f)
            val camera = Camera()

            while(!Display.isCloseRequested()) {
                entity.increaseRotation(0f, 1f, 0f)
                camera.move()
                renderer.prepare()

                shader.start()
                shader.loadViewMatrix(camera)
                renderer.render(entity, shader)
                shader.stop()

                DisplayManager.updateDisplay()

                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break
            }

            shader.dispose()
            loader.dispose()
            DisplayManager.closeDisplay()
        }
    }
}
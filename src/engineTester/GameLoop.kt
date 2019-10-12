package engineTester

import engines.render.*
import engines.render.OBJ.OBJLoader
import engines.terrains.Terrain
import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import textures.ModelTexture

class GameLoop {
    companion object {
        val entities = ArrayList<Entity>()

        @JvmStatic
        fun main(args: Array<String>) {
            DisplayManager.createDisplay()
            Display.setTitle("First Display")
            val loader = Loader()
            loadMany(loader, "grassModel", "grassTexture")
            loadMany(loader, "tree", "tree")

            val light = Light(Vector3f(3000f, 2000f, 2000f), Vector3f(1f,1f,1f))

            val terrain = Terrain(0, 0, loader, ModelTexture(loader.loadTexture("grass")))
            val terrain2 = Terrain(1, 0, loader, ModelTexture(loader.loadTexture("grass")))
            val terrain3 = Terrain(2, 0, loader, ModelTexture(loader.loadTexture("grass")))

            val camera = Camera()
            val masterRenderer = MasterRenderer()
            with(camera.position) { y += 5f; z += 50f }

            while(!Display.isCloseRequested()) {
                camera.move()
                masterRenderer.processTerrain(terrain)
                masterRenderer.processTerrain(terrain2)
                masterRenderer.processTerrain(terrain3)
                for (e in entities)
                    masterRenderer.processEntity(e)
                masterRenderer.render(light, camera)
                DisplayManager.updateDisplay()

                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break
            }

            masterRenderer.dispose()
            loader.dispose()
            DisplayManager.closeDisplay()
        }

        private fun loadMany(loader: Loader, obj: String, texture: String) {
            val modelData = OBJLoader.loadOBJ(obj)
            val model = loader.loadToVAO(modelData.vertices, modelData.textureCoords, modelData.normals, modelData.indices)
            val texturedModel = TexturedModel(model, ModelTexture(loader.loadTexture(texture)))
            with(texturedModel.texture) {
                useFakeLighting = true
                hasTrasparency = true
                shineDampener = 10f
                reflectivity = 0f
            }

            for(i in 0..1000) {
                val entity = Entity(texturedModel, Vector3f((0..500).random().toFloat(), 0f, (0..500).random().toFloat()),
                    0f, 0f, 0f, 1f)
                entities.add(entity)
            }
        }
    }

}
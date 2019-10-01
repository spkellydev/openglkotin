package engines.render

import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import shaders.StaticShader

class MasterRenderer {
    private val shader: StaticShader = StaticShader()
    private val renderer: Renderer = Renderer(shader)

    private val entities = HashMap<TexturedModel, ArrayList<Entity>>()

    fun render(sun: Light, camera: Camera) {
        renderer.prepare()
        shader.start()
        shader.loadLight(sun)
        shader.loadViewMatrix(camera)
        renderer.render(entities)
        shader.stop()
        entities.clear()
    }

    fun processEntity(entity: Entity) {
        val entityModel = entity.model
        val batch = entities[entityModel]
        if (batch != null) {
            batch.add(entity)
        } else {
            val newBatch = ArrayList<Entity>()
            newBatch.add(entity)
            entities.put(entityModel, newBatch)
        }
    }

    fun dispose() {
        shader.dispose()
    }
}
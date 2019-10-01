package engines.render

import engines.shader.StaticShader
import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.*
import org.lwjgl.util.vector.Matrix4f
import utils.Maths

class EntityRenderer(private val shader: StaticShader, projectionMatrix: Matrix4f) {
    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(entites: HashMap<TexturedModel, ArrayList<Entity>>) {
        for (model: TexturedModel in entites.keys) {
            prepareTexturedModel(model)
            val batch = entites[model]!!
            for (e: Entity in batch) {
                prepareInstance(e)
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.rawModel.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            }
            unbindTexturedModel()
        }
    }

    private fun prepareTexturedModel(texturedModel: TexturedModel) {
        val model = texturedModel.rawModel
        GL30.glBindVertexArray(model.vaoID)
        GL20.glEnableVertexAttribArray(0) // positions
        GL20.glEnableVertexAttribArray(1) // textureCoords
        GL20.glEnableVertexAttribArray(2) // normals
        val texture = texturedModel.texture
        if (texture.hasTrasparency) {
            MasterRenderer.disableCulling()
        }
        shader.loadFakeLighting(texture.useFakeLighting)
        shader.loadShine(texture.shineDampener, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.texture.textureID)
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTranformationMatrix(
            entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        shader.loadTransformationMatrix(transformationMatrix)
    }

    private fun unbindTexturedModel() {
        MasterRenderer.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}
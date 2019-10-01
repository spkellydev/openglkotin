package engines.render

import engines.terrains.Terrain
import engines.terrains.TerrainShader
import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class TerrainRenderer(private val shader: TerrainShader, projectionMatrix: Matrix4f) {
    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(terrains: List<Terrain>)  {
        for (terrain in terrains) {
            prepareTerrain(terrain)
            loadModelMatrix(terrain)
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.model.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            unbindTexturedModel()
        }
    }

    private fun prepareTerrain(terrain: Terrain) {
        val model = terrain.model
        GL30.glBindVertexArray(model.vaoID)
        GL20.glEnableVertexAttribArray(0) // positions
        GL20.glEnableVertexAttribArray(1) // textureCoords
        GL20.glEnableVertexAttribArray(2) // normals
        val texture = terrain.texture
        shader.loadShine(texture.shineDampener, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.texture.textureID)
    }

    private fun loadModelMatrix(terrain: Terrain) {
        val transformationMatrix = Maths.createTranformationMatrix(
            Vector3f(terrain.x, 0f, terrain.z), 0f, 0f, 0f, 1f)
        shader.loadTransformationMatrix(transformationMatrix)
    }

    private fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}
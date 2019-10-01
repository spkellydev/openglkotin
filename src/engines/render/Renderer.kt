package engines.render

import entities.Entity
import models.RawModel
import models.TexturedModel
import org.lwjgl.opengl.*
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import utils.Maths

class Renderer(shader: StaticShader) {
    private lateinit var projectionMatrix: Matrix4f
    private val shader: StaticShader = shader

    init {
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        createProjectionMatrix()
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(0.5f, 0f, 0f, 0f)
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

    fun prepareTexturedModel(texturedModel: TexturedModel) {
        val model = texturedModel.rawModel
        GL30.glBindVertexArray(model.vaoID)
        GL20.glEnableVertexAttribArray(0) // positions
        GL20.glEnableVertexAttribArray(1) // textureCoords
        GL20.glEnableVertexAttribArray(2) // normals
        val texture = texturedModel.texture
        shader.loadShine(texture.shineDampener, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.texture.textureID)
    }

    fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTranformationMatrix(
            entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        shader.loadTransformationMatrix(transformationMatrix)
    }

    fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    private fun createProjectionMatrix() {
        val aspectRatio = Display.getWidth() / Display.getHeight().toFloat()
        val yScale = ((1f / Math.tan(Math.toRadians((FOV / 2f).toDouble()))) * aspectRatio).toFloat()
        val xScale = yScale / aspectRatio
        val frustumLength = FAR_PLANE - NEAR_PLANE

        projectionMatrix = Matrix4f()
        projectionMatrix.m00 = xScale
        projectionMatrix.m11 = yScale
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength)
        projectionMatrix.m23 = -1f
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength)
        projectionMatrix.m33 = 0f
    }

    companion object {
        private const val FOV: Float = 70f
        private const val NEAR_PLANE: Float = 0.1f
        private const val FAR_PLANE: Float = 100f
    }
}
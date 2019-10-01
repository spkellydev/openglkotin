package engines.render

import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader

class MasterRenderer {
    private val shader: StaticShader = StaticShader()
    private lateinit var projectionMatrix: Matrix4f
    private val entityRenderer: EntityRenderer
    private val entities = HashMap<TexturedModel, ArrayList<Entity>>()

    init {
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        createProjectionMatrix()
        entityRenderer = EntityRenderer(shader, projectionMatrix)
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(0.5f, 0f, 0f, 0f)
    }

    fun render(sun: Light, camera: Camera) {
        prepare()
        shader.start()
        shader.loadLight(sun)
        shader.loadViewMatrix(camera)
        entityRenderer.render(entities)
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

    fun dispose() {
        shader.dispose()
    }

    companion object {
        private const val FOV: Float = 70f
        private const val NEAR_PLANE: Float = 0.1f
        private const val FAR_PLANE: Float = 100f
    }
}
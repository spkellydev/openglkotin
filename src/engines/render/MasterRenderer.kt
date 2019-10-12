package engines.render

import engines.shader.StaticShader
import engines.terrains.Terrain
import engines.terrains.TerrainShader
import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f

class MasterRenderer {
    private val shader: StaticShader = StaticShader()
    private lateinit var projectionMatrix: Matrix4f
    private val entityRenderer: EntityRenderer
    private val terrainRenderer: TerrainRenderer
    private val terrainShader: TerrainShader = TerrainShader()

    private val entities = HashMap<TexturedModel, ArrayList<Entity>>()
    private val terrains = ArrayList<Terrain>()

    init {
        enableCulling()
        createProjectionMatrix()
        entityRenderer = EntityRenderer(shader, projectionMatrix)
        terrainRenderer = TerrainRenderer(terrainShader, projectionMatrix)
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(RED, GREEN, BLUE, 0f)
    }

    fun render(sun: Light, camera: Camera) {
        prepare()
        shader.start()
        shader.loadSkyColor(RED, GREEN, BLUE)
        shader.loadLight(sun)
        shader.loadViewMatrix(camera)
        entityRenderer.render(entities)
        shader.stop()

        terrainShader.start()
        terrainShader.loadLight(sun)
        terrainShader.loadViewMatrix(camera)
        terrainRenderer.render(terrains)
        terrainShader.stop()

        terrains.clear()
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

    fun processTerrain(terrain: Terrain) {
        terrains.add(terrain)
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
        terrainShader.dispose()
    }

    companion object {
        private const val FOV: Float = 70f
        private const val NEAR_PLANE: Float = 0.1f
        private const val FAR_PLANE: Float = 100f
        private const val RED = 0.5f
        private const val BLUE = 0.5f
        private const val GREEN = 0.5f

        fun enableCulling() {
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glCullFace(GL11.GL_BACK)
        }

        fun disableCulling() {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }
}
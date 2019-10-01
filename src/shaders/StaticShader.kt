package shaders

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import utils.Maths
import java.lang.Exception

class StaticShader: ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var locationTransformationMatrix: Int? = null
    private var locationProjectionMatrix: Int? = null
    private var locationViewMatrix: Int? = null

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoordinates")
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix")
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix")
        locationViewMatrix = super.getUniformLocation("viewMatrix")
    }

    fun loadTransformationMatrix(matrix4f: Matrix4f) {
        super.loadMatrix(locationTransformationMatrix!!, matrix4f)
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        super.loadMatrix(locationProjectionMatrix!!, projection)
    }

    fun loadViewMatrix(camera: Camera) {
        super.loadMatrix(locationViewMatrix!!, Maths.createViewMatrix(camera))
    }

    companion object {
        private const val VERTEX_FILE: String = "src/shaders/shader.vert"
        private const val FRAGMENT_FILE: String = "src/shaders/shader.frag"
    }
}
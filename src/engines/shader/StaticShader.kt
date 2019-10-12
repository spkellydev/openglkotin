package engines.shader

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class StaticShader: ShaderProgram(
    VERTEX_FILE,
    FRAGMENT_FILE
) {
    private var locationTransformationMatrix: Int? = null
    private var locationProjectionMatrix: Int? = null
    private var locationViewMatrix: Int? = null
    private var locationLightPosition: Int? = null
    private var locationLightColor: Int? = null
    private var locationShineDamper: Int? = null
    private var locationReflectivity: Int? = null
    private var locationUseFakeLighting: Int? = null
    private var locationSkyColor: Int? = null

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoordinates")
        super.bindAttribute(2, "normal")
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix")
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix")
        locationViewMatrix = super.getUniformLocation("viewMatrix")
        locationLightPosition = super.getUniformLocation("lightPosition")
        locationLightColor = super.getUniformLocation("lightColor")
        locationShineDamper = super.getUniformLocation("shineDamper")
        locationReflectivity = super.getUniformLocation("reflectivity")
        locationUseFakeLighting = super.getUniformLocation("useFakeLighting")
        locationSkyColor = super.getUniformLocation("skyColor")
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        super.loadVector(locationSkyColor!!, Vector3f(r,g,b))
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

    fun loadLight(light: Light) {
        super.loadVector(locationLightPosition!!, light.position)
        super.loadVector(locationLightColor!!, light.color)
    }

    fun loadShine(damper: Float, reflectivity: Float) {
        super.loadFloat(locationShineDamper!!, damper)
        super.loadFloat(locationReflectivity!!, reflectivity)
    }

    fun loadFakeLighting(useFake: Boolean) {
        super.loadBoolean(locationUseFakeLighting!!, useFake)
    }

    companion object {
        private const val VERTEX_FILE: String = "src/shaders/shader.vert"
        private const val FRAGMENT_FILE: String = "src/shaders/shader.frag"
    }
}
package shaders

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.lang.StringBuilder
import java.nio.FloatBuffer

abstract class ShaderProgram(vertexShaderFile: String, fragmentShaderFile: String) {
    private var programID: Int
    private var vertexShaderID: Int
    private var fragmentShaderID: Int

    init {
        vertexShaderID = loadShader(vertexShaderFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderID = loadShader(fragmentShaderFile, GL20.GL_FRAGMENT_SHADER)
        programID = GL20.glCreateProgram()
        GL20.glAttachShader(programID, vertexShaderID)
        GL20.glAttachShader(programID, fragmentShaderID)
        bindAttributes()
        GL20.glLinkProgram(programID)
        GL20.glValidateProgram(programID)
        getAllUniformLocations()
    }

    fun start() { GL20.glUseProgram(programID) }
    fun stop() { GL20.glUseProgram(0) }

    fun dispose() {
        stop()
        GL20.glDetachShader(programID, vertexShaderID)
        GL20.glDetachShader(programID, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(programID)
    }

    protected abstract fun getAllUniformLocations()
    protected abstract fun bindAttributes()
    protected fun bindAttribute(attr: Int, name: String) {
        GL20.glBindAttribLocation(programID, attr, name)
    }
    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programID, uniformName)
    }
    protected fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }
    protected fun loadVector(location: Int, v3: Vector3f) {
        GL20.glUniform3f(location, v3.x, v3.y, v3.z)
    }
    protected fun loadBoolean(location: Int, value: Boolean) {
        var toLoad = 0f
        if (value) toLoad = 1f
        GL20.glUniform1f(location, toLoad)
    }
    protected fun loadMatrix(location: Int, matrix: Matrix4f) {
        matrix.store(matrixBuffer)
        matrixBuffer.flip()
        GL20.glUniformMatrix4(location, false, matrixBuffer)
    }

    companion object {
        private var matrixBuffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
        private fun loadShader(fileName: String, type: Int): Int {
            val shaderSource = StringBuilder()
            try {
                val reader = BufferedReader(FileReader(fileName))
                var line: String
                while (true) {
                    line = reader.readLine() ?: break
                    shaderSource.append(line).append("\n")
                }
                reader.close()
            } catch (e: IOException) {
                System.err.println("Could not read file")
                e.printStackTrace()
                System.exit(-1)
            }
            val shaderID = GL20.glCreateShader(type)
            GL20.glShaderSource(shaderID, shaderSource)
            GL20.glCompileShader(shaderID)
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.out.println(GL20.glGetShaderInfoLog(shaderID, 500))
                System.err.println("Could not compile shader")
                System.exit(-1)
            }
            return shaderID
        }
    }
}
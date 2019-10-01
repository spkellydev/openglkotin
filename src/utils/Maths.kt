package utils

import entities.Camera
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f

class Maths {
    companion object {
        fun createTranformationMatrix(translation: Vector3f, rx: Float, ry: Float, rz: Float,
                                      scale: Float): Matrix4f {
            val matrix = Matrix4f()
            matrix.setIdentity()
            Matrix4f.translate(translation, matrix, matrix)
            val rads = { value: Float -> Math.toRadians(value.toDouble()).toFloat() }
            Matrix4f.rotate(rads(rx), Vector3f(1f, 0f, 0f), matrix, matrix)
            Matrix4f.rotate(rads(ry), Vector3f(0f, 1f, 0f), matrix, matrix)
            Matrix4f.rotate(rads(rz), Vector3f(0f, 0f, 1f), matrix, matrix)
            Matrix4f.scale(Vector3f(scale, scale, scale), matrix, matrix)
            return matrix
        }

        fun createViewMatrix(camera: Camera): Matrix4f {
            val viewMatrix4f = Matrix4f()
            viewMatrix4f.setIdentity()
            val rads = { value: Float -> Math.toRadians(value.toDouble()).toFloat() }
            Matrix4f.rotate(rads(camera.pitch), Vector3f(1f, 0f, 0f), viewMatrix4f, viewMatrix4f)
            Matrix4f.rotate(rads(camera.yaw), Vector3f(0f, 1f, 0f), viewMatrix4f, viewMatrix4f)
            val cameraPosition = camera.position
            val negCameraPosition = Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
            Matrix4f.translate(negCameraPosition, viewMatrix4f, viewMatrix4f)
            return viewMatrix4f
        }
    }
}
package engines.render

import models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


object OBJLoader {

    fun loadModel(filename: String, loader: Loader): RawModel {
        var fr: FileReader? = null
        var line: String? = ""

        try {
            fr = FileReader(File("res/$filename.obj"))
        } catch (ex: Exception) {
            println("Couldn't load the file $filename")
            ex.printStackTrace()
        }

        val br = BufferedReader(fr!!)
        val lVertices = ArrayList<Vector3f>()
        val lNormals = ArrayList<Vector3f>()
        val lTextureMap = ArrayList<Vector2f>()
        val lIndices = ArrayList<Int>()

        var verticesArr: FloatArray? = null
        var normalArr: FloatArray? = null
        var textureArr: FloatArray? = null
        var indicesArr: IntArray? = null

        try {
            //int countLine = 1;
            while (true) {
                line = br.readLine()

                if (line == null || line.isEmpty()) {
                    println("END OF FILE")
                    break
                }

                val actualLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                //System.out.println(countLine+".-   "+line);
                if (line.startsWith("v ")) { // vertices
                    val vertex = Vector3f(
                        java.lang.Float.parseFloat(actualLine[1]), // x
                        java.lang.Float.parseFloat(actualLine[2]), // y
                        java.lang.Float.parseFloat(actualLine[3])
                    ) // z
                    lVertices.add(vertex)
                } else if (line.startsWith("vt ")) { // texture
                    val textureVertex = Vector2f(
                        java.lang.Float.parseFloat(actualLine[1]), // u
                        java.lang.Float.parseFloat(actualLine[2])
                    ) // v
                    lTextureMap.add(textureVertex)
                } else if (line.startsWith("vn ")) { // normals
                    val normalsVertex = Vector3f(
                        java.lang.Float.parseFloat(actualLine[1]), // x
                        java.lang.Float.parseFloat(actualLine[2]), // y
                        java.lang.Float.parseFloat(actualLine[3])
                    )     // z
                    lNormals.add(normalsVertex)
                } else if (line.startsWith("f ")) { // faces
                    if (textureArr == null) {
                        textureArr = FloatArray(lVertices.size * 2) // vector uv
                    }
                    if (normalArr == null) {
                        normalArr = FloatArray(lVertices.size * 3)     // vector xyz
                    }
                    val lineDiv = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (lineDiv.size > 3) {
                        processFace(lineDiv, lIndices, lTextureMap, lNormals, textureArr, normalArr)
                    }
                }
                //countLine++;
            } // end while true

            br.close()

            // list size vertex by 3 coord (xyz)
            verticesArr = FloatArray(lVertices.size * 3)
            indicesArr = IntArray(lIndices.size)

            var lineVertex = 0
            for (vertex in lVertices) {
                verticesArr[lineVertex++] = vertex.x
                verticesArr[lineVertex++] = vertex.y
                verticesArr[lineVertex++] = vertex.z
            }

            for (i in lIndices.indices) {
                indicesArr[i] = lIndices[i]
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            System.exit(-1)
        }

        return loader.loadToVAO(verticesArr!!, textureArr!!, indicesArr!!)
    }

    private fun processFace(
        actualLine: Array<String>, lIndices: MutableList<Int>,
        lTextureMap: List<Vector2f>, lNormals: List<Vector3f>,
        textureArr: FloatArray, normalArr: FloatArray
    ) {

        // Faces 3 columns
        for (i in 1..3) {
            val currentVertexFace = actualLine[i].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val currentVertex = Integer.parseInt(currentVertexFace[0]) - 1
            val currentTextureCoord = Integer.parseInt(currentVertexFace[1]) - 1
            val currentVertexNormal = Integer.parseInt(currentVertexFace[2]) - 1
            val vTextureMap = lTextureMap[currentTextureCoord]
            val vNormal = lNormals[currentVertexNormal]

            // Add vector to List Indices
            lIndices.add(currentVertex)
            // actual vertex to add the texture * 2 (each vertex have 2 texture coord uv)
            textureArr[currentVertex * 2] = vTextureMap.x  // coord u
            // coord v (-1 because opengl texture start in top left and we need bottom left)
            textureArr[currentVertex * 2 + 1] = 1 - vTextureMap.y

            // actual vertex to add the normal * 3 (each vertex have 3 normal coord xyz)
            normalArr[currentVertex * 3] = vNormal.x
            normalArr[currentVertex * 3 + 1] = vNormal.y
            normalArr[currentVertex * 3 + 2] = vNormal.z
        } // end for columns face

    }
}
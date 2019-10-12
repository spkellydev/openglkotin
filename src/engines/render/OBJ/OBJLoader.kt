package engines.render.OBJ

import engines.render.Loader
import models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.io.FileNotFoundException




object OBJLoader {
    private val RES_LOC = "res/"

    fun loadOBJ(objFileName: String): ModelData {
        var isr: FileReader? = null
        val objFile = File("$RES_LOC$objFileName.obj")
        try {
            isr = FileReader(objFile)
        } catch (e: FileNotFoundException) {
            println("File not found in res; don't use any extention")
        }

        val reader = BufferedReader(isr!!)
        var line: String?
        val vertices = ArrayList<Vertex>()
        val textures = ArrayList<Vector2f>()
        val normals = ArrayList<Vector3f>()
        val indices = ArrayList<Int>()
        try {
            while (true) {
                line = reader.readLine()
                if (line!!.startsWith("v ")) {
                    val currentLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val vertex = Vector3f(
                        java.lang.Float.valueOf(currentLine[1]) as Float,
                        java.lang.Float.valueOf(currentLine[2]) as Float,
                        java.lang.Float.valueOf(currentLine[3]) as Float
                    )
                    val newVertex = Vertex(vertices.size, vertex)
                    vertices.add(newVertex)

                } else if (line.startsWith("vt ")) {
                    val currentLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val texture = Vector2f(
                        java.lang.Float.valueOf(currentLine[1]) as Float,
                        java.lang.Float.valueOf(currentLine[2]) as Float
                    )
                    textures.add(texture)
                } else if (line.startsWith("vn ")) {
                    val currentLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val normal = Vector3f(
                        java.lang.Float.valueOf(currentLine[1]) as Float,
                        java.lang.Float.valueOf(currentLine[2]) as Float,
                        java.lang.Float.valueOf(currentLine[3]) as Float
                    )
                    normals.add(normal)
                } else if (line.startsWith("f ")) {
                    break
                }
            }
            while (line != null && line.startsWith("f ")) {
                val currentLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex1 = currentLine[1].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex2 = currentLine[2].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex3 = currentLine[3].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                processVertex(vertex1, vertices, indices)
                processVertex(vertex2, vertices, indices)
                processVertex(vertex3, vertices, indices)
                line = reader.readLine()
            }
            reader.close()
        } catch (e: IOException) {
            System.err.println("Error reading the file")
        }

        removeUnusedVertices(vertices)
        val verticesArray = FloatArray(vertices.size * 3)
        val texturesArray = FloatArray(vertices.size * 2)
        val normalsArray = FloatArray(vertices.size * 3)
        val furthest = convertDataToArrays(
            vertices, textures, normals, verticesArray,
            texturesArray, normalsArray
        )
        val indicesArray = convertIndicesListToArray(indices)
        return ModelData(
            verticesArray, texturesArray, normalsArray, indicesArray,
            furthest
        )
    }

    private fun processVertex(vertex: Array<String>, vertices: MutableList<Vertex>, indices: MutableList<Int>) {
        val index = Integer.parseInt(vertex[0]) - 1
        val currentVertex = vertices[index]
        val textureIndex = Integer.parseInt(vertex[1]) - 1
        val normalIndex = Integer.parseInt(vertex[2]) - 1
        if (!currentVertex.isSet) {
            currentVertex.textureIndex = textureIndex
            currentVertex.normalIndex = normalIndex
            indices.add(index)
        } else {
            dealWithAlreadyProcessedVertex(
                currentVertex, textureIndex, normalIndex, indices,
                vertices
            )
        }
    }

    private fun convertIndicesListToArray(indices: List<Int>): IntArray {
        val indicesArray = IntArray(indices.size)
        for (i in indicesArray.indices) {
            indicesArray[i] = indices[i]
        }
        return indicesArray
    }

    private fun convertDataToArrays(
        vertices: List<Vertex>, textures: List<Vector2f>,
        normals: List<Vector3f>, verticesArray: FloatArray, texturesArray: FloatArray,
        normalsArray: FloatArray
    ): Float {
        var furthestPoint = 0f
        for (i in vertices.indices) {
            val currentVertex = vertices[i]
            if (currentVertex.length > furthestPoint) {
                furthestPoint = currentVertex.length
            }
            val position = currentVertex.position
            val textureCoord = textures[currentVertex.textureIndex]
            val normalVector = normals[currentVertex.normalIndex]
            verticesArray[i * 3] = position.x
            verticesArray[i * 3 + 1] = position.y
            verticesArray[i * 3 + 2] = position.z
            texturesArray[i * 2] = textureCoord.x
            texturesArray[i * 2 + 1] = 1 - textureCoord.y
            normalsArray[i * 3] = normalVector.x
            normalsArray[i * 3 + 1] = normalVector.y
            normalsArray[i * 3 + 2] = normalVector.z
        }
        return furthestPoint
    }

    private fun dealWithAlreadyProcessedVertex(
        previousVertex: Vertex, newTextureIndex: Int,
        newNormalIndex: Int, indices: MutableList<Int>, vertices: MutableList<Vertex>
    ) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.index)
        } else {
            val anotherVertex = previousVertex.duplicateVertex
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(
                    anotherVertex, newTextureIndex, newNormalIndex,
                    indices, vertices
                )
            } else {
                val duplicateVertex = Vertex(vertices.size, previousVertex.position)
                duplicateVertex.textureIndex = newTextureIndex
                duplicateVertex.normalIndex = newNormalIndex
                previousVertex.duplicateVertex = duplicateVertex
                vertices.add(duplicateVertex)
                indices.add(duplicateVertex.index)
            }

        }
    }

    private fun removeUnusedVertices(vertices: List<Vertex>) {
        for (vertex in vertices) {
            if (!vertex.isSet) {
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }

    @Deprecated("Use loadObj instead")
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
                        java.lang.Float.parseFloat(actualLine[2])  // v
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
                        processFace(
                            lineDiv,
                            lIndices,
                            lTextureMap,
                            lNormals,
                            textureArr,
                            normalArr
                        )
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

        return loader.loadToVAO(verticesArr!!, textureArr!!, normalArr!!, indicesArr!!)
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
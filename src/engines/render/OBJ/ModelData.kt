package engines.render.OBJ

class ModelData(
    val vertices: FloatArray, val textureCoords: FloatArray,
    val normals: FloatArray, val indices: IntArray,
    val furthestPoint: Float
)
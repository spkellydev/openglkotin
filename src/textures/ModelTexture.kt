package textures

class ModelTexture(val textureID: Int) {
    var shineDampener: Float = 1f
    var reflectivity: Float = 0f
    var hasTrasparency = false
    var useFakeLighting = false
}
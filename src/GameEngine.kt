import org.lwjgl.LWJGLException
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode

class GameEngine {
    init {
        launchGame()
    }

    private fun launchGame() {
        try {
            Display.setDisplayMode(DisplayMode(800, 600))
            Display.create()

            while (!Display.isCloseRequested()) {
                Display.update()
            }

            Display.destroy()
        } catch (e: LWJGLException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            GameEngine()
        }
    }
}
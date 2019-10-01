package engines.render

import org.lwjgl.LWJGLException
import org.lwjgl.opengl.*

class DisplayManager {
    companion object {
        private const val WIDTH = 1200
        private const val HEIGHT = 720
        private const val FPS_CAP = 120

        fun createDisplay() {
            val attr = ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true)

            try {
                Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
                Display.create(PixelFormat(), attr)
            } catch (e: LWJGLException) {
                e.printStackTrace()
            } finally {
                GL11.glViewport(0, 0, WIDTH, HEIGHT)
            }
        }

        fun updateDisplay() {
            Display.sync(FPS_CAP)
            Display.update()
        }

        fun closeDisplay() {
            Display.destroy()
        }
    }
}
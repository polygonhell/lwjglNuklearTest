import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush

/**
  * Created by rob on 3/11/17.
  */

object Utils {
  def withStack[A](fn: MemoryStack => A) : A = {
    val stack = stackPush()
    val r = fn(stack)
    stack.close()
    r
  }
}


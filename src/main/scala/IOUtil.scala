import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.{Files, Paths}

import org.lwjgl.BufferUtils
import org.lwjgl.BufferUtils._

/**
  * Created by rob on 3/11/17.
  */
object IOUtil {
  private def resizeBuffer(buffer: ByteBuffer, newCapacity: Int) = {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip
    newBuffer.put(buffer)
    newBuffer
  }

  /**
    * Reads the specified resource and returns the raw data as a ByteBuffer.
    *
    * @param resource   the resource to read
    * @param bufferSize the initial buffer size
    * @return the resource data
    * @throws IOException if an IO error occurs
    */
  @throws[IOException]
  def ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer = {
    var buffer: ByteBuffer = null
    val path = Paths.get(resource)
    if (Files.isReadable(path)) {
      val fc = Files.newByteChannel(path)
      try {
        buffer = BufferUtils.createByteBuffer(fc.size.toInt + 1)
        while (fc.read(buffer) != -1) {}
      } finally if (fc != null) fc.close()
    } else {
      val source = this.getClass.getClassLoader.getResourceAsStream(resource)
      val rbc = Channels.newChannel(source)
      try {
        buffer = createByteBuffer(bufferSize)
        var bytes = 0
        do {
          bytes = rbc.read(buffer)
          if (bytes != -1 && buffer.remaining == 0) buffer = resizeBuffer(buffer, buffer.capacity * 2)
        } while (bytes != -1)
      } finally {
        if (source != null) source.close()
        if (rbc != null) rbc.close()
      }
    }

    buffer.flip
    buffer
  }
}

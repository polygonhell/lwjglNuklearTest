
import java.nio._

import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.nuklear._
import org.lwjgl.nuklear.Nuklear._
import org.lwjgl.opengl.{GL, GLUtil, KHRDebug}
import org.lwjgl.system.{MemoryStack, Platform}
import org.lwjgl.system.MemoryStack._
import org.lwjgl.opengl.ARBDebugOutput._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL14._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL43._
import org.lwjgl.stb.{STBTTAlignedQuad, STBTTFontinfo, STBTTPackContext, STBTTPackedchar}
import org.lwjgl.stb.STBTruetype._


//import org.lwjgl.system.MemoryStack._
import org.lwjgl.system.MemoryUtil._


object Utils {
  def withStack[A](fn: MemoryStack => A) : A = {
    val stack = stackPush()
    val r = fn(stack)
    stack.close()
    r
  }
}


object Main {
  import Utils.withStack

	val BUFFER_INITIAL_SIZE: Int = 4 * 1024

	val MAX_VERTEX_BUFFER: Int  = 512 * 1024
	val MAX_ELEMENT_BUFFER: Int = 128 * 1024

	val ALLOCATOR: NkAllocator = NkAllocator.create()
	ALLOCATOR.alloc(
		(_: Long, _: Long, size: Long) => {
			val mem: Long = nmemAlloc(size)
			if (mem == 0)
				throw new OutOfMemoryError()
			else
				mem
		}
	)

	ALLOCATOR.mfree(
		(_: Long, ptr: Long) => {
			nmemFree(ptr)
		}
	)

	val VERTEX_LAYOUT: NkDrawVertexLayoutElement.Buffer = NkDrawVertexLayoutElement.create(4)
			.position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
			.position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
			.position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
			.position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
			.flip()

  var win : Long = 0
  var width = 0
  var height = 0

  var display_width = 0
  var display_height = 0

  private val ctx = NkContext.create()
  private val default_font = NkUserFont.create()
  private val cmds = NkBuffer.create
  private val null_texture = NkDrawNullTexture.create

  var vbo = 0
  var vao = 0
  var ebo = 0
  var prog = 0
  var vert_shdr = 0
  var frag_shdr = 0
  var uniform_tex = 0
  var uniform_proj = 0

  val ttf: ByteBuffer = IOUtil.ioResourceToByteBuffer("demo/FiraSans.ttf", 160 * 1024)

  val demo = new Demo()
  val calc = new Calculator()


  def main(args: Array[String]): Unit = {

    GLFWErrorCallback.createPrint().set()
    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize glfw")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    if (Platform.get() == Platform.MACOSX)
      glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)

    val WINDOW_WIDTH = 640
    val WINDOW_HEIGHT = 640

    win = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "GLFW Nuklear Demo", NULL, NULL)
    if (win == NULL)
      throw new RuntimeException("Failed to create the GLFW window")

    glfwMakeContextCurrent(win)
    val caps = GL.createCapabilities()

    if (caps.OpenGL43) {
      glDebugMessageControl(GL_DEBUG_SOURCE_API, GL_DEBUG_TYPE_OTHER, GL_DEBUG_SEVERITY_NOTIFICATION, null: IntBuffer, false)
    } else if (caps.GL_KHR_debug) {
      KHRDebug.glDebugMessageControl(
        KHRDebug.GL_DEBUG_SOURCE_API,
        KHRDebug.GL_DEBUG_TYPE_OTHER,
        KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
        null: IntBuffer,
        false
      )
    } else if (caps.GL_ARB_debug_output) {
      glDebugMessageControlARB(GL_DEBUG_SOURCE_API_ARB, GL_DEBUG_TYPE_OTHER_ARB, GL_DEBUG_SEVERITY_LOW_ARB, null: IntBuffer, false)
    }

    val debugProc = GLUtil.setupDebugMessageCallback()

    val ctx = setupWindow(win)
    val BITMAP_W = 1024
    val BITMAP_H = 1024
    val FONT_HEIGHT = 18
    val fontTexID = glGenTextures
    val fontInfo = STBTTFontinfo.create()
    val cdata = STBTTPackedchar.create(95)
    var scale = .0
    var descent = .0

    withStack { stack =>
      stbtt_InitFont(fontInfo, ttf)
      scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT)

      val d = stack.mallocInt(1)
      stbtt_GetFontVMetrics(fontInfo, null, d, null)
      descent = d.get(0) * scale

      val bitmap = memAlloc(BITMAP_W * BITMAP_H)

      val pc = STBTTPackContext.mallocStack(stack)
      stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL)
      stbtt_PackSetOversampling(pc, 4, 4)
      stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata)
      stbtt_PackEnd(pc)

      // Convert R8 to RGBA8
      val texture = memAlloc(BITMAP_W * BITMAP_H * 4)
      (0 until bitmap.capacity()).foreach { i =>
        texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF)
      }
      texture.flip()

      glBindTexture(GL_TEXTURE_2D, fontTexID)
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture)
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

      memFree(texture)
      memFree(bitmap)
    }

    default_font.width((_, _, text, len) => {
      var text_width = 0.0f

      withStack { stack =>

        val unicode = stack.mallocInt(1)

        var glyph_len = nnk_utf_decode(text, memAddress(unicode), len)
        var text_len = glyph_len

        if (glyph_len == 0) {
          0
        } else {
          val advance = stack.mallocInt(1)
          while (text_len <= len && glyph_len != 0 && unicode.get(0) != NK_UTF_INVALID) {
            /* query currently drawn glyph information */
            stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null)
            text_width += advance.get(0) * scale.toFloat

            /* offset next glyph */
            glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len)
            text_len += glyph_len
          }
          text_width
        }
      }
    })
      .height(FONT_HEIGHT)
      .query((_, _, glyph, codepoint, _) => {
        withStack{ stack =>
          val x = stack.floats(0.0f)
          val y = stack.floats(0.0f)

          val q = STBTTAlignedQuad.mallocStack(stack)
          val advance = stack.mallocInt(1)

          stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false)
          stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null)

          val ufg = NkUserFontGlyph.create(glyph)

          ufg.width(q.x1() - q.x0())
          ufg.height(q.y1() - q.y0())
          ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent).toFloat)
          ufg.xadvance(advance.get(0) * scale.toFloat)
          ufg.uv(0).set(q.s0(), q.t0())
          ufg.uv(1).set(q.s1(), q.t1())
        }
      })
      .texture().id(fontTexID)

    nk_style_set_font(ctx, default_font)

    glfwShowWindow(win)

    while ( !glfwWindowShouldClose(win) ) {
      /* Input */
      newFrame()

      demo.layout(ctx, 50, 50)
      calc.layout(ctx, 300, 50)

      withStack{ stack =>
        val bg = stack.mallocFloat(4)
        nk_color_fv(bg, demo.background)

        val width = stack.mallocInt(1)
        val height = stack.mallocInt(1)

        glfwGetWindowSize(win, width, height)
        glViewport(0, 0, width.get(0), height.get(0))

        glClearColor(bg.get(0), bg.get(1), bg.get(2), bg.get(3))

      }

      glClear(GL_COLOR_BUFFER_BIT)
      /*
       * IMPORTANT: `nk_glfw_render` modifies some global OpenGL state
       * with blending, scissor, face culling, depth test and viewport and
       * defaults everything back into a default state.
       * Make sure to either a.) save and restore or b.) reset your own state after
       * rendering the UI.
       */
      render(NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER)
      glfwSwapBuffers(win)
    }


    shutdown()

    glfwFreeCallbacks(win)
    if ( debugProc != null )
      debugProc.free()
    glfwTerminate()
    glfwSetErrorCallback(null).free()

    println ("Done")
	}



  def destroy() : Unit = {
    glDetachShader(prog, vert_shdr)
    glDetachShader(prog, frag_shdr)
    glDeleteShader(vert_shdr)
    glDeleteShader(frag_shdr)
    glDeleteProgram(prog)
    glDeleteTextures(default_font.texture().id())
    glDeleteTextures(null_texture.texture().id())
    glDeleteBuffers(vbo)
    glDeleteBuffers(ebo)
    nk_buffer_free(cmds)
  }

  def shutdown() : Unit = {
    ctx.clip().copy().free()
    ctx.clip().paste().free()
    nk_free(ctx)
    destroy()
    default_font.query().free()
    default_font.width().free()

    calc.numberFilter.free()

    ALLOCATOR.alloc().free()
    ALLOCATOR.mfree().free()
  }



  def newFrame() : Unit ={
    withStack{ stack =>
      val w = stack.mallocInt(1)
      val h = stack.mallocInt(1)

      glfwGetWindowSize(win, w, h)
      width = w.get(0)
      height = h.get(0)

      glfwGetFramebufferSize(win, w, h)
      display_width = w.get(0)
      display_height = h.get(0)
    }

    nk_input_begin(ctx)
    glfwPollEvents()

    val mouse = ctx.input().mouse()
    if ( mouse.grab() )
      glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
    else if ( mouse.grabbed() ) {
      val prevX = mouse.prev().x()
      val prevY = mouse.prev().y()
      glfwSetCursorPos(win, prevX, prevY)
      mouse.pos().x(prevX)
      mouse.pos().y(prevY)
    } else if ( mouse.ungrab() )
      glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_NORMAL)

    nk_input_end(ctx)
  }




  def render(AA: Int, max_vertex_buffer: Int, max_element_buffer: Int) : Unit = {
    withStack{stack =>
      // setup global state
      glEnable(GL_BLEND)
      glBlendEquation(GL_FUNC_ADD)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      glDisable(GL_CULL_FACE)
      glDisable(GL_DEPTH_TEST)
      glEnable(GL_SCISSOR_TEST)
      glActiveTexture(GL_TEXTURE0)

      // setup program
      glUseProgram(prog)
      glUniform1i(uniform_tex, 0)
      glUniformMatrix4fv(uniform_proj, false, stack.floats(
        2.0f / width, 0.0f, 0.0f, 0.0f,
        0.0f, -2.0f / height, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f, 1.0f
      ))
      glViewport(0, 0, display_width, display_height)
    }

    {
      // convert from command queue into draw list and draw to screen

      // allocate vertex and element buffer
      glBindVertexArray(vao)
      glBindBuffer(GL_ARRAY_BUFFER, vbo)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)

      glBufferData(GL_ARRAY_BUFFER, max_vertex_buffer, GL_STREAM_DRAW)
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, max_element_buffer, GL_STREAM_DRAW)

      // load draw vertices & elements directly into vertex + element buffer
      val vertices = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, max_vertex_buffer, null)
      val elements = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, max_element_buffer, null)

      withStack {stack =>
        // fill convert configuration
        val config = NkConvertConfig.callocStack(stack)
          .vertex_layout(VERTEX_LAYOUT)
          .vertex_size(20)
          .vertex_alignment(4)
          .null_texture(null_texture)
          .circle_segment_count(22)
          .curve_segment_count(22)
          .arc_segment_count(22)
          .global_alpha(1.0f)
          .shape_AA(AA)
          .line_AA(AA)

        // setup buffers to load vertices and elements
        val vbuf = NkBuffer.mallocStack(stack)
        val ebuf = NkBuffer.mallocStack(stack)

        nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/)
        nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/)
        nk_convert(ctx, cmds, vbuf, ebuf, config)
      }
      glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER)
      glUnmapBuffer(GL_ARRAY_BUFFER)

      // iterate over and execute each draw command
      val fb_scale_x = display_width.toFloat / width
      val fb_scale_y = display_height.toFloat / height

      var offset : Long = NULL

      var cmd = nk__draw_begin(ctx, cmds)

      while (cmd != null) {
        if ( cmd.elem_count() != 0 ) {
          glBindTexture(GL_TEXTURE_2D, cmd.texture().id())
          glScissor(
            (cmd.clip_rect().x() * fb_scale_x).toInt,
            ((height - (cmd.clip_rect().y() + cmd.clip_rect().h())).toInt * fb_scale_y).toInt,
            (cmd.clip_rect().w() * fb_scale_x).toInt,
            (cmd.clip_rect().h() * fb_scale_y).toInt
          )
          glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset)
          offset += cmd.elem_count() * 2
        }
        cmd = nk__draw_next(cmd, cmds, ctx)
      }
      nk_clear(ctx)
    }

    // default OpenGL state
    glUseProgram(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
    glDisable(GL_BLEND)
    glDisable(GL_SCISSOR_TEST)
  }




  private def setupContext() {
    val NK_SHADER_VERSION = if (Platform.get eq Platform.MACOSX) "#version 150\n" else "#version 300 es\n"
    val vertex_shader = NK_SHADER_VERSION + "uniform mat4 ProjMtx;\n" + "in vec2 Position;\n" + "in vec2 TexCoord;\n" + "in vec4 Color;\n" + "out vec2 Frag_UV;\n" + "out vec4 Frag_Color;\n" + "void main() {\n" + "   Frag_UV = TexCoord;\n" + "   Frag_Color = Color;\n" + "   gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" + "}\n"
    val fragment_shader = NK_SHADER_VERSION + "precision mediump float;\n" + "uniform sampler2D Texture;\n" + "in vec2 Frag_UV;\n" + "in vec4 Frag_Color;\n" + "out vec4 Out_Color;\n" + "void main(){\n" + "   Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" + "}\n"
    nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE)
    prog = glCreateProgram
    vert_shdr = glCreateShader(GL_VERTEX_SHADER)
    frag_shdr = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(vert_shdr, vertex_shader)
    glShaderSource(frag_shdr, fragment_shader)
    glCompileShader(vert_shdr)
    glCompileShader(frag_shdr)
    if (glGetShaderi(vert_shdr, GL_COMPILE_STATUS) != GL_TRUE) throw new IllegalStateException
    if (glGetShaderi(frag_shdr, GL_COMPILE_STATUS) != GL_TRUE) throw new IllegalStateException
    glAttachShader(prog, vert_shdr)
    glAttachShader(prog, frag_shdr)
    glLinkProgram(prog)
    if (glGetProgrami(prog, GL_LINK_STATUS) != GL_TRUE) throw new IllegalStateException
    uniform_tex = glGetUniformLocation(prog, "Texture")
    uniform_proj = glGetUniformLocation(prog, "ProjMtx")
    val attrib_pos = glGetAttribLocation(prog, "Position")
    val attrib_uv = glGetAttribLocation(prog, "TexCoord")
    val attrib_col = glGetAttribLocation(prog, "Color")

    // buffer setup
    vbo = glGenBuffers
    ebo = glGenBuffers
    vao = glGenVertexArrays
    glBindVertexArray(vao)
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
    glEnableVertexAttribArray(attrib_pos)
    glEnableVertexAttribArray(attrib_uv)
    glEnableVertexAttribArray(attrib_col)
    glVertexAttribPointer(attrib_pos, 2, GL_FLOAT, false, 20, 0)
    glVertexAttribPointer(attrib_uv, 2, GL_FLOAT, false, 20, 8)
    glVertexAttribPointer(attrib_col, 4, GL_UNSIGNED_BYTE, true, 20, 16)

    // null texture setup
    val nullTexID = glGenTextures
    null_texture.texture.id(nullTexID)
    null_texture.uv.set(0.5f, 0.5f)
    glBindTexture(GL_TEXTURE_2D, nullTexID)

    withStack { stack =>
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF))
    }

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

    glBindTexture(GL_TEXTURE_2D, 0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
  }

  private def setupWindow(win: Long) = {
    glfwSetScrollCallback(win, (_, _, yoffset) => nk_input_scroll(ctx, yoffset.toFloat))
    glfwSetCharCallback(win, (_, codepoint) => nk_input_unicode(ctx, codepoint))
    glfwSetKeyCallback(win, (window, key, _, action, _) => {
      val press = action == GLFW_PRESS
      key match {
        case GLFW_KEY_ESCAPE => glfwSetWindowShouldClose(window, true)
        case GLFW_KEY_DELETE => nk_input_key(ctx, NK_KEY_DEL, press)
        case GLFW_KEY_ENTER => nk_input_key(ctx, NK_KEY_ENTER, press)
        case GLFW_KEY_TAB => nk_input_key(ctx, NK_KEY_TAB, press)
        case GLFW_KEY_BACKSPACE => nk_input_key(ctx, NK_KEY_BACKSPACE, press)
        case GLFW_KEY_UP => nk_input_key(ctx, NK_KEY_UP, press)
        case GLFW_KEY_DOWN => nk_input_key(ctx, NK_KEY_DOWN, press)
        case GLFW_KEY_HOME =>
          nk_input_key(ctx, NK_KEY_TEXT_START, press)
          nk_input_key(ctx, NK_KEY_SCROLL_START, press)
        case GLFW_KEY_END =>
          nk_input_key(ctx, NK_KEY_TEXT_END, press)
          nk_input_key(ctx, NK_KEY_SCROLL_END, press)
        case GLFW_KEY_PAGE_DOWN => nk_input_key(ctx, NK_KEY_SCROLL_DOWN, press)
        case GLFW_KEY_PAGE_UP => nk_input_key(ctx, NK_KEY_SCROLL_UP, press)
        case GLFW_KEY_LEFT_SHIFT | GLFW_KEY_RIGHT_SHIFT => nk_input_key(ctx, NK_KEY_SHIFT, press)
        case GLFW_KEY_LEFT_CONTROL | GLFW_KEY_RIGHT_CONTROL =>
          if (press) {
            nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
          } else {
            nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
            nk_input_key(ctx, NK_KEY_COPY, false)
            nk_input_key(ctx, NK_KEY_PASTE, false)
            nk_input_key(ctx, NK_KEY_CUT, false)
            nk_input_key(ctx, NK_KEY_SHIFT, false)
          }
      }
    })

    glfwSetCursorPosCallback(win, (_, xpos, ypos) => nk_input_motion(ctx, xpos.toInt, ypos.toInt))
    glfwSetMouseButtonCallback(win, (window, button, action, _) => withStack{ stack =>
        val cx = stack.mallocDouble(1)
        val cy = stack.mallocDouble(1)

        glfwGetCursorPos(window, cx, cy)

        val x = cx.get(0).toInt
        val y = cy.get(0).toInt

        val nkButton = button match {
          case GLFW_MOUSE_BUTTON_RIGHT => NK_BUTTON_RIGHT
          case GLFW_MOUSE_BUTTON_MIDDLE => NK_BUTTON_MIDDLE
          case _ => NK_BUTTON_LEFT
        }
        nk_input_button(ctx, nkButton, x, y, action == GLFW_PRESS)
    })

    nk_init(ctx, ALLOCATOR, null)

    ctx.clip.copy((_, text, len) => {
      if (len == 0)
        ()

      withStack { stack =>
        val str = stack.malloc(len + 1)
        memCopy(text, memAddress(str), len)
        str.put(len, 0)

        glfwSetClipboardString(win, str)
      }
    })

    ctx.clip.paste((_, edit) => {
      val text = nglfwGetClipboardString(win)
      if (text != NULL)
        nnk_textedit_paste(edit, text, nnk_strlen(text))
    })

    setupContext()
    ctx
  }
}
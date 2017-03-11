import java.nio.IntBuffer

import org.lwjgl.BufferUtils
import org.lwjgl.nuklear.{NkColor, NkContext, NkRect, NkVec2}
import org.lwjgl.nuklear.Nuklear._
import Utils.withStack

/**
  * Created by rob on 3/11/17.
  */


class Demo2(title: String) {
  private val EASY: Int = 0
  private val HARD: Int = 1

  var background: NkColor = NkColor.create
  nk_rgb(28, 48, 62, background)
  private var op: Int = EASY
  private val compression: IntBuffer = BufferUtils.createIntBuffer(1).put(0, 20)

  def layout(ctx: NkContext, x: Int, y: Int) {
    withStack { stack =>
      val rect: NkRect = NkRect.mallocStack(stack)
      if (nk_begin(ctx, title, nk_rect(x, y, 230, 250, rect), NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE)) {
        nk_layout_row_static(ctx, 30, 80, 1)
        if (nk_button_label(ctx, "button")) System.out.println("button pressed")
        nk_layout_row_dynamic(ctx, 30, 2)
        if (nk_option_label(ctx, "easy", op == EASY)) op = EASY
        if (nk_option_label(ctx, "hard", op == HARD)) op = HARD
        nk_layout_row_dynamic(ctx, 25, 1)
        nk_property_int(ctx, "Compression:", 0, compression, 100, 10, 1)
        nk_layout_row_dynamic(ctx, 20, 1)
        nk_label(ctx, "background:", NK_TEXT_LEFT)
        nk_layout_row_dynamic(ctx, 25, 1)
        if (nk_combo_begin_color(ctx, background, NkVec2.mallocStack(stack).set(nk_widget_width(ctx), 400))) {
          nk_layout_row_dynamic(ctx, 120, 1)
          nk_color_picker(ctx, background, NK_RGBA)
          nk_layout_row_dynamic(ctx, 25, 1)
          background.r((nk_propertyi(ctx, "#R:", 0, background.r & 0xFF, 255, 1, 1) & 0xFF).toByte)
          background.g((nk_propertyi(ctx, "#G:", 0, background.g & 0xFF, 255, 1, 1) & 0xFF).toByte)
          background.b((nk_propertyi(ctx, "#B:", 0, background.b & 0xFF, 255, 1, 1) & 0xFF).toByte)
          background.a((nk_propertyi(ctx, "#A:", 0, background.a & 0xFF, 255, 1, 1) & 0xFF).toByte)
          nk_combo_end(ctx)
        }
      }
      nk_end(ctx)
    }
  }
}
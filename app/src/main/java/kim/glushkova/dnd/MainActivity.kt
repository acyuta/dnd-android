package kim.glushkova.dnd

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var layout: LinearLayout
    lateinit var views: Array<View>
    var color: Int = 0
    var selectedId: Int = 0

    companion object {
        const val IMAGEVIEW_TAG = "icon bitmap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.layout)
        color = ContextCompat.getColor(this, R.color.dark_blue)
        bulidViews(layout)
    }

    private fun bulidViews(layout: LinearLayout?) {
        val count: Int = 4
        views = emptyArray()
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_pig)
        val _height = resources?.getDimensionPixelSize(R.dimen.pigsize)
        (0..count).forEach {
            val ll = LinearLayout(this)
            layout?.addView(ll)
            ll.layoutParams.apply {
                width = MATCH_PARENT
                height = _height!!
            }
            ll.setBackgroundResource(R.drawable.bg_border)
            val im = ImageView(this)
            val pos = it
            im.apply {
                setPadding(10, 10, 10, 10)
                tag = IMAGEVIEW_TAG
                id = pos
                if (it != 0)
                    visibility = View.INVISIBLE
                dragAndDrop(this)
                setOnLongClickListener {
                    val item = ClipData.Item(it.tag.toString().plus(pos) as? CharSequence)
                    val dragData = ClipData(
                        it.tag.toString().plus(pos) as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )

                    val myShadow = View.DragShadowBuilder(it)

                    it.startDragAndDrop(
                        dragData,
                        myShadow,
                        null,
                        0
                    )
                    true
                }
            }
            ll.addView(im)
            im.setImageDrawable(drawable)
            views = views.plus(im)
        }
    }


    private fun dragAndDrop(image: ImageView) {
        image.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Timber.d("Action ${v.id} $event")
                    views.forEach {
                        Timber.d("Set visible")
                        it.visibility = View.VISIBLE
                        (it as? ImageView)?.setColorFilter(color)
                        it.invalidate()
                    }
                    true //indicate that View can accept the dragged data
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Timber.d("Action ${v.id} $event")
                    (v as? ImageView)?.setColorFilter(Color.LTGRAY)
                    selectedId = v.id
                    Timber.d("Select $selectedId")
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION ->
                    true //ignore this event
                DragEvent.ACTION_DRAG_EXITED -> {
                    Timber.d("Action ${v.id} $event")
                    (v as? ImageView)?.setColorFilter(Color.BLUE)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    Timber.d("Action ${v.id} $event")

                    //Get the item containingn dragged data
                    Timber.d("Clip data = ${event.clipData}")
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    Timber.d("Dropping item = ${item.text}")
                    Timber.d("View item = ${v.tag}")
                    (v as? ImageView)?.clearColorFilter()
                    v.invalidate()

                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Timber.d("Action ${v.id} $event")
                    (v as? ImageView)?.clearColorFilter()
                    views.forEach {
                        if (it.id == selectedId)
                            it.visibility = View.VISIBLE
                        else
                            it.visibility = View.GONE
                    }
                    v.invalidate()

//                    when (event.result) {
//                        true -> Toast.makeText(this, "Drop was handled", Toast.LENGTH_SHORT)
//                        else -> Toast.makeText(this, "Drop didn't work", Toast.LENGTH_SHORT)
//                    }.show()
                    true
                }

                else -> false
            }
        }
    }
}

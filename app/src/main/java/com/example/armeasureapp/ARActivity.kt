package com.example.armeasureapp

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlin.math.sqrt

class ARActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private var firstAnchor: AnchorNode? = null
    private var secondAnchor: AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            handleTap(hitResult)
        }
    }

    private fun handleTap(hitResult: HitResult) {
        if (firstAnchor == null) {
            firstAnchor = placeAnchor(hitResult)
            Toast.makeText(this, "First point placed", Toast.LENGTH_SHORT).show()
        } else if (secondAnchor == null) {
            secondAnchor = placeAnchor(hitResult)
            measureDistance()
        } else {
            clearAnchors()
            firstAnchor = placeAnchor(hitResult)
            Toast.makeText(this, "First point placed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun placeAnchor(hitResult: HitResult): AnchorNode {
        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        ModelRenderable.builder()
            .setSource(this, R.raw.andy)  // You can use a default 3D model for the anchor points
            .build()
            .thenAccept { renderable ->
                val node = TransformableNode(arFragment.transformationSystem)
                node.setParent(anchorNode)
                node.renderable = renderable
                node.select()
            }

        return anchorNode
    }

    private fun measureDistance() {
        if (firstAnchor != null && secondAnchor != null) {
            val firstPosition = firstAnchor!!.worldPosition
            val secondPosition = secondAnchor!!.worldPosition

            val dx = firstPosition.x - secondPosition.x
            val dy = firstPosition.y - secondPosition.y
            val dz = firstPosition.z - secondPosition.z

            val distance = sqrt((dx * dx + dy * dy + dz * dz).toDouble())

            Toast.makeText(this, "Distance: %.2f meters".format(distance), Toast.LENGTH_LONG).show()
        }
    }

    private fun clearAnchors() {
        firstAnchor?.anchor?.detach()
        firstAnchor?.setParent(null)
        firstAnchor = null

        secondAnchor?.anchor?.detach()
        secondAnchor?.setParent(null)
        secondAnchor = null
    }
}

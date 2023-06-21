package com.likeminds.chatmm.utils

import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.hypot

object AnimationUtils {

    @JvmStatic
    private val ANIMATION_DURATION_SHORT = 250

    @JvmStatic
    @JvmOverloads
    fun circleRevealView(view: View, duration: Int = ANIMATION_DURATION_SHORT) {
        // get the center for the clipping circle
        val cx = view.width
        val cy = view.height / 2

        // get the final radius for the clipping circle
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        // make the view visible and start the animation
        view.visibility = View.VISIBLE
        anim.start()
    }

    @JvmStatic
    fun circleHideView(view: View, listenerAdapter: AnimatorListenerAdapter) {
        circleHideView(view, ANIMATION_DURATION_SHORT, listenerAdapter)
    }

    @JvmStatic
    fun circleHideView(view: View, duration: Int, listenerAdapter: AnimatorListenerAdapter) {
        // get the center for the clipping circle
        val cx = view.width
        val cy = view.height / 2

        // get the initial radius for the clipping circle
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(listenerAdapter)

        anim.duration = if (duration > 0) duration.toLong() else ANIMATION_DURATION_SHORT.toLong()

        // start the animation
        anim.start()
    }
}
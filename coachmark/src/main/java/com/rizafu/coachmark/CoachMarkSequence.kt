package com.rizafu.coachmark

import java.util.Collections
import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.Queue

/**
 * Created by RizaFu on 11/14/16.
 */

class CoachMarkSequence {
    private val coachMarks: Queue<CoachMark.Builder> = LinkedList<CoachMark.Builder>()
    private var coachMark: CoachMark? = null
    private var started: Boolean = false

    private var onSequenceFinish: (() -> Unit)? = null
    private var onSequenceShowNext: ((CoachMarkSequence, CoachMark?) -> Unit)? = null

    fun setCoachMarks(vararg coachMarks: CoachMark.Builder): CoachMarkSequence {
        Collections.addAll<CoachMark.Builder>(this.coachMarks, *coachMarks)
        return this
    }

    fun setCoachMarks(coachMarks: List<CoachMark.Builder>): CoachMarkSequence {
        this.coachMarks.addAll(coachMarks)
        return this
    }

    fun addCoachMark(coachMarks: CoachMark.Builder): CoachMarkSequence {
        this.coachMarks.add(coachMarks)
        return this
    }

    fun setOnSequenceFinish(onSequenceFinish: () -> Unit): CoachMarkSequence {
        this.onSequenceFinish = onSequenceFinish
        return this
    }

    fun setOnSequenceShowNext(onSequenceShowNext: (CoachMarkSequence, CoachMark?) -> Unit): CoachMarkSequence {
        this.onSequenceShowNext = onSequenceShowNext
        return this
    }

    fun start() {
        if (coachMarks.isEmpty() || started) return

        started = true
        next()
    }

    fun showNext() {
        coachMark?.dismiss()
    }

    private operator fun next() {
        try {
            coachMark = coachMarks.remove()
                    .setOnClickTarget{ it.dismiss() }
                    .setOnAfterDismissListener { next() }
                    .show()
            onSequenceShowNext?.invoke(this, coachMark)
        } catch (e: NoSuchElementException) {
            onSequenceFinish?.invoke()
        }

    }

}
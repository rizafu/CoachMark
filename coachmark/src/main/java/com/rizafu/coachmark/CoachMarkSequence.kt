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

    private var onSequenceFinish: OnSequenceFinish? = null
    private var onSequenceShowNext: OnSequenceShowNext? = null

    private val onClick: (CoachMark) -> Unit = { coachMark -> coachMark.destroy(Runnable{ next() }) }

    interface OnSequenceFinish {
        fun onSequenceFinish()
    }

    interface OnSequenceShowNext {
        fun onSequenceShowNext(coachMarkSequence: CoachMarkSequence, coachMark: CoachMark?)
    }

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

    fun setOnSequenceFinish(OnSequenceFinish: OnSequenceFinish): CoachMarkSequence {
        this.onSequenceFinish = OnSequenceFinish
        return this
    }

    fun setOnSequenceShowNext(onSequenceShowNext: OnSequenceShowNext): CoachMarkSequence {
        this.onSequenceShowNext = onSequenceShowNext
        return this
    }

    fun start() {
        if (coachMarks.isEmpty() || started) return

        started = true
        next()
    }

    fun showNext() {
        coachMark?.destroy(Runnable { next() })
    }

    private operator fun next() {
        try {
            coachMark = coachMarks.remove().setOnClickTarget(onClick).show()
            onSequenceShowNext?.onSequenceShowNext(this, coachMark)
        } catch (e: NoSuchElementException) {
            onSequenceFinish?.onSequenceFinish()
        }

    }

}
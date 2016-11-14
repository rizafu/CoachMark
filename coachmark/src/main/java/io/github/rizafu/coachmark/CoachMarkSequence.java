package io.github.rizafu.coachmark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Created by RizaFu on 11/14/16.
 */

public class CoachMarkSequence {
    private Queue<CoachMark.Builder> coachMarks;
    private boolean started;

    Listener listener;

    public interface Listener {
        void onSequenceFinish();
    }

    public CoachMarkSequence() {
        this.coachMarks = new LinkedList<>();
    }

    public CoachMarkSequence setCoachMarks(CoachMark.Builder... coachMarks) {
        Collections.addAll(this.coachMarks, coachMarks);
        return this;
    }

    public CoachMarkSequence setCoachMarks(List<CoachMark.Builder> coachMarks) {
        this.coachMarks.addAll(coachMarks);
        return this;
    }

    public CoachMarkSequence addCoachMark(CoachMark.Builder coachMarks) {
        this.coachMarks.add(coachMarks);
        return this;
    }

    public CoachMarkSequence setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        if (coachMarks.isEmpty() || started) return;

        started = true;
        showNext();
    }

    private void showNext() {
        try{
            coachMarks.remove().setOnClickTarget(onClick).setOnClickAction(onClick).show();
        } catch (NoSuchElementException e){
            if (listener != null) listener.onSequenceFinish();
        }
    }

    private CoachMark.Builder.OnClick onClick = new CoachMark.Builder.OnClick() {
        @Override
        public void onClick(CoachMark coachMark) {
            coachMark.destroy(new Runnable() {
                @Override
                public void run() {
                    showNext();
                }
            });
        }
    };

}
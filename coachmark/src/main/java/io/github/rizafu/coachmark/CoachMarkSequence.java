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
    private CoachMark coachMark;
    private boolean started;

    private OnSequenceFinish OnSequenceFinish;
    private OnSequenceShowNext onSequenceShowNext;

    public interface OnSequenceFinish {
        void onSequenceFinish();
    }

    public interface OnSequenceShowNext{
        void OnSequenceShowNext(CoachMarkSequence coachMarkSequence, CoachMark coachMark);
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

    public CoachMarkSequence setOnSequenceFinish(OnSequenceFinish OnSequenceFinish) {
        this.OnSequenceFinish = OnSequenceFinish;
        return this;
    }

    public CoachMarkSequence setOnSequenceShowNext(OnSequenceShowNext onSequenceShowNext) {
        this.onSequenceShowNext = onSequenceShowNext;
        return this;
    }

    public void start() {
        if (coachMarks.isEmpty() || started) return;

        started = true;
        next();
    }

    public void showNext(){
        coachMark.destroy(new Runnable() {
            @Override
            public void run() {
                next();
            }
        });
    }

    private void next() {
        try{
            coachMark = coachMarks.remove().setOnClickTarget(onClick).show();
            if (onSequenceShowNext!=null){
                onSequenceShowNext.OnSequenceShowNext(this,coachMark);
            }
        } catch (NoSuchElementException e){
            if (OnSequenceFinish != null) OnSequenceFinish.onSequenceFinish();
        }
    }

    private CoachMark.Builder.OnClick onClick = new CoachMark.Builder.OnClick() {
        @Override
        public void onClick(CoachMark coachMark) {
            coachMark.destroy(new Runnable() {
                @Override
                public void run() {
                    next();
                }
            });
        }
    };

}
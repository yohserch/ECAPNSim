package Components;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by serch on 19/01/15.
 */
public class Interval implements Serializable {
    IntervalDate startTime;
    IntervalDate finalTime;

    public Interval() {

    }

    public Interval(IntervalDate startTime, IntervalDate finalTime) throws IntervalWrongException {
        this.startTime = startTime;
        this.finalTime = finalTime;
        if (!startTime.lessThan(finalTime)) throw new IntervalWrongException();
    }

    public IntervalDate getStartTime() {
        return startTime;
    }

    public IntervalDate getFinalTime() {
        return finalTime;
    }

    public boolean isInside(Calendar eT) {
        IntervalDate eventTime = new IntervalDate(eT, startTime.getHigherTime());
        if (startTime.lessThan(eventTime) && eventTime.lessThan(finalTime))
            return true;
        return false;
    }

}

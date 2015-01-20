package Components;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by serch on 19/01/15.
 */
public class IntervalDate implements Serializable{
    //Stores the values of table
    int[] time = {-1, -1, -1, -1, -1, -1};
    int higherTime;

    public IntervalDate() {
        higherTime = -1;
    }

    public IntervalDate(int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59) throw  new IntervalDateException();
        time[0] = sec;
        higherTime = 0;
    }

    public IntervalDate(int min, int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59 || min < 0 || min > 59)
            throw new IntervalDateException();
        time[0] = sec;
        time[1] = min;
        higherTime = 1;
    }

    public IntervalDate(int hour, int min, int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59 || min < 0 || min > 59 || hour < 0 || hour > 23)
            throw new IntervalDateException();
        time[0] = sec;
        time[1] = min;
        time[2] = hour;
        higherTime = 2;
    }

    /** Construye un objeto IntervalDate. */
    public IntervalDate(int day, int hour, int min, int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59 || min < 0 || min > 59 || hour < 0 || hour > 23 || day < 0 || day > 31)
            throw new IntervalDateException();
        time[0] = sec;
        time[1] = min;
        time[2] = hour;
        time[3] = day;
        higherTime = 3;
    }

    /** Construye un objeto IntervalDate. */
    public IntervalDate(int month, int day, int hour, int min, int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59 || min < 0 || min > 59 || hour < 0 || hour > 23 || day < 0 || day > 31 || month < 0 || month > 12)
             throw new IntervalDateException();
        time[0] = sec;
        time[1] = min;
        time[2] = hour;
        time[3] = day;
        time[4] = month-1;
        higherTime = 4;
    }

    /** Construye un objeto IntervalDate. */
    public IntervalDate(int year, int month, int day, int hour, int min, int sec) throws IntervalDateException {
        if (sec < 0 || sec > 59 || min < 0 || min > 59 || hour < 0 || hour > 23 || day < 0 || day > 31 || month < 0 || month > 12)
            throw new IntervalDateException();
        time[0] = sec;
        time[1] = min;
        time[2] = hour;
        time[3] = day;
        time[4] = month-1;
        time[5] = year;
        higherTime = 5;
    }

    public IntervalDate(String interD) throws IntervalDateException {
        System.out.println("Constructor IntervalDate(Srting) ***");
        int i;
        char c;
        String number = new String();
        higherTime = -1;
        System.out.println("Constructor interD = " + interD);
        for(i=interD.length()-1; i>=0; i--) {
            c = interD.charAt(i);
            System.out.println(" c = " + c + "; i = " + i + "; higherTime = " + higherTime);
            if(c >= '0' && c <= '9') {
                number = interD.substring(i,i+1).concat(number);
            } else if((c == ':' && higherTime != 1) || (c == '-' && higherTime == 1) || i == interD.length()) {
                higherTime++;
                time[higherTime] = (new Integer(number)).intValue();
                System.out.println("time[higherTime] = " + time[higherTime]);
                switch(higherTime) {   // validacion de los valores de la fecha
                    case 0:
                    case 1: if(time[higherTime] < 0 || time[higherTime] > 59) {
                        System.out.println("Case 0,1");
                        throw new IntervalDateException();
                    }
                        break;
                    case 2: if(time[higherTime] < 0 || time[higherTime] > 23) {
                        System.out.println("Case 2");
                        throw new IntervalDateException();
                    }
                        break;
                    case 3: if(time[higherTime] < 0 || time[higherTime] > 31) {
                        System.out.println("Case 3");
                        throw new IntervalDateException();
                    }
                        break;
                    case 4: if(time[higherTime] < 0 || time[higherTime] > 12) {
                        System.out.println("Case 4");
                        throw new IntervalDateException();
                    }
                        break;
                }
                number = new String();
            } else {
                System.out.println("Case else");
                throw new IntervalDateException();
            }
        }
    }

    public IntervalDate(Calendar c, int higherTime) {
        int[] numbers = {Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DATE, Calendar.MONTH, Calendar.YEAR};
        for(int i=0; i<=higherTime; i++) {
            time[i] = c.get(numbers[i]);
            System.out.println("time[" + i + "] = " + time[i]);
        }

        this.higherTime = higherTime;
    }

    /** Devuelve el valor de tiempo mas alto. */
    public int getHigherTime() {
        return higherTime;
    }

    /** Devuelve el valor de tiempo en la posicion especificada. */
    public int getTimeIn(int pos) {
        return this.time[pos];
    }

    /** Devuelve el valor de tiempo mas alto. */
    public boolean lessThan(IntervalDate id) {
        if(this.higherTime != id.getHigherTime()) {
            return false;
        }
        for(int ht = this.higherTime; ht >= 0; ht--) {
            if(this.time[ht] < id.getTimeIn(ht)) {
                return true;
            }else if(this.time[ht] > id.getTimeIn(ht)) {
                return false;
            }
        }
        return false;
    }
}

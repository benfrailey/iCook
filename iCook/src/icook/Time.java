/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package icook;

/**
 *
 * @author Ben Frailey
 */
public class Time {

    private int hour;
    private int minute;
    
    public Time(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }
    
    public void subtractTime(int minutes){
        minute -= minutes;
        while(minute < 0){
            hour --;
            minute += 60;
            if(hour < 1){
                hour += 12;
            }
        }
    }
    
    public int minutesFromOriginal(Time original){
        int minutes = 0;
        if(hour == original.hour){
            return original.minute - minute;
        }
        else{
            if(hour > original.hour){
                int rollover = 12 - hour;
                minutes = (rollover + original.hour) * 60;
            }
            else
            {
                minutes = (original.hour - hour) * 60;
                if(minute > original.minute)
                    minutes -= 60;
            }
                if(minute > original.minute){
                    minutes += 60 - (minute - original.minute);
                }
                else{
                    minutes += original.minute - minute;
            }
        }
        return minutes;
    }
    
    public void printTime(int difference, Time original){
        System.out.println(hour + ":" + minute + " is " + difference + "minutes from " + original.hour + original.minute);
    }
    public int getHour(){
        return hour;
    }
    
    public int getMinute(){
        return minute;
    }
}

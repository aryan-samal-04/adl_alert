//
// Name:    CalendarData.java - Data structure class
// Type:    Java source file
// Program: ADL (Activities of Daily Living) Schedule Alert
// Author:  Aryan Samal
// Version: 1.0
//
// Copyright 2020, This software is confidential and proprietary to the author.
//

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

//
// CalendarData Class:
// Holds all data involving ADL tasks including methods to alter and retrieve data.
public class CalendarData
{

    private Date mDate;
    private String mStringLocalTime;
    private String mStringDate;
    private String mTask;
    private boolean mRepeat;
    private int mPriority;
    private final SimpleDateFormat mDateFormatter;
    private int mLocalTimeHour;
    private int mLocalTimeMinute;
    private boolean mRowUpdatedFlag = false;
    private boolean mRowInsertedFlag = false;
    private boolean mRowScheduledFlag = false;
    private boolean mDateTimeUpdatedFlag = false;
    private final int mTaskId;

    // Constructor
    public CalendarData(String pStringDate, String pLocalTime, String pTask, boolean pRepeat, int pPriority,
                        int pTaskId)
    {

        mDateFormatter = new SimpleDateFormat(("yyyy-MM-dd"));
        try {
            mDate = mDateFormatter.parse(pStringDate);
        } catch (ParseException e) {
            System.out.println("error");
        }

        DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        mStringLocalTime = pLocalTime;

        mStringDate = mDateFormatter.format(mDate);
        mTask = pTask;
        mRepeat = pRepeat;
        mPriority = pPriority;
        mTaskId = pTaskId;
    }

    //
    // getmRepeat:
    // returns true if the task is supposed to repeat and false otherwise
    public boolean getmRepeat()
    {
        return mRepeat;
    }

    //
    // setmRowUpdatedFlag:
    // sets the boolean value that determines if a row was updated or not
    public void setmRowUpdatedFlag(boolean pRowUpdateFlagValue)
    {
        mRowUpdatedFlag = pRowUpdateFlagValue;
    }

    //
    // getmRowUpdatedFlag:
    // returns true or false depending on if the row was updated or not
    public boolean getmRowUpdatedFlag()
    {
        return mRowUpdatedFlag;
    }

    //
    // setmRowInsertedFlag:
    // sets the mRowInsertedFlag to true if a row was inserted and false if it was not
    public void setmRowInsertedFlag(boolean pRowInsertUpdateFlagValue)
    {
        mRowInsertedFlag = pRowInsertUpdateFlagValue;
    }

    //
    // getmRowInsertedFlag:
    // returns true if the row was inserted and false if it was not
    public boolean getmRowInsertedFlag()
    {
        return mRowInsertedFlag;
    }

    //
    // getmRowScheduledFlag:
    // returns true if the row was scheduled and has a timer and false otherwise
    public boolean getmRowScheduledFlag()
    {
        return mRowScheduledFlag;
    }

    //
    // setmRowScheduledFlag:
    // sets the mRowScheduledFlag to true if the task was registered with
    // a timer and false if it was not registered with a timer
    public void setmRowScheduledFlag(boolean pRowScheduledUpdateFlagValue)
    {
        mRowScheduledFlag = pRowScheduledUpdateFlagValue;
    }

    //
    // setmDateTimeUpdatedFlag:
    // sets the mDateTimeUpdatedFlag to true if the time or date of the object
    // was changed and false if it was not changed
    public void setmDateTimeUpdatedFlag(boolean pDateTimeUpdatedFlagValue)
    {
        mDateTimeUpdatedFlag = pDateTimeUpdatedFlagValue;
    }

    //
    // getmDateTimeUpdatedFlag:
    // returns true if the date or time of the object was updated and false if it was not updated
    public boolean getmDateTimeUpdatedFlag()
    {
        return mDateTimeUpdatedFlag;
    }

    //
    // getmStringDate:
    // returns a string representation of the task's date
    public String getmStringDate()
    {
        return mStringDate;
    }

    //
    // getmPriority:
    // returns a int between 1 and 4 measuring the priority of the task
    public int getmPriority()
    {
        return mPriority;
    }

    //
    // getmTask:
    // returns a String representation of the task
    public String getmTask()
    {
        return mTask;
    }

    //
    // getmStringLocalTime:
    // returns a String representation of the time
    public String getmStringLocalTime()
    {
        return mStringLocalTime;
    }

    //
    // getmTaskId:
    // returns an int containing the TaskId of the task
    public int getmTaskId()
    {
        return mTaskId;
    }

    //
    // setmStringDate:
    // sets the String representation of the date for the object
    public void setmStringDate (String pStringDate)
    {
        mStringDate = pStringDate;
    }

    //
    // setmStringLocalTime:
    // sets the String representation of the time
    public void setmStringLocalTime (String pStringLocalTime)
    {
        mStringLocalTime = pStringLocalTime;
    }

    //
    // setmTask:
    // sets the task String
    public void setmTask(String pTask)
    {
        mTask = pTask;
    }

    //
    // setmRepeat:
    // sets the mRepeat boolean variable to true if the task is to be repeat and false if it is not
    public void setmRepeat(boolean pRepeat)
    {
        mRepeat = pRepeat;
    }

    //
    // setmPriority:
    // sets the priority int of the task
    public void setmPriority(int pPriority)
    {
        mPriority = pPriority;
    }
} // end of CalendarData class

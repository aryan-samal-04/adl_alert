//
// Name:    processTask.java - Timer class for sending messages
// Type:    Java source file
// Program: ADL (Activities of Daily Living) Schedule Alert
// Author:  Aryan Samal
// Version: 1.0
//
// Copyright 2021, This software is confidential and proprietary to the author. Copy, download,
// and reproduction of the software in any form is prohibited unless for academic purposes.
//

//
// Twilio REST API Classes for SMS gateway integration
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

//
// ProcessTask Class:
// Timer class that sends SMS messages at a designated time. Schedules repeated tasks and updates
// new task date in the JTable and database.
class ProcessTask extends TimerTask
{

    private final int mTaskId;
    public static final String ACCOUNT_SID = "AC82712ce7774041da00c74c21776412b8";
    public static final String AUTH_TOKEN = "630508404c28c2420d7fbd49d84543d9";

    //
    // ProcessTask:
    // Constructs each ProcessTask object with a taskID in order to properly identify it
    public ProcessTask(int pTaskId )
    {
        mTaskId = pTaskId;
    }

    //
    // run:
    // Sends SMS message using Twilio REST API to user's phone number once the timer has run out
    public void run()
    {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Using Twilio Message Class, sends SMS message to phone number
        Message message = Message.creator(new PhoneNumber("+19728004944"),
                new PhoneNumber("+19802179005"), MySQLDBHandler.getMessage(mTaskId))
                .create();

        int rowIndex = 0;

        for(int row = 0; row < ADL_ALERT.calendarRows.length; row++)
        {
            if(ADL_ALERT.calendarRows[row] != null && ADL_ALERT.calendarRows[row].getmTaskId() == mTaskId)
            {
                rowIndex = row;
            }
        }

        int tableIndex = 0;
        for(int row = 0; row < ADL_ALERT.mMainTable.getRowCount(); row++)
        {
            if((int) ADL_ALERT.mMainTable.getValueAt(row,5) == mTaskId)
            {
                tableIndex = row;
            }
        }

        if(MySQLDBHandler.getRepeatStatus(mTaskId)==false)
        {
            ADL_ALERT.deleteRowFromJTable(mTaskId);
            MySQLDBHandler.deleteRow(mTaskId);
        }

        else
        {
            System.out.println("Parsing new date for repeat");
            String newDateTime = null;

            try
            {
                newDateTime = (String) ADL_ALERT.calendarRows[rowIndex].getmStringDate();  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(newDateTime));
                c.add(Calendar.DATE, 7);  // number of days to add
                newDateTime = sdf.format(c.getTime());
            }

            catch(Exception e)
            {
                System.out.println("Error while parsing repeat date function");
            }

            if(MySQLDBHandler.updateRow(mTaskId,
                     newDateTime,
                    ADL_ALERT.calendarRows[rowIndex].getmStringLocalTime(),
                    ADL_ALERT.calendarRows[rowIndex].getmTask(),
                    ADL_ALERT.calendarRows[rowIndex].getmRepeat(),
                    ADL_ALERT.calendarRows[rowIndex].getmPriority()))
            {
                System.out.println("Task repeat completed!");
                ADL_ALERT.calendarRows[rowIndex].setmStringDate(newDateTime);
                ADL_ALERT.mMainTable.setValueAt(newDateTime,tableIndex,0);
                ADL_ALERT.calendarRows[rowIndex].setmRowScheduledFlag(false);
                ADL_ALERT.taskScheduler(mTaskId);
            }
        }
    }
}// end of ProcessTask class
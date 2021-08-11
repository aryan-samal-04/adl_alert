//
// Name:    MySQLDBHandler.java - MySQL DB interface file
// Type:    Java source file
// Program: ADL (Activities of Daily Living) Schedule Alert
// Author:  Aryan Samal
// Version: 1.0
//
// Copyright 2020, This software is confidential and proprietary to the author.
//

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

//
// MySQLDBHandler Class:
// Handles all MySQL database interactions including accessing and altering data.
public class MySQLDBHandler
{

    private static int mMaxTaskId = 0;
    private static CalendarData[] mIntialCalendarRows = new CalendarData[50];
    private static int totalRows = 0;
    private static Connection conn = null;

    //
    // Main:
    // Main method for MySQLDBHandler which sets up initial connection to MYSQL database.
    public static void main()
    {

        try
        {
            // connecting to MySQL db
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql?" +
                            "user=Aryan&password=06092004");
            System.out.println("Connection Successful");
        }

        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = conn.createStatement();
            // Reading table data.
            rs = stmt.executeQuery("SELECT task_id, task_date, " +
                    "time_format(task_time,\"%h %i %p \") as task_time, " +
                    "Description, task_repeat, task_priority FROM adl_alert_tbl");

            int i = 0;

            while (rs.next())
            {
                // Populating CalendarData from DB table.
                mIntialCalendarRows[i] = new CalendarData(rs.getString("task_date"),
                         rs.getString("task_time"),
                         rs.getString("Description"),
                         rs.getBoolean("task_repeat"),
                         rs.getInt("task_priority"),
                         rs.getInt("task_id"));
                i++;
            }
            rs.close();
            stmt.close();

        }

        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    //
    // getmInitialCalendarRows:
    // Returns an array of CalendarData objects that represents initial tasks stored in the MYSQL database
    public static CalendarData[] getmIntialCalendarRows()
    {
        return mIntialCalendarRows;
    }

    //
    // getMaxTaskId:
    // returns a int containing the max task id inside the MySQL database
    public static int getMaxTaskId()
    {
        ResultSet rs = null;
        Statement stmt = null;
        int maxTaskId = 0;

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(" SELECT MAX(task_id) as MaxTaskId from adl_alert_tbl");
            if (rs.next())
            {
                maxTaskId = rs.getInt("MaxTaskId");
                System.out.println("Max Task Id: " + maxTaskId);
            }
            rs.close();
            stmt.close();
        }

        catch (SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return maxTaskId;
    }

    //
    // getNumberRows:
    // returns an int containing the number of rows in the database
    public static int getNumberRows()
    {
        try
        {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as numRows FROM adl_alert_tbl");
            if (rs.next())
             {
                 totalRows = rs.getInt("numRows");
                 //System.out.println("Total Rows: " + totalRows);
             }
            rs.close();
            statement.close();
        }

        catch (Exception e)
        {
            System.out.println("Error getting row count");
            e.printStackTrace();
        }

        return totalRows;
    }

    //
    // getDateTime:
    // returns date and time of event
    public static String getDateTime(int pTaskId)
    {
        String DateTime = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT task_id, task_date, " +
                    "task_time, " +
                    "Description, task_repeat, task_priority FROM adl_alert_tbl WHERE task_id = " + pTaskId);

            while(rs.next())
            {
                DateTime = rs.getString("task_date") + " " + rs.getString("task_time");
            }
            rs.close();
            stmt.close();
        }

        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return DateTime;
    }

    //
    // getRepeatStatus:
    // returns true if the task needs to be repeats and false if it does not need to be repeated
    public static boolean getRepeatStatus (int pTaskId)
    {
        Statement stmt = null;
        ResultSet rs = null;
        boolean repeatStatus = false;
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT task_id, task_date, " +
                    "time_format(task_time,\"%h %i %p \") as task_time, " +
                    "Description, task_repeat, task_priority FROM adl_alert_tbl WHERE task_id = " + pTaskId);

            while(rs.next())
            {
                repeatStatus = rs.getBoolean("task_repeat");
            }
            rs.close();
            stmt.close();
        }

        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return repeatStatus;
    }

    //
    // getMessage:
    // Returns String message containing schedule information that is to be sent to phone number
    public static String getMessage(int pTaskId)
    {
        Statement stmt = null;
        ResultSet rs = null;
        String message = null;

        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT task_id, task_date, " +
                    "time_format(task_time,\"%h %i %p \") as task_time, " +
                    "Description, task_repeat, task_priority FROM adl_alert_tbl WHERE task_id = " + pTaskId);

            while (rs.next())
            {
                message = "You have " + (rs.getString("Description"))
                        + " at " + (rs.getString("task_date"))
                        + " " + (rs.getString("task_time"))
                        + " with a priority of " + (rs.getInt("task_priority"));
            }
            rs.close();
            stmt.close();
        }

        catch (SQLException ex)
        {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return message;
    }

    //
    // insertRow:
    // Returns true if row is inserted successfully, otherwise returns false.
    public static boolean insertRow(int pTaskId,
                                    String pStringDate,
                                    String pStringTime,
                                    String pTask,
                                    boolean pRepeat,
                                    int pPriority)
    {
        Statement stmt = null;
        String tmpInsert = null;

        try
        {
            tmpInsert = "INSERT INTO adl_alert_tbl VALUES (" + pTaskId + ", "
                    + "\'" + pStringDate + "\'" + ", " + "str_to_date(" + "\""
                    + pStringTime + "\"" + ", \"%h %i %p\")" + ", " + "\"" + pTask + "\""
                    + ", " + pRepeat + ", " + pPriority + ");";

            stmt = conn.createStatement();
            if (stmt.execute (tmpInsert)==false)
            {
                System.out.println("INSERT successful. " + tmpInsert);
                stmt.close();
                return true;
            }
        }

        catch (SQLException ex)
        {
            System.out.println("INSERT stmt failed: " + tmpInsert);
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }

        return false;
    }

    //
    // deleteRow:
    // returns true if row was deleted successfully, false otherwise
    public static boolean deleteRow(int pTaskId)
    {
        Statement stmt = null;

        try
        {
            stmt = conn.createStatement();
            String tmpDelete = "DELETE FROM adl_alert_tbl where task_id = " + pTaskId;
            if(stmt.execute(tmpDelete)==false)
            {
                return true;
            }
            stmt.close();
        }

        catch (SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }

        return false;
    }

    //
    // updateRow:
    // returns true if row was vd successfully, false otherwise
    public static boolean updateRow(int pTaskId,
                                    String pStringDate,
                                    String pStringTime,
                                    String pTask,
                                    boolean pRepeat,
                                    int pPriority)
    {
        Statement stmt = null;

        try
        {
            String tmpUpdate = "UPDATE adl_alert_tbl SET task_date = \'" + pStringDate +
                    "\', task_time = str_to_date(\"" + pStringTime +
                    "\", \"%h %i %p\"), Description = \'" + pTask +
                    "\', task_repeat = " + pRepeat +
                    ", task_priority = " + pPriority + " WHERE task_id = " + pTaskId ;

            stmt = conn.createStatement();
            if (stmt.execute (tmpUpdate)==false)
            {
                System.out.println("Successfully updated database");
                stmt.close();
                return true;
            }
        }

        catch (SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }

        return false;
    }
} // end of MySQLDBHandler class

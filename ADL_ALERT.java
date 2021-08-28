//
// Name:    ADL_ALERT.java - Main ADL_ALERT Program
// Type:    Java source file
// Program: ADL (Activities of Daily Living) Schedule Alert
// Author:  Aryan Samal
// Version: 1.0
//
// Copyright 2021, This software is confidential and proprietary to the author. Copy, download,
// and reproduction of the software in any form is prohibited unless for academic purposes.
//

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import javax.swing.table.TableColumn;
import java.lang.Object;
import java.time.*;
import java.util.Timer;

//
// ADL_ALERT Class:
// Creates GUI and houses all main functions for creating, deleting, and updating tasks.
public class ADL_ALERT extends JFrame implements ActionListener
{

    private JFrame mMainFrame;
    private JScrollPane mCalendarScrollPane;
    private JPanel mCalendarPanel;
    private JPanel mButtonPanel;
    static JTable mMainTable;
    static DefaultTableModel mModel;
    static Timer timer = new Timer("ADL_ALERT");
    private CellEditorListener ChangeNotification = null;
    private String[] columnNames = { "Date", "Time", "Task", "Repeat", "Priority", "Task ID" };
    static Object[][] mRows = new Object[50][6];
    private static int MAIN_FRAME_WIDTH = 1000;
    private static int MAIN_FRAME_HEIGHT = 650;
    private static ProcessTask[] mTasks = new ProcessTask[50];
    private static int  CALENDAR_SCROLL_PANE_HEIGHT = 750;
    private static int BUTTON_LENGTH = 120;
    private final int MAX_TABLE_SIZE = 50;
    private SimpleDateFormat mDateFormatter;
    private DateTimeFormatter example = DateTimeFormatter.ofPattern("hh:mm a");
    private LocalTime mLocalTime;
    static CalendarData[] calendarRows = new CalendarData[50];
    private int rowCalendarData;

    //
    // ADL_ALERT constructor that creates and populates initial JTable.
    public ADL_ALERT()
    {

        mMainFrame = new JFrame("ADL Alert");
        mCalendarPanel = new JPanel();
        mButtonPanel = new JPanel();
        MySQLDBHandler.main();
        calendarRows = MySQLDBHandler.getmIntialCalendarRows();
        rowCalendarData = MySQLDBHandler.getNumberRows();

        for(int i = 0; i < MySQLDBHandler.getNumberRows(); i++)
        {

            mRows[i] = new Object[]{calendarRows[i].getmStringDate(),
                    calendarRows[i].getmStringLocalTime(),
                    calendarRows[i].getmTask(),
                    calendarRows[i].getmRepeat(),
                    calendarRows[i].getmPriority(),
                    calendarRows[i].getmTaskId()};
        }

        mModel = new DefaultTableModel(mRows, columnNames);
        mMainTable = new JTable(mModel);
        mMainTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mModel.setRowCount(MySQLDBHandler.getNumberRows());
        mModel.setColumnCount(6);
        mMainTable.setSize(new Dimension(MAIN_FRAME_WIDTH, 70));
        mCalendarScrollPane = new JScrollPane(mMainTable);
        mMainFrame.add(mButtonPanel, BorderLayout.SOUTH);
        mMainFrame.add(mCalendarScrollPane);
    }

    //
    // Main method that calls all other relevant methods and creates main JFrame.
    public void main()
    {
        mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mButtonPanel.setLayout( new FlowLayout() );

        this.getContentPane().setLayout(new FlowLayout());

        init();

        mMainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
        mMainFrame.setPreferredSize(new Dimension(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT));
        mMainFrame.setResizable(true);
        mMainFrame.pack();
        mMainFrame.setVisible(true);

        for(int row = 0; row < MySQLDBHandler.getNumberRows(); row++)
        {
            taskScheduler((int)mMainTable.getValueAt(row, 5));
        }
    }

    //
    // actionPerformed:
    // Action method that performs tasks such as updating, deleting, and adding rows from the JTable
    // and MySQL database.
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String action = ae.getActionCommand();
        System.out.println(action);
        int newTaskId = MySQLDBHandler.getMaxTaskId()+1;

        if(action.equals("Add"))
        {
            int i = 0;
            int row = 0;
            boolean foundIndex = true;

            while(foundIndex && i < 50)
            {
                if(calendarRows[i] == null)
                {
                    row = i;
                    foundIndex = false;
                }
                i++;
            }

            System.out.println("Add row index: " + row);
            calendarRows[rowCalendarData] = new CalendarData("2021-01-01",
                    "12 00 AM",
                    "--", true, 1,newTaskId);
            mRows[mMainTable.getRowCount()] = new Object[]{calendarRows[rowCalendarData].getmStringDate(),
                    calendarRows[rowCalendarData].getmStringLocalTime(),
                    calendarRows[rowCalendarData].getmTask(),
                    calendarRows[rowCalendarData].getmRepeat(),
                    calendarRows[rowCalendarData].getmPriority(), newTaskId};
            mModel.addRow(mRows[mMainTable.getRowCount()]);
            calendarRows[rowCalendarData].setmRowInsertedFlag(true);
            rowCalendarData++;
        }

        else if(action.equals("Delete"))
        {
            int selectedRow = mMainTable.getSelectedRow();
            int rowIndex = 0;

            for(int i = 0; i < calendarRows.length; i++)
            {
                if(calendarRows[i] != null && calendarRows[i].getmTaskId() == (int)mMainTable.getValueAt(selectedRow, 5))
                {
                    rowIndex = i;
                }
            }
                if (selectedRow != -1) {
                    // remove selected row from the model
                    System.out.println("Delete row index: " + selectedRow);

                    if(MySQLDBHandler.deleteRow(
                            (Integer.parseInt(mMainTable.getValueAt(selectedRow,5).toString()))))
                    {
                        System.out.println("Deleted from database succesfully.");
                        mModel.removeRow(selectedRow);
                    }

                    if(calendarRows[rowIndex ].getmRowScheduledFlag()==true)
                    {
                        calendarRows[rowIndex ].setmRowScheduledFlag(false);
                        mTasks[calendarRows[rowIndex ].getmTaskId()].cancel();
                        System.out.println("Task Canceled");
                    }

                    calendarRows[rowIndex ] = null;
                    mRows[selectedRow] = null;
                }
            }

        else if(action.equals("Update"))
        {
            for(int rowIndex = 0; rowIndex < MAX_TABLE_SIZE; rowIndex++)
            {
                if(calendarRows[rowIndex] != null)
                {
                    System.out.println("Update RowIndex: " + rowIndex
                            + " InsertFlag: " + calendarRows[rowIndex].getmRowInsertedFlag()
                            + " UpdateFlag: " + calendarRows[rowIndex].getmRowUpdatedFlag());

                    if (calendarRows[rowIndex].getmRowInsertedFlag())
                    {
                        int tableRow = 0;
                        for(int i = 0; i < mMainTable.getRowCount(); i++)
                        {
                            if(calendarRows[rowIndex].getmTaskId() ==
                                    Integer.parseInt(mMainTable.getValueAt(tableRow, 5).toString()))
                            {
                                tableRow = i;
                            }
                        }

                        if (MySQLDBHandler.insertRow(calendarRows[rowIndex].getmTaskId(),
                                calendarRows[rowIndex].getmStringDate(),
                                calendarRows[rowIndex].getmStringLocalTime(),
                                calendarRows[rowIndex].getmTask(),
                                calendarRows[rowIndex].getmRepeat(),
                                calendarRows[rowIndex].getmPriority()))
                        {
                                calendarRows[rowIndex].setmRowInsertedFlag(false);
                        }
                    }

                    if (calendarRows[rowIndex].getmRowUpdatedFlag())
                    {
                        int tableRow = 0;

                        for(int i = 0; i < mMainTable.getRowCount(); i++)
                        {
                            if(calendarRows[rowIndex].getmTaskId() ==
                                    Integer.parseInt(mMainTable.getValueAt(tableRow, 5).toString()))
                            {
                                tableRow = i;
                            }
                        }

                        if (MySQLDBHandler.updateRow(calendarRows[rowIndex].getmTaskId(),
                                calendarRows[rowIndex].getmStringDate(),
                                calendarRows[rowIndex].getmStringLocalTime(),
                                calendarRows[rowIndex].getmTask(),
                                calendarRows[rowIndex].getmRepeat(),
                                calendarRows[rowIndex].getmPriority()))
                        {
                            calendarRows[rowIndex].setmRowUpdatedFlag(false);
                        }

                        if(calendarRows[rowIndex].getmDateTimeUpdatedFlag())
                        {
                            calendarRows[rowIndex].setmRowScheduledFlag(false);
                            taskScheduler(calendarRows[rowIndex].getmTaskId());
                        }

                        if (calendarRows[rowIndex].getmRowScheduledFlag() == false)
                        {
                            ADL_ALERT.taskScheduler(calendarRows[rowIndex].getmTaskId());
                        }
                    }
                }
            }
        }
    }

    //
    // init:
    // creates add, update, and delete buttons and also sets formats for each column in ADL_ALERT
    public void init()
    {
        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        JButton update = new JButton("Update");

        add.addActionListener(this);
        delete.addActionListener(this);
        update.addActionListener(this);

        add.setMinimumSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));
        add.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));
        delete.setMinimumSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));
        delete.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));
        update.setMinimumSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));
        update.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_LENGTH));


        mButtonPanel.add(add);
        mButtonPanel.add(delete);
        mButtonPanel.add(update);


        // Formatting for DateColumn
        TableColumn dateColumn = mMainTable.getColumnModel().getColumn(0);
        DateFormat dateColumnFormat = new SimpleDateFormat("yyyy-mm-dd");
        JFormattedTextField dateTextField = new JFormattedTextField(dateColumnFormat);
        dateTextField.setColumns(8);

        try
        {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            //dobMask.setPlaceholderCharacter('0');
            dateMask.install(dateTextField);
        }

        catch (ParseException ex)
        {
            System.out.println("error");
        }

        dateColumn.setCellEditor(new DefaultCellEditor(dateTextField));

        // Formatting for TimeColumn
        TableColumn timeColumn = mMainTable.getColumnModel().getColumn(1);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h mm a");
        JFormattedTextField timeTextField = new JFormattedTextField(timeFormat);
        timeTextField.setColumns(9);

        try
        {
            MaskFormatter timeMask = new MaskFormatter("## ## AA");
            //dobMask.setPlaceholder('0');
            timeMask.install(timeTextField);
        }
        catch (ParseException ex)
        {
            System.out.println("error");
        }

        timeColumn.setCellEditor(new DefaultCellEditor(timeTextField));

        // Formatting for TaskColumn
        TableColumn taskColumn = mMainTable.getColumnModel().getColumn(2);
        SimpleDateFormat taskFormat = new SimpleDateFormat("");
        JFormattedTextField taskTextField = new JFormattedTextField(taskFormat);
        taskTextField.setColumns(9);
        taskColumn.setCellEditor(new DefaultCellEditor(taskTextField));

        // Formatting for RepeatColumn
        TableColumn repeatColumn = mMainTable.getColumnModel().getColumn(3);
        SimpleDateFormat repeatFormat = new SimpleDateFormat("aaaaa");
        JFormattedTextField repeatTextField = new JFormattedTextField(repeatFormat);
        repeatTextField.setColumns(5);

        try
        {
            MaskFormatter repeatMask = new MaskFormatter("AAAAA");
            //dobMask.setPlaceholder('0');
            repeatMask.install(repeatTextField);
        }

        catch (ParseException ex)
        {
            System.out.println("error");
        }

        repeatColumn.setCellEditor(new DefaultCellEditor(repeatTextField));

        // Formatting for priorityColumn
        TableColumn priorityColumn = mMainTable.getColumnModel().getColumn(4);
        SimpleDateFormat priorityFormat = new SimpleDateFormat("h");
        JFormattedTextField priorityTextField = new JFormattedTextField(priorityFormat);
        priorityTextField.setColumns(2);

        try
        {
            MaskFormatter priorityMask = new MaskFormatter("#");
            //dobMask.setPlaceholder('0');
            priorityMask.install(priorityTextField);
        }

        catch (ParseException ex)
        {
            System.out.println("error");
        }
        priorityColumn.setCellEditor(new DefaultCellEditor(priorityTextField));


        // checks if cell was changed to a different value and if the new cell input
        // is valid/follows the format
        ChangeNotification = new CellEditorListener()
        {
            public void editingCanceled(ChangeEvent e)
            {
                final int row = mMainTable.getSelectedRow();
                final int column = mMainTable.getSelectedColumn();
                //System.out.println("Editing Canceled " + row + " " + column);
                //System.out.println("The user canceled editing.");
            }

            public void editingStopped(ChangeEvent e)
            {
                final int rowSelected = mMainTable.getSelectedRow();
                final int columnSelected = mMainTable.getSelectedColumn();
                System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                        + " Editing Stopped " + rowSelected + " " + columnSelected + " " +
                        mMainTable.getValueAt(rowSelected, columnSelected).toString());
                int rowIndex = 0;

                for(int i =0; i < MAX_TABLE_SIZE; i++)
                {
                    if(calendarRows[i] != null && (Integer.parseInt(mMainTable.getValueAt(rowSelected, 5).toString()) == calendarRows[i].getmTaskId()))
                    {
                        rowIndex = i;
                    }
                }

                if (calendarRows[rowIndex] != null)
                {
                    if (columnSelected == 0)
                    {
                        if (!(isDateValid(dateFormatter(mMainTable.getValueAt(rowSelected, columnSelected).toString())))) {
                            mMainTable.setValueAt(mRows[rowSelected][columnSelected], rowSelected, columnSelected);
                        }
                    }

                    else if (columnSelected == 3)
                    {
                        if (!(mMainTable.getValueAt(rowSelected, columnSelected).toString().equals("true"))
                                && !(mMainTable.getValueAt(rowSelected, columnSelected).toString().equals("false")))
                        {
                            mMainTable.setValueAt(mRows[rowSelected][columnSelected], rowSelected, columnSelected);
                        }
                    }

                    else if (columnSelected == 4)
                    {
                        int priorityInt = Integer.parseInt(mMainTable.getValueAt(rowSelected, columnSelected).toString());
                        if (priorityInt <= 1 || priorityInt >= 4)
                        {
                            mMainTable.setValueAt(mRows[rowSelected][columnSelected], rowSelected, columnSelected);
                        }
                    }

                    if (!(mMainTable.getValueAt(rowSelected, 0).toString())
                            .equals(calendarRows[rowIndex].getmStringDate()))
                    {
                        calendarRows[rowIndex].setmRowUpdatedFlag(true);
                        calendarRows[rowIndex].setmDateTimeUpdatedFlag(true);
                        calendarRows[rowIndex].setmStringDate((String) mMainTable.getValueAt(rowSelected, 0));
                    }

                    if (!(mMainTable.getValueAt(rowSelected, 1).toString()
                            .equals(calendarRows[rowIndex].getmStringLocalTime())))
                    {
                        calendarRows[rowIndex].setmRowUpdatedFlag(true);
                        calendarRows[rowIndex].setmDateTimeUpdatedFlag(true);
                        calendarRows[rowIndex].setmStringLocalTime((String) mMainTable.getValueAt(rowSelected, 1));
                    }

                    if (!(mMainTable.getValueAt(rowSelected, 2).toString()
                            .equals(calendarRows[rowIndex].getmTask())))
                    {
                        calendarRows[rowIndex].setmRowUpdatedFlag(true);
                        calendarRows[rowIndex].setmTask((String) mMainTable.getValueAt(rowSelected, 2));
                    }

                    if (!(mMainTable.getValueAt(rowSelected, 3).toString()
                            .equals(calendarRows[rowIndex].getmRepeat())))
                    {
                        calendarRows[rowIndex].setmRowUpdatedFlag(true);
                        calendarRows[rowIndex].setmRepeat(Boolean.parseBoolean(mMainTable.getValueAt(rowSelected, 3).toString()));
                    }

                    int priorityInt = Integer.parseInt(mMainTable.getValueAt(rowSelected, 4).toString());

                    if (priorityInt != calendarRows[rowIndex].getmPriority()) {
                        calendarRows[rowIndex].setmRowUpdatedFlag(true);
                        calendarRows[rowIndex].setmPriority(Integer.parseInt(mMainTable.getValueAt(rowSelected, 4).toString()));
                    }
                }
            }
        };

        priorityColumn.getCellEditor().addCellEditorListener(ChangeNotification);
        repeatColumn.getCellEditor().addCellEditorListener(ChangeNotification);
        dateColumn.getCellEditor().addCellEditorListener(ChangeNotification);
        timeColumn.getCellEditor().addCellEditorListener(ChangeNotification);
        taskColumn.getCellEditor().addCellEditorListener(ChangeNotification);

        mMainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final int row = mMainTable.getSelectedRow();
                final int column = mMainTable.getSelectedColumn();
                //System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                        //+ " addMouseListener " + row + " " + column);
            }
        });

        mMainTable.addFocusListener(
                new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        mMainTable.setSelectionForeground(Color.WHITE);
                        System.out.println();
                        final int row = mMainTable.getSelectedRow();
                        final int column = mMainTable.getSelectedColumn();
                        //System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                                //+ " Focus Gained " + row + " " + column);
                        //System.out.println(row + " " + column);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        switch (mMainTable.getSelectedColumn())
                        {
                            case 0:
                                TableColumn dobColumn1 = mMainTable.getColumnModel().getColumn(0);
                                DateFormat df1 = new SimpleDateFormat("yyyy-mm-dd");
                                JFormattedTextField tf1 = new JFormattedTextField(df1);
                                tf1.setColumns(10);
                                try {
                                    MaskFormatter dobMask1 = new MaskFormatter("####-##-##");
                                    //dobMask.setPlaceholderCharacter('0');
                                    dobMask1.install(tf1);
                                } catch (ParseException ex) {
                                    System.out.println("error");
                                }
                                dobColumn1.setCellEditor(new DefaultCellEditor(tf1));
                                dobColumn1.getCellEditor().addCellEditorListener(ChangeNotification);
                                break;

                            case 1:
                                System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber());
                                TableColumn timeColumn = mMainTable.getColumnModel().getColumn(1);
                                SimpleDateFormat localTimeFormatter = new SimpleDateFormat("h mm a");
                                JFormattedTextField timeTextField = new JFormattedTextField(localTimeFormatter);
                                timeTextField.setColumns(9);
                                try {
                                    MaskFormatter dobMask = new MaskFormatter("## ##*AA");
                                    //dobMask.setPlaceholderCharacter('0');
                                    dobMask.install(timeTextField);
                                } catch (ParseException ex) {
                                    System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                                            +" ERROR ");
                                    ex.printStackTrace();
                                }
                                timeColumn.setCellEditor(new DefaultCellEditor(timeTextField));
                                timeColumn.getCellEditor().addCellEditorListener(ChangeNotification);
                                break;

                            default:
                                final int row = mMainTable.getSelectedRow();
                                final int column = mMainTable.getSelectedColumn();
                                //System.out.println("Focus Lost " + row + " " + column);
                                //System.out.println(mMainTable.getValueAt(row, column).toString());
                                break;
                        }
                    }
                });
    }

    //
    // isDateValid:
    // checks to see if date inputted into JTable is valid
    public boolean isDateValid(Date pDate)
    {
        if (pDate == null)
        {
            return false;
        }

        if ((pDate.getYear() + 1900) != 2021)
        {
            System.out.println("Year " + pDate.getYear());
            return false;
        }

        else if (!(pDate.getMonth() >= 1) || !(pDate.getMonth() <= 12))
        {
            System.out.println("Month");
            return false;
        }

        else if (!(pDate.getDay() >= 1) || !(pDate.getDay() <= 31))
        {
            System.out.println("Day");
            return false;
        }

        return true;
    }

    //
    // dateFormatter
    // takes in a String and returns a Date object in yyyy-mm-dd format
    public Date dateFormatter(String pStringDate)
    {
        mDateFormatter = new SimpleDateFormat(("yyyy-MM-dd"));

        try
        {
            return mDateFormatter.parse(pStringDate);
        }

        catch(ParseException e)
        {
            System.out.println("error");
        }

        return null;
    }

    //
    // localTimeFormatter:
    // formats LocalTime objects
    public LocalTime localTimeFormatter(String pLocalTime)
    {
        final int row = mMainTable.getSelectedRow();
        final int column = mMainTable.getSelectedColumn();
        DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("h*mm*a");

        try
        {
            return LocalTime.parse(pLocalTime, localTimeFormatter);
        }

        catch(DateTimeParseException e)
        {
            mMainTable.setValueAt(mRows[row][column], row, column);
        }

        return null;
    }

    //
    // isTimeValid:
    // determines if inputted time is valid or not
    public boolean isTimeValid(LocalTime pLocalTime)
    {
        System.out.println("IsTimeValid " + (pLocalTime.getHour() + " " + pLocalTime.getMinute()));
        if(pLocalTime==null)
        {
            return false;
        }

        if(!(pLocalTime.getHour() >= 1) || !(pLocalTime.getHour() <= 24))
        {
            return false;
        }

        if(!(pLocalTime.getMinute() >= 0) || !(pLocalTime.getMinute() <= 60))
        {
            return false;
        }

        return true;
    }

    //
    // deleteRowFromJTable:
    // Method for deleting a row from JTable once task has finished.
    public static void deleteRowFromJTable(int pTaskId)
    {
        for(int row = 0; row < mMainTable.getRowCount(); row++)
        {
            if((int)mMainTable.getValueAt(row,5) == pTaskId)
            {
                mModel.removeRow(row);
            }
        }
        System.out.println("Row Deleted!");
    }

    //
    // taskScheduler:
    // Creates TimerTask for each task in the MySQL database.
    public static void taskScheduler(int pTaskId)
    {
        int row = 0;

        for(int i = 0; i < calendarRows.length; i++)
        {
            if(calendarRows[i] != null && calendarRows[i].getmTaskId() == pTaskId)
            {
                row = i;
            }
        }

            if (calendarRows[row].getmRowScheduledFlag()==false)
            {
                String timeNowString = MySQLDBHandler.getDateTime(pTaskId);
                Date timeNowDate = null;

                try
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    timeNowDate  = dateFormat.parse(timeNowString);
                }

                catch(ParseException e){
                    e.printStackTrace();
                }

                mTasks[pTaskId] = new ProcessTask(pTaskId);
                timer.schedule(mTasks[pTaskId], timeNowDate);
                System.out.println("Task Scheduled for " + timeNowDate);
                calendarRows[row].setmRowScheduledFlag(true);
            }
    }
}





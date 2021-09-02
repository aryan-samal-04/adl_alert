Program: ADL (Activities of Daily Living) Schedule Alert

Author:  Aryan Samal

Version: 1.0

Copyright 2021, This software is confidential and proprietary to the author. Copy, download,
and reproduction of the software in any form is prohibited unless for academic purposes.

Program Overview:

ADL_ALERT is an Activities of Daily Living Alert system primarily designed to
be used for managing disabled person's support activities. Program implements Java GUI,
Java timer and task, and integrates with third-party SMS gateway. It sends SMS message
to handheld at the task scheduled time. Task related data which includes date, time,
priority, and description are populated in MySQL database. In addition to alerting the user
of their daily task, the program also allows for the input of a priority number between
1 and 4, indicating to the user how important their task is. The program also has a repeat
function where the user can choose to auto-repeat a task on the same day the following week, which
relieves reprogramming expired tasks. All scheduling data is managed from GUI using three
buttons: add, delete, and update. The program integrates Twilio SMS gateway using its
REST API for delivering messages to handheld.

Third-Party dependencies:
1. Twilio REST API Classes
2. Twilio Gateway Service - Subscribe SMS service.
3. MySQL DBMS

ADL_ALERT Program Overview:

ADL_ALERT is an Activities of Daily Living Alert system primarily designed to
be used for managing disable person's support activities. Program implements Java GUI,
Java timer and task, and integrates with third-party SMS gateway. It sends SMS message
to handheld at the task scheduled time. Tasks related data which includes data, time,
priority and description are populated in MySQL database. In addition to alerting the user
of their daily task, the program also allows for the input of a priority number between
1 and 4, indicating to the user how important their task is. The program also has a repeat
function where the user can choose to auto-repeat a task same day following week, which
relieves reprogramming expired tasks. All scheduling data is managed from GUI using three
buttons: add, delete, and update. The program integrates Twilio SMS gateway using its
REST API for delivering messages to handheld.

Third-Party dependencies:
1. Twilio REST API Classes
2. Twilio Gateway Service subscription - MUST acruqire Twilio SMS account/phone number.
3. MySQL DBMS

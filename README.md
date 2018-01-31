# CourseCodify
Problem Description:
Nowadays, during the lectures, students take a lot of images (images of the slides, images of friend’s notes), recording audio and take notes on their mobile phone.  And it is cumbersome to organize all the materials because it gets mixed up with other files in the mobile (for example the image taken during the lectures is saved with other pictures taken in some other place and audio recordings get mixed up with the songs in the mobile phone). So, this application provides the students with the functionality to take images, record audio and take notes. The app saves all the materials(images/notes/recordings) on different folder based on the events in their calendar (folder name will be same as the event name) and prevents them to mix up with other resources in the file.
 It allows students to create the events in the calendar and all the materials collected (images/audio recordings/notes) during that period to be stored in the folder. 
It allows the students to share their material with their friends.

The folder structure would be like:

If I have Mobile System Engineering as an Event from 8:30 to 10:30. Folder would look like

Mobile System Engineering > Images
				> Recordings
				> Notes

Functionality:

1.	Audio Recordings: The app has a recorder with which the user can record audio. The has its own player, which is capable to play audio of any size.
2.	Take Images: The app has its own camera that allows the user to capture images.
3.	Take Notes: The app provides the user with a Text Editor that allows the users to create and edit the nodes.
4.	Calendar View: The app has the calendar view that shows the current date’s event. The app also shows the material collected at particular event.

5.	Share Materials: The app allows the user to share images/notes/recordings via social media.

6.	View Materials: The app allows the user to view All Materials, or only Images or only Notes or only Recordings for the particular event. The user can see the number of images, notes and recordings collected for an event.
7.	Material Isolation: The materials are stored in public external storage. Hence the user can view the materials on their laptops, or any other devices but it is protected from getting mixed with the other apps. Like, Google Photos or Gallery App cannot access the images and Google Play cannot access the recordings.
Architecture:

 

First, the app gets all the calendar names present in the google calendar. The calendar names are shown in Settings Activity, where the user can set the preferred calendar. All the events for the selected calendar are retrieved, so that the user can see the events based on the selected date in the calendar view. 
The materials are segregated based on the events, so each material collected during a certain event will be stored in the folder with the same name as the event.
The user can take the images using the camera build specially for this app. If the event already exists, then the image will be stored in the folder (same name as the event). Else, the user will be directed to the calendar where they must create an event. When the event is created the images will be saved inside NewCreatedEventName/images folder.
For notes, the user is provided with an activity where s/he can create/edit notes. If the event exists, then it is saved in that event name folder, if not, user will be directed to Calendar App where s/he must create a new event.
The user can record an audio or play audio captured during certain event. The saving of the recording is similar to that of images and notes.
The view of the material can be done in various ways:
View All Material of a certain event: User can view the list of images, notes, and recordings of a certain event.
Only View Specific kind of Material: User can view only the images or only recordings or only notes of the event. The user will also be provided with the count of images or recordings or notes.
The user is provided with the spinner so that they can view any event they want.
The user can delete the materials from the folder or share it via any social media apps.
The materials are isolated from other apps so that they don’t get mixed up with other resources. However, since the materials are saved in public directory, it can be accessed in other devices like laptops using USB.
The app can handle the situation when there are multiple events at the same time, by providing the user with the option to select the event they want to choose.

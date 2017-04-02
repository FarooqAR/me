# Me – A social Networking App
### Project submitted by: Farooq abdul rehman – Roll # 1381
### Most of the work is in branch "final".. master branch is still incomplete
* [Apk on Google Drive](https://drive.google.com/file/d/0B0SHfYGNhWHwTmdxZmRWTXE5dVk/view?usp=sharing)
* [Firebase](https://app-me.firebaseio.com/)
### This app will use the following libraries/sdks:

* 	Firebase – For Database
*	Cloudinary – For Image Uploading
*	Picasso – For Image Retrieving
*	Android crop – For Image cropping
*	Facebook sdk – For Facebook Authentication
*	Google Play services Plus Api – For Google authentication
*	Google Play services Places Api – For location purposes
*	Support libraries 

Roboto-Medium  is used as the main font of this app.

### As the app opens:
*	User will be first asked for his login information to use the app. There will be three types of authentication:
1.      Firebase Authentication
2.	Facebook Authentication
3.	Google Authentication
![login.png](http://i.imgur.com/sWxnVj7.png)
*	If the user wants to sign up with Email and Password, there will be 4 steps:
* 1.	User have to enter his email and password
2.	User have to enter his first name , last name and age
3.	(Optional) User have to enter his description and country
4.	User has to set his profile picture to finish the step, he can pick a picture from gallery or file manager, he can also choose avatar from the avatars activity. Avatars are uploaded on Imgur.com. Profile image will be uploaded to Cloudinary database and the returned link will be updated in firebase.
*	If the user sign up with Facebook or Google; his first name, last name and his profile image will be used later.
## Main Screen:
*	After the user completes sign up or login process, he will be proceeded to main screen with a navigation drawer from where he can switch between fragments.
*	Navigation drawer is made according to material design guidelines. 
*	In navigation drawer, there is a header for user information and recyclerview for showing fragments list.
![main_screen_drawer.png](http://i.imgur.com/VR3j4bl.png)
## Friends:
*	First thing to show to user will be his friends. If he don’t have any friends he can click on the given button to find a contact
*	If he have friends, he can remove one by clicking on a button adjacent to that user
*	As he go to find a contact, he can search a user by first name or last name and if user with given filters exists, it will be shown in the recyclerview.
*	If someone has already sent a friend request. User can confirm him as friend.
*	If user has sent a friend request to someone. “Request sent” will be shown.
![friends.png](http://i.imgur.com/ejaZwA7.png)
## Settings
*	User can change his email, password, first name, last name, profile image, description, age and country in Settings fragment.
*	If the user is authenticated with facebook or google, he can’t change his email or password because this app don’t take email or password or any sensitive data.
![settings.png](http://i.imgur.com/fkHlJyg.png)
## Private Chat:
*	In Chat fragment, there is a list of friends with their online status. user can switch between conversation by clicking on a friend item.
*	Friends list will appear on left side of the screen containing user icon and name (in landscape).
*	If the user is online, a green dot will appear on the top left corner of a user’s image.
*	User can send a normal message, image or a location
*       A location in message will be shown as a red marker, clicking on it will open up the Maps Activity.
![chat.png](http://i.imgur.com/R3ADFF2.png)
## Private Chat Notifications:
*	For notifications, app will use a service with broadcast receiver.
*	If user is on a fragment other than chat. user will get notified if his friend sent a message.  notifications will appear on status bar, clicking a notification will open chat fragment 
*	If user leave the app, user will still get notifications for messages sent by his/her friends
*	If the app is completely closed, service will also get destroyed with it. 
## Groups:
*	User can see all the groups in Groups fragment; he can join a group or create one.
*	User can search a group by its name.
*	If user joins a group, a request will be sent to its admin who can add him as a member or just ignore his request.
*	Clicking on floating action button will open dialog where user will be prompted for group’s name and description. He will be added as a member with access level admin.
*	Opening a group will start group activity:
*	If the user is not member of that group, he can only see its members
*	If the user is just a member of that group, he can chat as well as see members
*	If the user is admin of that group, he can see its chat, members and member requests.
*	Admin can remove a member, add a member
![groups.png](http://i.imgur.com/1omaKRA.png)

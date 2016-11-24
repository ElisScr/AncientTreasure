# AncientTreasure






Exam Project
“ANCIENT TREASURES”
ICT Engineering
Team:
	
Lilian Kumor 	          	
Elisabeta Scriba             

Course: IT-MOB2-S17  
Supervisor:
Kian Thusgaard Larsen

General description

Ancient Treasures is a Treasure hunt game type with one or more players who will try to find hidden artefacts.
In order to play Ancient Treasures, a player needs to have at least 2 Bluetooth low energy devices (iBeacon in our game, that are hardcoded to indicate the proximity), the same amount of Nfc tags and a Bluetooth server nearby. 
Because we are not using a database, the login part is just a name that is passed from the login activity to the second activity and all the achievements in the game will not be stored.
 

As interfaces, the app is using iBeacon (Bluetooth low energy), Nfc and Bluetooth.

The purpose for using iBeacon is to get the proximity, helping the player to find easier an artefact by getting a hint (COLD, WARM, OR HOT).


When the hint shows HOT, the player should be close enough to see the artefact.

The Nfc is used to grab the artefact attached to each iBeacon.
Each Nfc tag shows the message “Congratulation! You got a rare artefact “and a counter shows how many artefacts a player has.



When a player found all artefacts, it will get the possibility to send an info text to a Bluetooth server by pressing the SEND button.
A feedback message from Bluetooth server will pop up with how many bytes were received and the confirmation of the original message.

Ancient Treasures may be an indoor or outdoor activity. Outdoor it can be played in a garden or a not so noisy place, because the proximity will lose its accuracy.


 

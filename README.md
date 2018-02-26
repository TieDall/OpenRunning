# OpenRunning
Ziel des Projektes „OpenRunning“ ist die Entwicklung einer Open Source AndroidApplikation. Die Anwendung soll eine Plattform für Läufer zum gemeinsamen Austausch bieten. Nutzer können ihre eigenen Laufstrecken hinzufügen und erhalten gleichermaßen Einsicht in die Strecken anderer Nutzer. 
## Getting Started
### Aufsetzen MySQL-Datenbank
```
SQL-Skript
```
### Aufsetzen Webserver
Folgende Dateien müssen erstellt und durch den Webserver zur Verfügung gestellt werden (BEACHTE: Dateien müssen sich im Root-Verzeichnis befinden!):

conn.php - Hierbei müssen der Datenbank-Benutzername, das Datenbank-Passwort, sowie die IP der Datenbank eingetragen werden.
```
<?php
	$db_name = "openrunning";
	$mysql_username = "root";
	$mysql_password = "";
	$server_name = "localhost";
	
	$conn = mysqli_connect ($server_name, $mysql_username, $mysql_password, $db_name);
?>
```

route_add.php
```
<?php
	require "conn.php";
	
	$creator = $_POST["creator"];
	$describtion = $_POST["describtion"];
	$length = $_POST["length"];
	$rating_count = $_POST["rating_count"];
	$rating_average = $_POST["rating_average"];
	$waypoints = $_POST["waypoints"];
	
	
	
	$mysql_qry = "INSERT INTO `strecken`(`Ersteller`, `Beschreibung`, `Streckenlaenge`, `Anzahl_Bewertungen`, `Durchschnittsbewertung`, `Wegpunkte`) VALUES ('$creator','$describtion','$length','$rating_count','$rating_average','$waypoints');";
		
	if ($conn->query($mysql_qry) === TRUE) {
		echo "New record created successfully";
	} else {
		echo "Error: " . $mysql_qry . "<br>" . $conn->error;
	}
 ?>
```
### Anpassung BackgroundWorker.java
In der Klasse "BackgroundWorker.java" ist der String "url" zu ersetzen mit der IP der Datenbank.
```
Codesnippet
```

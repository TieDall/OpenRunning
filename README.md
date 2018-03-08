# OpenRunning
Ziel des Projektes „OpenRunning“ ist die Entwicklung einer Open Source AndroidApplikation. Die Anwendung soll eine Plattform für Läufer zum gemeinsamen Austausch bieten. Nutzer können ihre eigenen Laufstrecken hinzufügen und erhalten gleichermaßen Einsicht in die Strecken anderer Nutzer. 
## Getting Started
### Aufsetzen MySQL-Datenbank
```
drop DATABASE if EXISTS openrunning;
create DATABASE if not exists openrunning;

create table if not exists Personen (
	BID int not null AUTO_INCREMENT,
    	Benutzertyp int(1),
   	Benutzername varchar(15),
    	Mailadresse varchar(30),
	Passworthash varchar(500),
    	Favoriten varchar(50) DEFAULT '',
    	PRIMARY KEY (BID)
);

create table if not EXISTS Strecken (
	SID int not null AUTO_INCREMENT,
    	Ersteller int,
    	Beschreibung varchar(500) DEFAULT '',
    	Streckenlaenge double,
    	Anzahl_Bewertungen int DEFAULT 0,
    	Durchschnittsbewertung double DEFAULT 0,
   	Wegpunkte varchar(500),
	Streckenstatus int(1),
   	PRIMARY KEY(SID),
    	FOREIGN KEY(Ersteller) REFERENCES Personen(BID) on delete cascade
);
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

getHash.php
```
<?php
	require "conn.php";

	$user_name = $_POST["username"];

	$mysql_qry = "select Passworthash from Personen where Benutzername like '$user_name';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["Passworthash"];
		}
	}
	else {
	  echo "no user";
	}
 ?>
```

getRoutes.php
```
<?php
	require "conn.php";

	$length_min = $_POST["length_min"];
  	$length_max = $_POST["length_max"];
	$rating = $_POST["rating"];

	$mysql_qry = "select SID from Strecken where Streckenlaenge <= '$length_max' and Streckenlaenge >= '$length_min' and Durchschnittsbewertung >= '$rating';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["SID"]. "_";
		}
	}
	else {
	  echo "error";
	}
 ?>
```

getUser_info.php
```
<?php
	require "conn.php";

	$user_name = $_POST["username"];
	$mailadresse = $_POST["mailadresse"];

	$mysql_qry = "select Benutzername from Personen where Benutzername like '$user_name' or Mailadresse = '$mailadresse';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["Benutzername"];
		}
	}
	else {
	  echo "";
	}
 ?>
 ```
getUser_update.php
 ```
 <?php
	require "conn.php";

	$user_type = $_POST["bid"];

	$mysql_qry = "select Benutzertyp from Personen where BID like '$user_type';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["Benutzertyp"];
		}
	}
	else {
	  echo "not found";
	}
 ?>
 ```
 
login.php
 ```
 <?php
	require "conn.php";

	$user_name = $_POST["username"];
	$user_pass = $_POST["password"];

	$mysql_qry = "select BID, Benutzertyp from Personen where Benutzername like '$user_name' and Passworthash = '$user_pass';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["BID"]. "_" . $row["Benutzertyp"];
		}
	}
	else {
	  echo "login not success";
	}
 ?>
 ```
 
register.php
 ```
 <?php
	require "conn.php";
	$user_name = $_POST["username"];
	$user_pass = $_POST["password"];
	$Mailadresse = $_POST["mailadresse"];
	$mysql_qry = "insert into Personen (Benutzertyp, Benutzername, Mailadresse, Passworthash) values (0, '$user_name','$Mailadresse','$user_pass');";

	if ($conn->query($mysql_qry) === TRUE){
  		echo "Insert Succesfull";
	}
	else {
  		echo "Error: " . $mysql_qry . "<br>" . $conn->error;
	}
 ?>
 ```
 
removeUser.php
 ```
<?php
	require "conn.php";

	$user_name = $_POST["username"];

	$mysql_qry1 = "select Benutzername from Personen where Benutzername like '$user_name';";
	$mysql_qry2 = "delete from personen where Benutzername like '$user_name';";

	$result = mysqli_query ($conn ,$mysql_qry1);
	mysqli_query ($conn ,$mysql_qry2);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["Benutzername"];
		}
	}
	else {
		echo "not found";
	}
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



	$mysql_qry = "INSERT INTO `strecken`(`Ersteller`, `Beschreibung`, `Streckenlaenge`, `Anzahl_Bewertungen`, `Durchschnittsbewertung`, `Wegpunkte`, `Streckenstatus`) VALUES ('$creator','$describtion','$length','$rating_count','$rating_average','$waypoints', 0);";

	if ($conn->query($mysql_qry) === TRUE) {
		echo "New record created successfully";
	} else {
		echo "Error: " . $mysql_qry . "<br>" . $conn->error;
	}
 ?>
```

route_info.php
```
<?php
	require "conn.php";

	$id = $_POST["id"];

	$mysql_qry = "select Ersteller, Beschreibung, Streckenlaenge, Anzahl_Bewertungen, Durchschnittsbewertung, Wegpunkte from strecken where SID = '$id';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["Ersteller"]. "?" . $row["Beschreibung"]. "?" . $row["Streckenlaenge"]. "?" . $row["Anzahl_Bewertungen"]. "?" . $row["Durchschnittsbewertung"]. "?" . $row["Wegpunkte"];
		}
	}
	else {
	  echo "failure";
	}
 ?>
```

setRouteStatus.php
```
<?php
	require "conn.php";

	$sid = $_POST["sid"];
	$status = $_POST["status"];


	$mysql_qry = "update strecken Set Streckenstatus = '$status' where SID = '$sid';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if ($conn->query($mysql_qry) === TRUE) {
		echo "erfolgreich";
	}
	else {
	  echo "failure";
	}
 ?>
 ```
 
deleteRoute.php
```
<?php
	require "conn.php";

	$sid = $_POST["sid"];

	$mysql_qry = "delete from strecken where SID like '$sid';";

	$result = mysqli_query ($conn ,$mysql_qry);

  if ($conn->query($mysql_qry) === TRUE) {
		echo "erfolgreich";
	}
	else {
		echo "error";
	}
 ?>
``` 

getRoutetoStatus.php
```
<?php
	require "conn.php";

	$status = $_POST["status"];

	$mysql_qry = "select SID from strecken where Streckenstatus like '$status';";

	$result = mysqli_query ($conn ,$mysql_qry);

	if (mysqli_num_rows($result) > 0){
		while($row = $result->fetch_assoc()) {
			echo $row["SID"]. "_";
		}
	}
	else {
	  echo "no report Routes";
	}
 ?>
 ```

### Anpassung DBHandler.java
In der Klasse "DBHandler.java" ist der String "DB_IP_ADDRESS" zu ersetzen mit der IP der Datenbank und ggf. "DB_PROTOCOL".
```
// Change variables below for configuration
private static final String DB_PROTOCOL = "http";
private static final String DB_IP_ADDRESS = "192.168.178.20";
```

### Anpassung CreateRouteActivity.java, DeleteRouteActivity.java, SearchActivity.java, SearchResultActivity.java
In den übersteheneden Klassen muss jeweils folgende Variable angepasst werden "private String apiKey". Der Key wird zur Streckenberechnung benötigt. Hierfür muss man sich auf "http://developer.mapquest.com" registrieren. Der Key, der in den Klassen steht, läuft am 25.03.2018 ab.

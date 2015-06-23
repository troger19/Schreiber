BEGIN TRANSACTION;
CREATE TABLE `table1` (
	`id`	INTEGER,
	`slovak`	TEXT,
	`german`	TEXT,
	`topic`	TEXT,
	`skill`	INTEGER,
	PRIMARY KEY(id)
);
INSERT INTO `table1` VALUES(1,'ahoj','Hallo','Greetings',0);
INSERT INTO `table1` VALUES(2,'dovidenia','auf Wiedersehn','Greetings',0);
COMMIT;

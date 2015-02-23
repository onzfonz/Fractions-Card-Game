create schema tug

CREATE TABLE tug.users (uid INT NOT NULL CONSTRAINT uid_pk PRIMARY KEY, uname VARCHAR(20) NOT NULL, 
gender SMALLINT CONSTRAINT gender_ck CHECK (gender IN (0, 1)), treatment CHAR(1) CONSTRAINT treatment_ck CHECK (treatment in ('A', 'B')),
u3cset SMALLINT, u3frac SMALLINT, u4cset SMALLINT, u4frac SMALLINT, u4ela SMALLINT, 
aq1 SMALLINT CONSTRAINT aq1_ck CHECK (aq1 IN (0, 1)),
aq2 SMALLINT CONSTRAINT aq2_ck CHECK (aq2 IN (0, 1)), aq3 SMALLINT CONSTRAINT aq3_ck CHECK (aq3 IN (0, 1)), 
aq4 SMALLINT CONSTRAINT aq4_ck CHECK (aq4 IN (0, 1)), aq5 SMALLINT CONSTRAINT aq5_ck CHECK (aq5 IN (0, 1)), 
aq6 SMALLINT CONSTRAINT aq6_ck CHECK (aq6 IN (0, 1)), aq7 SMALLINT CONSTRAINT aq7_ck CHECK (aq7 IN (0, 1)), 
aq8 SMALLINT CONSTRAINT aq8_ck CHECK (aq8 IN (0, 1)), aq9 SMALLINT CONSTRAINT aq9_ck CHECK (aq9 IN (0, 1)), 
aq10 SMALLINT CONSTRAINT aq10_ck CHECK (aq10 IN (0, 1)), aq11 SMALLINT CONSTRAINT aq11_ck CHECK (aq11 IN (0, 1)), 
aq12 SMALLINT CONSTRAINT aq12_ck CHECK (aq12 IN (0, 1)), aq13 SMALLINT CONSTRAINT aq13_ck CHECK (aq13 IN (0, 1)), 
aq14 SMALLINT CONSTRAINT aq14_ck CHECK (aq14 IN (0, 1)), aq15 SMALLINT CONSTRAINT aq15_ck CHECK (aq15 IN (0, 1)), 
aq16 SMALLINT CONSTRAINT aq16_ck CHECK (aq16 IN (0, 1)), aq17 SMALLINT CONSTRAINT aq17_ck CHECK (aq17 IN (0, 1)), 
aq18 SMALLINT CONSTRAINT aq18_ck CHECK (aq18 IN (0, 1)), aq19 SMALLINT CONSTRAINT aq19_ck CHECK (aq19 IN (0, 1)), 
aq20 SMALLINT CONSTRAINT aq20_ck CHECK (aq20 IN (0, 1)), aq21 SMALLINT CONSTRAINT aq21_ck CHECK (aq21 IN (0, 1)),
aq22 SMALLINT CONSTRAINT aq22_ck CHECK (aq22 IN (0, 1)), aqtot SMALLINT, mq1 SMALLINT CONSTRAINT mq1_ck CHECK (mq1 IN (0, 1)),
mq2 SMALLINT CONSTRAINT mq2_ck CHECK (mq2 IN (0, 1)), mq3 SMALLINT CONSTRAINT mq3_ck CHECK (mq3 IN (0, 1)), 
mq4 SMALLINT CONSTRAINT mq4_ck CHECK (mq4 IN (0, 1)), mq5 SMALLINT CONSTRAINT mq5_ck CHECK (mq5 IN (0, 1)), 
mq6 SMALLINT CONSTRAINT mq6_ck CHECK (mq6 IN (0, 1)), mq7 SMALLINT CONSTRAINT mq7_ck CHECK (mq7 IN (0, 1)), 
mq8 SMALLINT CONSTRAINT mq8_ck CHECK (mq8 IN (0, 1)), mq9 SMALLINT CONSTRAINT mq9_ck CHECK (mq9 IN (0, 1)), 
mq10 SMALLINT CONSTRAINT mq10_ck CHECK (mq10 IN (0, 1)), mq11 SMALLINT CONSTRAINT mq11_ck CHECK (mq11 IN (0, 1)), 
mq12 SMALLINT CONSTRAINT mq12_ck CHECK (mq12 IN (0, 1)), mq13 SMALLINT CONSTRAINT mq13_ck CHECK (mq13 IN (0, 1)), 
mq14 SMALLINT CONSTRAINT mq14_ck CHECK (mq14 IN (0, 1)), mq15 SMALLINT CONSTRAINT mq15_ck CHECK (mq15 IN (0, 1)), 
mq16 SMALLINT CONSTRAINT mq16_ck CHECK (mq16 IN (0, 1)), mq17 SMALLINT CONSTRAINT mq17_ck CHECK (mq17 IN (0, 1)), 
mq18 SMALLINT CONSTRAINT mq18_ck CHECK (mq18 IN (0, 1)), mq19 SMALLINT CONSTRAINT mq19_ck CHECK (mq19 IN (0, 1)), 
mq20 SMALLINT CONSTRAINT mq20_ck CHECK (mq20 IN (0, 1)), mq21 SMALLINT CONSTRAINT mq21_ck CHECK (mq21 IN (0, 1)),
mq22 SMALLINT CONSTRAINT mq22_ck CHECK (mq22 IN (0, 1)), mqtot SMALLINT, zq1 SMALLINT CONSTRAINT zq1_ck CHECK (zq1 IN (0, 1)),
zq2 SMALLINT CONSTRAINT zq2_ck CHECK (zq2 IN (0, 1)), zq3 SMALLINT CONSTRAINT zq3_ck CHECK (zq3 IN (0, 1)), 
zq4 SMALLINT CONSTRAINT zq4_ck CHECK (zq4 IN (0, 1)), zq5 SMALLINT CONSTRAINT zq5_ck CHECK (zq5 IN (0, 1)), 
zq6 SMALLINT CONSTRAINT zq6_ck CHECK (zq6 IN (0, 1)), zq7 SMALLINT CONSTRAINT zq7_ck CHECK (zq7 IN (0, 1)), 
zq8 SMALLINT CONSTRAINT zq8_ck CHECK (zq8 IN (0, 1)), zq9 SMALLINT CONSTRAINT zq9_ck CHECK (zq9 IN (0, 1)), 
zq10 SMALLINT CONSTRAINT zq10_ck CHECK (zq10 IN (0, 1)), zq11 SMALLINT CONSTRAINT zq11_ck CHECK (zq11 IN (0, 1)), 
zq12 SMALLINT CONSTRAINT zq12_ck CHECK (zq12 IN (0, 1)), zq13 SMALLINT CONSTRAINT zq13_ck CHECK (zq13 IN (0, 1)), 
zq14 SMALLINT CONSTRAINT zq14_ck CHECK (zq14 IN (0, 1)), zq15 SMALLINT CONSTRAINT zq15_ck CHECK (zq15 IN (0, 1)), 
zq16 SMALLINT CONSTRAINT zq16_ck CHECK (zq16 IN (0, 1)), zq17 SMALLINT CONSTRAINT zq17_ck CHECK (zq17 IN (0, 1)), 
zq18 SMALLINT CONSTRAINT zq18_ck CHECK (zq18 IN (0, 1)), zq19 SMALLINT CONSTRAINT zq19_ck CHECK (zq19 IN (0, 1)), 
zq20 SMALLINT CONSTRAINT zq20_ck CHECK (zq20 IN (0, 1)), zq21 SMALLINT CONSTRAINT zq21_ck CHECK (zq21 IN (0, 1)),
zq22 SMALLINT CONSTRAINT zq22_ck CHECK (zq22 IN (0, 1)), zqtot SMALLINT);

ALTER TABLE tug.users ADD CONSTRAINT uname_unique UNIQUE (uname)



insert into tug.users values (1, 'Osvaldo');
select * from tug.users;


CREATE TABLE tug.cards (cid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, 
cistrick SMALLINT CONSTRAINT cist_ck CHECK (cistrick IN (0, 1)), ctype VARCHAR(5), content VARCHAR(30), 
cnum SMALLINT NOT NULL, cden SMALLINT, cdecimal SMALLINT CONSTRAINT cdecimal_ck CHECK (cdecimal IN (0, 1)));

insert into tug.cards (cistrick, ctype, content, cnum, cden, cdecimal) values (0, 'team', 'The Twins', 2, null, 0);


CREATE TABLE tug.questions(qid INT NOT NULL CONSTRAINT qid_pk PRIMARY KEY, question VARCHAR(30) NOT NULL, qans SMALLINT NOT NULL, 
qnum SMALLINT NOT NULL, qden SMALLINT NOT NULL, qval SMALLINT NOT NULL, qdecimal SMALLINT CONSTRAINT qdecimal_ck CHECK (qdecimal IN (0, 1)));

CREATE TABLE tug.pairs (pid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, uid1 INT, uid2 INT);

CREATE TABLE tug.games (gid INT PRIMARY KEY, pid1 INT NOT NULL, pid2 INT NOT NULL, pstart TIMESTAMP NOT NULL, pend TIMESTAMP NOT NULL);

CREATE TABLE tug.userlogs (ulogid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, uid INT NOT NULL, qaid INT NOT NULL, uorder SMALLINT NOT NULL, qid INT NOT NULL, ulogtime TIMESTAMP, ulogattempt SMALLINT, ulogtype VARCHAR(15), ulogppl SMALLINT, uloglines SMALLINT, ulogmarks SMALLINT, ulogshown SMALLINT CONSTRAINT ulogshown_ck CHECK (ulogshown IN (0, 1)));

CREATE TABLE tug.pairlogs (plogid INT PRIMARY KEY, gid INT NOT NULL, pid INT NOT NULL, porder INT NOT NULL, qaid INT, plogtime TIMESTAMP NOT NULL, plogtype VARCHAR(20), plogcontent VARCHAR(250));
DELETE FROM tug.pairlogs;
DROP TABLE tug.pairlogs;

CREATE TABLE tug.statelogs (slogid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, gid INT NOT NULL, plogid INT NOT NULL, p1teamid0 INT, p1teamid1 INT, p2teamid0 INT, p2teamid1 INT, p1numleft0 INT, p1numleft1 INT, p2numleft0 INT, p2numleft1 INT, p1trickid0 INT, p1trickid1 INT, p1trickid2 INT, p1trickid3 INT, p2trickid0 INT, p2trickid1 INT, p2trickid2 INT, p2trickid3 INT);

SELECT pid from tug.pairs WHERE uid1 = (SELECT uid from tug.users where uname = 'Christian') AND uid2 = (SELECT uid from tug.users where uname = null);
SELECT pid from tug.pairs WHERE uid1 = (SELECT uid from tug.users where uname = 'Christian') AND uid2 is null;
SELECT pid from tug.pairs WHERE uid2 is null;


select uid, u3cset, u4cset, mqtot, zqtot from tug.users where treatment = 'B';
select qid, uid from tug.userlogs where uid = 223 and ulogtype = 'QShown';
select qid from tug.userlogs where uid = 214 AND ulogtype = 'QStarted' AND qid IN (select qid from tug.questions where qans = -1);
select * from tug.users where uid = 214;
select * from tug.userlogs where uid = 214 AND ulogtype = 'QStarted';
select qid from tug.questions where qans = -1;
select qid, attempt from tug.userlogs where uid = 214 AND ulogtype = 'QTried';
select gid from tug.games where pid1 IN (select pid from tug.pairs where uid1 = 214 OR uid2 = 214) OR pid2 IN (select pid from tug.pairs where uid1 = 214 OR uid2 = 214);
select qid from tug.userlogs where ulogtype = 'QDone';

select * from tug.userlogs where uid = 214 and qaid in (select qaid from tug.pairlogs where plogtype = 'QuestionAns' and gid in (select gid from tug.games where gid >= 1 and gid <= 4 and (pid1 IN (select pid from tug.pairs where uid1 = 214 OR uid2 = 214) OR pid2 IN (select pid from tug.pairs where uid1 = 214 OR uid2 = 214))));
select * from tug.userlogs where qid = -304012;
select * from tug.userlogs where qid = -10208 and ulogtype = 'QTried';

select * from tug.userlogs where qaid in (select distinct qaid from tug.pairlogs where qaid is not null and plogcontent like '%Shadow%') and uid = 214;
select uid, zq12, zq13, zq14, zq15, zq16, zq17, mqtot, zqtot from tug.users where treatment = 'B';
select uid, (mq12 + mq13 + mq14 + mq15 + mq16 + mq17), (zq12 + zq13 + zq14 + zq15 + zq16 + zq17), mqtot, zqtot from tug.users where treatment = 'B';
create schema gar

CREATE TABLE gar.users (uid INT NOT NULL CONSTRAINT uid_pk PRIMARY KEY, uname VARCHAR(20) NOT NULL, 
lastname VARCHAR(20) NOT NULL, teacher CHAR(1) CONSTRAINT teacher_ck CHECK (teacher in ('A', 'J', 'D')),
instudy SMALLINT CONSTRAINT instudy_ck CHECK (instudy IN (0, 1)), hasprepost SMALLINT CONSTRAINT hasprepost_ck CHECK (hasprepost IN (0, 1)),
notes VARCHAR(50), gender SMALLINT CONSTRAINT gender_ck CHECK (gender IN (0, 1)), camath SMALLINT, 
mathlev CHAR(1) CONSTRAINT mathlev_ck CHECK (mathlev IN ('A', 'B', 'C')), 
speced SMALLINT CONSTRAINT speced_ck CHECK (speced IN (0, 1)), 
englishprof SMALLINT CONSTRAINT englishprof_ck CHECK (englishprof IN (1, 2, 3, 4)),
islatino SMALLINT CONSTRAINT islatino_ck CHECK (islatino IN (0, 1)),
treatment CHAR(1) CONSTRAINT treatment_ck CHECK (treatment in ('A', 'B', 'C')),
group1 SMALLINT CONSTRAINT group1_ck CHECK (group1 in (1, 2, 3, 4, 5, 6, 7)),
side1 SMALLINT CONSTRAINT side1_ck CHECK (side1 IN (1, 2)),
group2 SMALLINT CONSTRAINT group2_ck CHECK (group2 in (1, 2, 3, 4, 5, 6, 7)),
side2 SMALLINT CONSTRAINT side2_ck CHECK (side2 IN (1, 2)),
group3 SMALLINT CONSTRAINT group3_ck CHECK (group3 in (1, 2, 3, 4, 5, 6, 7)),
side3 SMALLINT CONSTRAINT side3_ck CHECK (side3 IN (1, 2)),
pre1 SMALLINT CONSTRAINT pre1_ck CHECK (pre1 IN (0, 1)),
pre2 SMALLINT CONSTRAINT pre2_ck CHECK (pre2 IN (0, 1)), pre3 SMALLINT CONSTRAINT pre3_ck CHECK (pre3 IN (0, 1)), 
pre4 SMALLINT CONSTRAINT pre4_ck CHECK (pre4 IN (0, 1)), pre5 SMALLINT CONSTRAINT pre5_ck CHECK (pre5 IN (0, 1)), 
pre6 SMALLINT CONSTRAINT pre6_ck CHECK (pre6 IN (0, 1)), pre7 SMALLINT CONSTRAINT pre7_ck CHECK (pre7 IN (0, 1)), 
pre8 SMALLINT CONSTRAINT pre8_ck CHECK (pre8 IN (0, 1)), pre9 SMALLINT CONSTRAINT pre9_ck CHECK (pre9 IN (0, 1)), 
pre10 SMALLINT CONSTRAINT pre10_ck CHECK (pre10 IN (0, 1)), pre11 SMALLINT CONSTRAINT pre11_ck CHECK (pre11 IN (0, 1)), 
pre12 SMALLINT CONSTRAINT pre12_ck CHECK (pre12 IN (0, 1)), pre13 SMALLINT CONSTRAINT pre13_ck CHECK (pre13 IN (0, 1)), 
pre14 SMALLINT CONSTRAINT pre14_ck CHECK (pre14 IN (0, 1)), pre15 SMALLINT CONSTRAINT pre15_ck CHECK (pre15 IN (0, 1)), 
pre16 SMALLINT CONSTRAINT pre16_ck CHECK (pre16 IN (0, 1)), pre17 SMALLINT CONSTRAINT pre17_ck CHECK (pre17 IN (0, 1)), 
pre18 SMALLINT CONSTRAINT pre18_ck CHECK (pre18 IN (0, 1)), pre19 SMALLINT CONSTRAINT pre19_ck CHECK (pre19 IN (0, 1)), 
pre20 SMALLINT CONSTRAINT pre20_ck CHECK (pre20 IN (0, 1)), pre21 SMALLINT CONSTRAINT pre21_ck CHECK (pre21 IN (0, 1)),
pre22a SMALLINT CONSTRAINT pre22a_ck CHECK (pre22a IN (0, 1)), pre22b SMALLINT CONSTRAINT pre22b_ck CHECK (pre22b IN (0, 1)), 
pre22c SMALLINT CONSTRAINT pre22c_ck CHECK (pre22c IN (0, 1)), pre22d SMALLINT CONSTRAINT pre22d_ck CHECK (pre22d IN (0, 1)),
pre22e SMALLINT CONSTRAINT pre22e_ck CHECK (pre22e IN (0, 1)), pre22f SMALLINT CONSTRAINT pre22f_ck CHECK (pre22f IN (0, 1)),
pre23a SMALLINT CONSTRAINT pre23a_ck CHECK (pre23a IN (0, 1)), pre23b SMALLINT CONSTRAINT pre23b_ck CHECK (pre23b IN (0, 1)), 
pre23c SMALLINT CONSTRAINT pre23c_ck CHECK (pre23c IN (0, 1)), pre23d SMALLINT CONSTRAINT pre23d_ck CHECK (pre23d IN (0, 1)),
pre23e SMALLINT CONSTRAINT pre23e_ck CHECK (pre23e IN (0, 1)), pre24 SMALLINT CONSTRAINT pre24_ck CHECK (pre24 IN (0, 1)), 
pre25 SMALLINT CONSTRAINT pre25_ck CHECK (pre25 IN (0, 1)), pre26 SMALLINT CONSTRAINT pre26_ck CHECK (pre26 IN (0, 1)),
pre27a SMALLINT CONSTRAINT pre27a_ck CHECK (pre27a IN (0, 1)), pre27b SMALLINT CONSTRAINT pre27b_ck CHECK (pre27b IN (0, 1)), 
pre27c SMALLINT CONSTRAINT pre27c_ck CHECK (pre27c IN (0, 1)),
pre28a SMALLINT CONSTRAINT pre28a_ck CHECK (pre28a IN (0, 1)), pre28b SMALLINT CONSTRAINT pre28b_ck CHECK (pre28b IN (0, 1)), 
pre28c SMALLINT CONSTRAINT pre28c_ck CHECK (pre28c IN (0, 1)), pre28d SMALLINT CONSTRAINT pre28d_ck CHECK (pre28d IN (0, 1)),
pretot SMALLINT, 
p1 SMALLINT CONSTRAINT p1_ck CHECK (p1 IN (0, 1)),
p2 SMALLINT CONSTRAINT p2_ck CHECK (p2 IN (0, 1)), p3 SMALLINT CONSTRAINT p3_ck CHECK (p3 IN (0, 1)), 
p4 SMALLINT CONSTRAINT p4_ck CHECK (p4 IN (0, 1)), p5 SMALLINT CONSTRAINT p5_ck CHECK (p5 IN (0, 1)), 
p6 SMALLINT CONSTRAINT p6_ck CHECK (p6 IN (0, 1)), p7 SMALLINT CONSTRAINT p7_ck CHECK (p7 IN (0, 1)), 
p8 SMALLINT CONSTRAINT p8_ck CHECK (p8 IN (0, 1)), p9 SMALLINT CONSTRAINT p9_ck CHECK (p9 IN (0, 1)), 
p10 SMALLINT CONSTRAINT p10_ck CHECK (p10 IN (0, 1)), p11 SMALLINT CONSTRAINT p11_ck CHECK (p11 IN (0, 1)), 
p12 SMALLINT CONSTRAINT p12_ck CHECK (p12 IN (0, 1)), p13 SMALLINT CONSTRAINT p13_ck CHECK (p13 IN (0, 1)), 
p14 SMALLINT CONSTRAINT p14_ck CHECK (p14 IN (0, 1)), p15 SMALLINT CONSTRAINT p15_ck CHECK (p15 IN (0, 1)), 
p16 SMALLINT CONSTRAINT p16_ck CHECK (p16 IN (0, 1)), p17 SMALLINT CONSTRAINT p17_ck CHECK (p17 IN (0, 1)), 
p18 SMALLINT CONSTRAINT p18_ck CHECK (p18 IN (0, 1)), p19 SMALLINT CONSTRAINT p19_ck CHECK (p19 IN (0, 1)), 
p20 SMALLINT CONSTRAINT p20_ck CHECK (p20 IN (0, 1)), p21 SMALLINT CONSTRAINT p21_ck CHECK (p21 IN (0, 1)),
p22a SMALLINT CONSTRAINT p22a_ck CHECK (p22a IN (0, 1)), p22b SMALLINT CONSTRAINT p22b_ck CHECK (p22b IN (0, 1)), 
p22c SMALLINT CONSTRAINT p22c_ck CHECK (p22c IN (0, 1)), p22d SMALLINT CONSTRAINT p22d_ck CHECK (p22d IN (0, 1)),
p22e SMALLINT CONSTRAINT p22e_ck CHECK (p22e IN (0, 1)), p22f SMALLINT CONSTRAINT p22f_ck CHECK (p22f IN (0, 1)),
p23a SMALLINT CONSTRAINT p23a_ck CHECK (p23a IN (0, 1)), p23b SMALLINT CONSTRAINT p23b_ck CHECK (p23b IN (0, 1)), 
p23c SMALLINT CONSTRAINT p23c_ck CHECK (p23c IN (0, 1)), p23d SMALLINT CONSTRAINT p23d_ck CHECK (p23d IN (0, 1)),
p23e SMALLINT CONSTRAINT p23e_ck CHECK (p23e IN (0, 1)), p24 SMALLINT CONSTRAINT p24_ck CHECK (p24 IN (0, 1)), 
p25 SMALLINT CONSTRAINT p25_ck CHECK (p25 IN (0, 1)), p26 SMALLINT CONSTRAINT p26_ck CHECK (p26 IN (0, 1)),
p27a SMALLINT CONSTRAINT p27a_ck CHECK (p27a IN (0, 1)), p27b SMALLINT CONSTRAINT p27b_ck CHECK (p27b IN (0, 1)), 
p27c SMALLINT CONSTRAINT p27c_ck CHECK (p27c IN (0, 1)),
p28a SMALLINT CONSTRAINT p28a_ck CHECK (p28a IN (0, 1)), p28b SMALLINT CONSTRAINT p28b_ck CHECK (p28b IN (0, 1)), 
p28c SMALLINT CONSTRAINT p28c_ck CHECK (p28c IN (0, 1)), p28d SMALLINT CONSTRAINT p28d_ck CHECK (p28d IN (0, 1)),
pa1 SMALLINT,
pa2 VARCHAR(100), pa3 VARCHAR(150), pa4 VARCHAR(50), pa5 VARCHAR(110),
pa6 SMALLINT,
pa7 VARCHAR(120), pa8 SMALLINT CONSTRAINT pa8_ck CHECK (pa8 IN (1, 2, 3, 4, 5)), pa9 SMALLINT CONSTRAINT pa9_ck CHECK (pa9 IN (1, 2, 3, 4, 5)),
pa10 SMALLINT CONSTRAINT pa10_ck CHECK (pa10 IN (1, 2, 3, 4, 5)), pa11 SMALLINT CONSTRAINT pa11_ck CHECK (pa11 IN (1, 2, 3, 4, 5)),
pa12 SMALLINT CONSTRAINT pa12_ck CHECK (pa12 IN (1, 2, 3, 4, 5)), pa13 SMALLINT CONSTRAINT pa13_ck CHECK (pa13 IN (1, 2, 3, 4, 5)),
ptot SMALLINT, delta SMALLINT, prep VARCHAR(15), pnotes VARCHAR(300));
ALTER TABLE gar.users ADD CONSTRAINT uname_unique UNIQUE (uname)


ALTER TABLE gar.users DROP CONSTRAINT pa6_ck;
DROP TABLE gar.users;


insert into gar.users values (1, 'Osvaldo');
select * from gar.users;


CREATE TABLE gar.cards (cid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, 
cistrick SMALLINT CONSTRAINT cist_ck CHECK (cistrick IN (0, 1)), ctype VARCHAR(5), content VARCHAR(30), 
cnum SMALLINT NOT NULL, cden SMALLINT, cdecimal SMALLINT CONSTRAINT cdecimal_ck CHECK (cdecimal IN (0, 1)));

insert into gar.cards (cistrick, ctype, content, cnum, cden, cdecimal) values (0, 'team', 'The Twins', 2, null, 0);


CREATE TABLE gar.questions(qid INT NOT NULL CONSTRAINT qid_pk PRIMARY KEY, question VARCHAR(30) NOT NULL, qans SMALLINT NOT NULL, 
qnum SMALLINT NOT NULL, qden SMALLINT NOT NULL, qval SMALLINT NOT NULL, qdecimal SMALLINT CONSTRAINT qdecimal_ck CHECK (qdecimal IN (0, 1)));

CREATE TABLE gar.pairs (pid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, uid1 INT, uid2 INT);

CREATE TABLE gar.games (gid INT PRIMARY KEY, pid1 INT NOT NULL, pid2 INT NOT NULL, pstart TIMESTAMP NOT NULL, pend TIMESTAMP NOT NULL);
DROP TABLE gar.games;

-- While at first I thought it would be good to have a session column, it's much easier to just search by timestamp
-- that would be a much more regular way of doing things.
ALTER TABLE gar.games ADD COLUMN session SMALLINT;
ALTER TABLE gar.games DROP COLUMN session;

CREATE TABLE gar.userlogs (ulogid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, uid INT NOT NULL, qaid INT NOT NULL, uorder SMALLINT NOT NULL, qid INT NOT NULL, ulogtime TIMESTAMP, ulogattempt VARCHAR(10), ulogtype VARCHAR(15), ulogppl SMALLINT, uloglines SMALLINT, ulogmarks SMALLINT, ulogshown SMALLINT CONSTRAINT ulogshown_ck CHECK (ulogshown IN (0, 1)));
DROP TABLE gar.userlogs;
DELETE FROM gar.games;
DELETE FROM gar.pairlogs;
DELETE FROM GAR.STATELOGS;
DELETE FROM gar.userlogs;

CREATE TABLE gar.pairlogs (plogid INT PRIMARY KEY, gid INT NOT NULL, pid INT NOT NULL, porder INT NOT NULL, qaid INT, plogtime TIMESTAMP NOT NULL, plogtype VARCHAR(20), plogcontent VARCHAR(350));

DROP TABLE gar.pairlogs;
DROP TABLE gar.statelogs;

CREATE TABLE gar.statelogs (slogid INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, gid INT NOT NULL, plogid INT NOT NULL, p1teamid0 INT, p1teamid1 INT, p2teamid0 INT, p2teamid1 INT, p1numleft0 INT, p1numleft1 INT, p2numleft0 INT, p2numleft1 INT, p1trickid0 INT, p1trickid1 INT, p1trickid2 INT, p1trickid3 INT, p2trickid0 INT, p2trickid1 INT, p2trickid2 INT, p2trickid3 INT);

SELECT pid from gar.pairs WHERE uid1 = (SELECT uid from gar.users where uname = 'Christian') AND uid2 = (SELECT uid from gar.users where uname = null);
SELECT pid from gar.pairs WHERE uid1 = (SELECT uid from gar.users where uname = 'Christian') AND uid2 is null;
SELECT pid from gar.pairs WHERE uid2 is null;


select uid, u3cset, u4cset, mqtot, ptot from gar.users where treatment = 'B';
select qid, uid from gar.userlogs where uid = 223 and ulogtype = 'QShown';
select qid from gar.userlogs where uid = 214 AND ulogtype = 'QStarted' AND qid IN (select qid from gar.questions where qans = -1);
select * from gar.users where uid = 214;
select * from gar.userlogs where uid = 214 AND ulogtype = 'QStarted';
select qid from gar.questions where qans = -1;
select qid, attempt from gar.userlogs where uid = 214 AND ulogtype = 'QTried';
select gid from gar.games where pid1 IN (select pid from gar.pairs where uid1 = 214 OR uid2 = 214) OR pid2 IN (select pid from gar.pairs where uid1 = 214 OR uid2 = 214);
select qid from gar.userlogs where ulogtype = 'QDone';

select * from gar.userlogs where uid = 214 and qaid in (select qaid from gar.pairlogs where plogtype = 'QuestionAns' and gid in (select gid from gar.games where gid >= 1 and gid <= 4 and (pid1 IN (select pid from gar.pairs where uid1 = 214 OR uid2 = 214) OR pid2 IN (select pid from gar.pairs where uid1 = 214 OR uid2 = 214))));
select * from gar.userlogs where qid = -304012;
select * from gar.userlogs where qid = -10208 and ulogtype = 'QTried';
select * from gar.userlogs where qid = 20308 and qaid in (select qaid from gar.pailogs where)
select * from gar.userlogs where qid = 20308 and ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'A')

--get all userlogs created from 2/3 of 8 for the abstract group on the first day.
select * from gar.userlogs where qid = 20308 and ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and ulogtime < '2013-01-15 17:34:00'
select * from gar.userlogs where qid = 20308 and ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and ulogtime > '2013-02-12 07:34:00'
-- abstract group went from 6 times in the first day to 9 times in the last day
-- story went from 0 times to 15 on the last day

--gives us all the questions that were generated incorrectly
select * from gar.questions where qans = -1

--get all of the incorrect questions generated during the first session by group _
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and qid in (select qid from gar.questions where qans = -1) and ulogtime < '2013-01-15 17:00:00'
--4th game
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'A') and qid in (select qid from gar.questions where qans = -1) and ulogtime > '2013-01-29 07:00:00' and ulogtime < '2013-01-29 17:00:00'
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'B') and qid in (select qid from gar.questions where qans = -1) and ulogtime > '2013-01-29 07:00:00' and ulogtime < '2013-01-29 17:00:00'
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and qid in (select qid from gar.questions where qans = -1) and ulogtime > '2013-01-29 07:00:00' and ulogtime < '2013-01-29 17:00:00'
--5th game
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and qid in (select qid from gar.questions where qans = -1) and ulogtime > '2013-02-12 07:00:00'
--users for a specific game
select distinct uid from gar.userlogs where uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and ulogtime > '2013-01-29 07:00:00' and ulogtime < '2013-01-29 17:00:00'
--In 5th session, numfolks (26, 24, 26), questions wrong (62, 70, 66) , total (251, 273, 306)

--get total questions (251, 273, 306) for session5
select * from gar.userlogs where ulogtype = 'QStarted' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and ulogtime > '2013-01-29 07:00:00' and ulogtime < '2013-01-29 17:00:00'
--for session 1 (24, 24, 27) we get numwrong (10, 27, 16) and total (69, 78, 99)


--get total times you asked for help
select * from gar.userlogs where ulogtype = 'QShown' and uid in (select uid from gar.users where instudy = 1 and treatment = 'C') and ulogtime > '2013-02-12 07:00:00' and ulogtime < '2013-02-12 17:00:00'
select qaid from gar.userlogs where ulogtype = 'QDone'
select count(qaid) from gar.userlogs where qaid in (select qaid from gar.userlogs where ulogtype = 'QDone') and ulogtype = 'QDone'
select min(qaid) from gar.userlogs where ulogtype = 'QDone'

--all games from session 1  (for 2, 3, 4, or 5) just change to other dates, 01-22, 01-29, 02-05, 02-12
select * from gar.games where pstart < '2013-01-15 17:34:00' and pstart > '2013-01-15 07:32:00'

--trying to figure out what the call is to get the shadow questions like we've done before
select * from gar.userlogs where qaid in (select distinct qaid from gar.pairlogs where qaid is not null and plogcontent like '%Shadow%') and uid = 401

select * from gar.pairlogs where plogcontent like '%Shadow%' and plogtime < '2013-01-15 17:30:00'


select * from gar.userlogs where qaid in (select distinct qaid from gar.pairlogs where qaid is not null and plogcontent like '%Shadow%') and uid = 214;
select uid, p12, p13, p14, p15, p16, p17, mqtot, ptot from gar.users where treatment = 'B';
select uid, (mq12 + mq13 + mq14 + mq15 + mq16 + mq17), (p12 + p13 + p14 + p15 + p16 + p17), mqtot, ptot from gar.users where treatment = 'B';
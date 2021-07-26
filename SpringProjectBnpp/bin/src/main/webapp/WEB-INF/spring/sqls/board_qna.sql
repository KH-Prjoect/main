DROP TABLE BOARDQNA;

CREATE SEQUENCE BQNUMSEQ;


CREATE TABLE BOARDQNA (
	BQ_NUM	NUMBER	NOT NULL,
	BQ_TITLE	VARCHAR2(1000)	NOT NULL,
	BQ_CONTENT	VARCHAR2(4000)	NOT NULL,
	BQ_REGDATE	DATE	NOT NULL,
	BQ_COMPLETE	VARCHAR(2)	NOT NULL,
	BQ_ANSWER	VARCHAR(4000)	 NULL,
	MEMBER_ID	VARCHAR2(500)	NOT NULL
);

ALTER TABLE Boardqna ADD CONSTRAINT PK_BOARDQNA PRIMARY KEY (
	BQ_NUM
);

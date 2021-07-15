
CREATE SEQUENCE BRNUMSEQ;
DROP SEQUENCE BRNUMSEQ;


CREATE TABLE BOARDRECIPE (
	BR_NUM	NUMBER		PRIMARY KEY,
	BR_TITLE	VARCHAR2(1000)		NOT NULL,
	BR_CONTENT	VARCHAR2(4000)		NOT NULL,
	BR_REGDATE	DATE		NOT NULL,
	BR_READCOUNT	NUMBER	DEFAULT 0	NOT NULL,
	BR_RECCOUNT	NUMBER	DEFAULT 0	NOT NULL,
	BR_DELFLAG	VARCHAR2(2)		NOT NULL,
	BR_CATEGORY	VARCHAR2(500)		NULL,
	MEMBER_ID	VARCHAR2(500)		NOT NULL
);


DROP TABLE BOARDRECIPE;


SELECT * FROM BOARDRECIPE;

INSERT INTO BOARDRECIPE VALUES(BRNUMSEQ.NEXTVAL,'제목테스트','내용테스트',SYSDATE,0,0,'N','Board','테스트');

	SELECT BR_NUM, BR_TITLE, BR_CONTENT, BR_REGDATE, BR_READCOUNT,BR_RECCOUNT,BR_DELFLAG,BR_CATEGORY,MEMBER_ID
	FROM BOARDRECIPE
	ORDER BY BR_NUM DESC;
	
	

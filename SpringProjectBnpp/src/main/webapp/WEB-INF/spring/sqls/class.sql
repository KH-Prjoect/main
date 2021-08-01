DROP TABLE CLASS;
DROP SEQUENCE CLASS_NUM;

CREATE SEQUENCE CLASS_NUM;
CREATE TABLE CLASS(
	CLASS_NUM NUMBER NOT NULL PRIMARY KEY,
	TEACHER_ID VARCHAR2(100) NOT NULL,
	CLASS_TITLE VARCHAR2(200) NOT NULL,
	CLASS_INTRO VARCHAR2(3000) NOT NULL,
	CLASS_PRICE NUMBER NOT NULL,
	CLASS_HOW VARCHAR2(50) NOT NULL,
	CLASS_WHERE VARCHAR2(1000) DEFAULT '온라인 강의입니다.',
	CLASS_INGRED VARCHAR2(2000) NOT NULL,
	CLASS_CONTENT VARCHAR2(3000) NOT NULL,
	CLASS_REGDATE DATE NOT NULL,
	CLASS_MEDIA_NAME VARCHAR2(400),
	CLASS_MEDIA_PATH VARCHAR2(400),
	CONSTRAINT FK_MEMBERID FOREIGN KEY(TEACHER_ID) REFERENCES MEMBER(MEMBER_ID),
	CONSTRAINT CLASS_HOW_CHK CHECK (CLASS_HOW IN ('N', 'F'))
);

SELECT * FROM CLASS;

INSERT INTO CLASS
VALUES(CLASS_NUM.NEXTVAL, 'teacher01', '이너조인테스트', '이너조인테스트', '5000', 'N', DEFAULT, '양파1개', '양파볶음', SYSDATE, '', '');

SELECT M.MEMBER_NAME, C.CLASS_NUM, C.TEACHER_ID, C.CLASS_TITLE, C.CLASS_INTRO, C.CLASS_PRICE, C.CLASS_HOW, C.CLASS_WHERE, C.CLASS_INGRED, C.CLASS_CONTENT, C.CLASS_REGDATE, C.CLASS_MEDIA_NAME, C.CLASS_MEDIA_PATH
FROM MEMBER M INNER JOIN CLASS C ON M.MEMBER_ID = C.TEACHER_ID;

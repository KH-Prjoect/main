DROP SEQUENCE FOODSEQ;
DROP TABLE FOOD;

CREATE SEQUENCE FOODSEQ;

CREATE TABLE FOOD(
	FOOD_NUM NUMBER PRIMARY KEY,
	FOOD_NAME VARCHAR2(1000) NOT NULL,
	FOOD_LIFE VARCHAR2(1000),
	FOOD_ALARM_YN VARCHAR2(2) CHECK(FOOD_ALARM_YN IN ('Y', 'N')) NOT NULL,
	MEMBER_ID VARCHAR2(500) NOT NULL
);

SELECT FOOD_NUM, FOOD_NAME, FOOD_LIFE, FOOD_ALARM_YN, MEMBER_ID 
FROM FOOD
ORDER BY FOOD_NUM DESC;

INSERT INTO FOOD
VALUES(FOODSEQ.NEXTVAL, '', NULL, 'N', '12');

UPDATE FOOD
SET FOOD_LIFE = '2022-08-01'
WHERE FOOD_NUM = 1;
CREATE TABLE Groups 
(
    ID    bigint NOT NULL,
    Name 	varchar(255) NOT NULL,
    
    UNIQUE(Name),
    PRIMARY KEY(ID)
);

CREATE TABLE Articles 
(
  Body      VARCHAR NOT NULL ,
  Date      BIGINT NOT NULL ,
  Sender    VARCHAR( 255 ) NOT NULL ,
  GroupID   BIGINT NOT NULL ,
  MessageID VARCHAR( 255 ) NOT NULL ,
  NumberInGroup INT NOT NULL ,
  Subject   VARCHAR( 255 ) NOT NULL,

  PRIMARY KEY(MessageID)
);

CREATE TABLE ArticleReferences
(
);

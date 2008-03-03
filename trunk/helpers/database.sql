CREATE TABLE Groups 
(
    ID 		integer NOT NULL,
    Name 	varchar(255) NOT NULL,
    
    PRIMARY KEY(ID)
);

CREATE TABLE Articles
(
    Body			varchar NOT NULL,
    Date 			timestamp NOT NULL,
    GroupID			integer,
    MessageID 		varchar(255) NOT NULL,
    NumberInGroup   integer,
    
    PRIMARY KEY(MessageID)
);

CREATE TABLE ArticleReferences
(
);

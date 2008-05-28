CREATE TABLE Groups 
(
    ID    bigint NOT NULL,
    Name 	varchar(255) NOT NULL,
    
    UNIQUE(Name),
    PRIMARY KEY(ID)
);

CREATE TABLE Articles
(
    Body			varchar NOT NULL,
    Date 			bigint NOT NULL,
    GroupID         integer,
    MessageID 		varchar(255) NOT NULL,
    NumberInGroup   integer,
    
    PRIMARY KEY(MessageID)
);

CREATE TABLE ArticleReferences
(
);

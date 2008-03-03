CREATE TABLE Groups 
(
    ID 		integer NOT NULL,
    Name 	varchar(255) NOT NULL,
    
    PRIMARY KEY(ID)
);

CREATE TABLE News 
(
    Body			text NOT NULL,
    Date 			timestamp NOT NULL,
    GroupID			integer,
    MessageID 		varchar(255) NOT NULL,
    
    PRIMARY KEY(MessageID)
);

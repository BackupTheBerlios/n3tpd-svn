CREATE TABLE Groups 
(
    ID    bigint NOT NULL,
    Name 	varchar(255) NOT NULL,
    
    UNIQUE(Name),
    PRIMARY KEY(ID)
)
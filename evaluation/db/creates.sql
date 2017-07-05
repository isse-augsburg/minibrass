CREATE TABLE IF NOT EXISTS Config 
( ID INTEGER PRIMARY KEY,
  Timeout        INTEGER     NOT NULL,
  SearchType     INTEGER     NOT NULL,
  SPD            BOOLEAN     NOT NULL,   
  MIF            BOOLEAN     NOT NULL, 
  PropRed        BOOLEAN     NOT NULL,
  LnsProb        REAL ,
  LnsIter        INTEGER
);

CREATE TABLE IF NOT EXISTS Solver 
( ID INTEGER PRIMARY KEY,
  SolverName     VARCHAR(100)     NOT NULL
);


CREATE TABLE IF NOT EXISTS JobResult
   ( ID INTEGER PRIMARY KEY AUTOINCREMENT,
     Problem        VARCHAR(100)     NOT NULL,  
    Instance       VARCHAR(100)  NOT NULL,  
    SolverId       INTEGER NOT NULL,
    ConfigId       INTEGER NOT NULL,
    Solved         BOOLEAN  NOT NULL, 
    Optimally      BOOLEAN  NOT NULL, 
    Objective      INTEGER  NOT NULL, 
    NoSolutions    INTEGER  NOT NULL, 
    ElapsedSecs    REAL  NOT NULL,
    FOREIGN KEY(ConfigId) REFERENCES Config(ID),
    FOREIGN KEY(SolverId) REFERENCES Solver(ID)
) ;

CREATE TABLE IF NOT EXISTS ProblemInformation
   (
    Problem        VARCHAR(100)     NOT NULL,  
    Instance       VARCHAR(100)  NOT NULL,  
    MaxObjectiveSpd   INTEGER       NOT NULL,
    MaxObjectiveTpd   INTEGER       NOT NULL,
    PRIMARY KEY(Problem,Instance)
) ;

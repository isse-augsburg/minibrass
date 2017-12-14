p = 5;

% some minisearch-compatible keywords instead of enums
int: AUGUST = 1; int: HANS = 2; 
int: HERMANN = 3; int: MARIA = 4; 
int: FRANZISKA = 5;

% august, hans, hermann, maria, franziska
t = 5;
% some minisearch-compatible keywords instead of enums
int: HOMEMADE = 1; int: FOOD = 2; int: COUPON = 3; int: ACTIVE = 4; int: KITSCH = 5;

tagged = [{HOMEMADE, ACTIVE}, {FOOD, COUPON,HOMEMADE}, {ACTIVE}, {KITSCH, ACTIVE}, {FOOD,ACTIVE}];


% Franziska and Hans are in a relationship, so they have to give each other presents anyway
excludes  = [{},{FRANZISKA},{},{},{HANS}];

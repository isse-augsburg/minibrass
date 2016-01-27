var 0..5: x;
var 0..5: y;
var 5..10: w;

% first objective
var int: o1;
constraint o1 = 3*x + 2*y;   % minimally 0, maximally 25

% second objective
var int: o2;
constraint o2 = w - 2*x - y; % minimally 5 - 10 - 5 = -10 
                           % maximally 10 - 0 - 0 = 10

% should lead to
%   0..25: o1
% -10..10: o2

% objective composed of two objectives
var int: z;
constraint z = o1 + o2; % minimally -10, maximally 35

solve minimize z;

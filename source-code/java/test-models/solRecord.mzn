include "solRecord_o.mzn";
% include "soft_constraints/minibrass.mzn"; 


var 1..3: value; 

solve 
:: int_search([value], input_order, indomain_min,complete)
%search miniBrass();
satisfy; % for satisfaction problems 

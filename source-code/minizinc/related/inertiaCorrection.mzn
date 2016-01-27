%% A MiniZinc model for inertia correction

% given in time steps 
int: minWindowLength;
int: maxWindowLength;
set of int: WINDOW = minWindowLength .. maxWindowLength;

% needed for calculation to seconds
int: dt; % delta time in ms 

var WINDOW: window;
var float: windowInSeconds = (2*window*dt) / 1000;
 
int: minJ;
int: maxJ;
int: precision = 10;

set of float: jDOM = minJ / precision .. maxJ / precision;
var minJ..maxJ: j;

var jDOM: J = j/precision;

float: domSize = 1000.0;

int: T; % number of time steps 
set of int: TIME = 1..T;

% makes a safe lookup with truncation possible
function var float: lookup(array[int] of float: values, var int: index) :: promise_total 
=
let {
  int: maxIndex = max(index_set(values));
} in
(
  if index < 1 then values[1] elseif index > maxIndex then values[maxIndex] else values[index] endif
);

% now for the window and actual differences
array[TIME] of float: temperature;
array[TIME] of var float: predTemperature = [lookup(temperature, t-window) | t in TIME];
array[TIME] of var float: succTemperature = [lookup(temperature, t+window) | t in TIME];
array[TIME] of var float: diffTemp = [succTemperature[t] - predTemperature[t] | t in TIME ];

% now the actual correction
array[TIME] of var -domSize..domSize: corrTemp = [temperature[t] + (diffTemp[t] / windowInSeconds)  * J | t in TIME ];
%array[TIME] of var 0.0..12.0: corrTemp; % = [temperature[t] + (diffTemp[t] / windowInSeconds)  * J | t in TIME ];

array[TIME] of int: altitude;

set of min(altitude)..max(altitude): altitudes = { altitude[t] | t in TIME};

array[altitudes] of var -domSize..domSize: maxTemp; 
array[altitudes] of var -domSize..domSize: minTemp; 
array[altitudes] of var -domSize..domSize: spanTemp; 

constraint forall(a in altitudes) (maxTemp[a] = ( max(t in TIME where altitude[t] = a) (corrTemp[t]) ));
constraint forall(a in altitudes) (minTemp[a] = ( min(t in TIME where altitude[t] = a) (corrTemp[t]) ));
constraint forall(a in altitudes) (spanTemp[a] = maxTemp[a] - minTemp[a]);

var -domSize..domSize: OBJ = sum(a in altitudes) (spanTemp[a] * spanTemp[a]);
% sum(a in altitudes) (spanTemp[a]);
% sum(a in altitudes) (spanTemp[a]); % for all errors

%constraint forall(a in altitudes) (spanCorrTemp[a] >= 0);
%constraint window = 2;
%constraint j = 20;

solve 
:: seq_search([
  int_search([window], input_order, indomain_min, complete),
  int_search([j], input_order, indomain_min, complete)])
minimize OBJ;

output ["altitudes \(altitudes), predTemperature: \(predTemperature); windowInSeconds: \(windowInSeconds)\nmaxTemp=\(maxTemp), spanTemp \(spanTemp)\n J : \(J), j \(j), window \(window), OBJ: \(OBJ)"
      ];
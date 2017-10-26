% This will be used to evaluate whether a copter is fit for purpose for
% some task and return the least amount of reconfigurations 

% -------------------------------------
% MiniBrass
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/pvs_set_based.mzn";
include "soft_constraints/cr_types.mzn";

nCrEdges = max(index_set_1of2(crEdges));

% use upper line if we want to indicate that changing hardware 1 is *more severe* than changing hardware 2 
% so it is *more important* to leave hardware 1 as-is 
%include "soft_constraints/pvs_spd.mzn"; 
penalties = [1 | i in SOFTCONSTRAINTS];

% -------------------------------------
% Meta data
int: maxHwIdent; int: maxSwIdent;
% 0 for unused slots
set of int: HARDWARE = 1..maxHwIdent; set of int: HARDWARE0 = HARDWARE union {0};
set of int: SOFTWARE = 1..maxSwIdent; set of int: SOFTWARE0 = SOFTWARE union {0};

int: maxCapabilities; % things like ... temperature, pressure ...
set of int: CAPABILITIES = 1..maxCapabilities;
array[HARDWARE] of set of CAPABILITIES: capabilities; 

% additional (physical) parameters (such as payload ...) 
array[HARDWARE] of int: payload; 
constraint forall(i in HARDWARE)( assert (payload[i] >= 0,"Payload[\(i)] must be positive, is \(payload[i])") );

int: maxSoftwareSlots;
int: maxHardwareSlots; 

set of int: SW_SLOTS = 1..maxSoftwareSlots;
set of int: HW_SLOTS = 1..maxHardwareSlots;

% -------------------------------------
% Task specification
set of CAPABILITIES: requiredCapabilities; % {T,P}
int: payloadThreshold; 
constraint assert(payloadThreshold >= 0, "Payload threshold must be non-negative");

% -------------------------------------
% Configurations

% current configuration - therefore par(ameter)
array[SW_SLOTS] of par SOFTWARE0: softwareAtSlot;
array[HW_SLOTS] of par HARDWARE0: hardwareAtSlot;

% decisions to be made - new configuration, therefore var(iable)
array[SW_SLOTS] of var SOFTWARE0: targetSoftwareAtSlot;
array[HW_SLOTS] of var HARDWARE0: targetHardwareAtSlot;

include "alldifferent_except_0.mzn";

% we require HARDWARE to denote *identity*, so the slots
% can safely be all different (except for 0 values denoting undefinedness)
constraint alldifferent_except_0(targetSoftwareAtSlot);
constraint alldifferent_except_0(targetHardwareAtSlot);

% -------------------------------------
% Hard constraints (defined by task spec)

% capabilities met 
constraint forall(r in requiredCapabilities) (exists(i in HW_SLOTS)(r in capabilities[targetHardwareAtSlot[i]] ) );

% payload below threshold
constraint sum(i in HW_SLOTS where targetHardwareAtSlot[i] in HARDWARE ) (payload[targetHardwareAtSlot[i]] ) <= payloadThreshold;

% -------------------------------------
% Soft constraints (stay as close to the original as possible)

nScs = maxSoftwareSlots + maxHardwareSlots;

constraint forall(i in SW_SLOTS) (satisfied[i] <-> (softwareAtSlot[i] = targetSoftwareAtSlot[i])) ;

constraint forall(i in HW_SLOTS) (satisfied[i+maxSoftwareSlots] <-> (hardwareAtSlot[i] = targetHardwareAtSlot[i])) ;

ann: searchAnn = int_search(mifSatisfied, input_order, indomain_max, complete);

solve 
:: searchAnn
minimize penSum;

output ["Solution with penalty \(penSum) from violated soft constraints \(violatedScs)\n"] 
       ++ ["  Software (current): \n"] 
       ++ [ "    s[\(i)]: \(softwareAtSlot[i])\n" | i in SW_SLOTS]  
       ++ ["  Hardware (current) \n"]
       ++ [ "    h[\(i)]: \(hardwareAtSlot[i])\n" | i in HW_SLOTS]   
       ++ ["  -----------  \n"]
       ++ ["  Software (target): \n"] 
       ++ [ "    s[\(i)]: \(targetSoftwareAtSlot[i])\n" | i in SW_SLOTS]  
       ++ ["  Hardware (target) \n"]
       ++ [ "    h[\(i)]: \(targetHardwareAtSlot[i])\n" | i in HW_SLOTS];  
var 0..6000: B;
var 0..4000: T;
solve maximize 25*B + 30*T;
constraint 140*B + 200*T <= 140*200*40;
output ["B=",show(B), "T=",show(T)];
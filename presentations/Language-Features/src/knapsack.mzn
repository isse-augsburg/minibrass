int: n; set of int: ITEMS = 1..n;
int: K; % capacity
array[ITEMS] of int: v;
array[ITEMS] of int: w;

array[ITEMS] of var bool: x;

constraint sum(i in ITEMS) (x[i]*w[i]) <= K;
solve maximize sum(i in ITEMS) (x[i]*v[i]);

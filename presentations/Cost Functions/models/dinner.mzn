int: steak = 1; int: fish = 2; int: pizza = 3;
int: red = 1; int: white = 2;
set of int: MEAL = {steak, fish, pizza};
set of int: WINE = {red, white};
var MEAL: meal; var WINE: wine; 

var 0..4: mealA; var 0..4: mealB; var 0..4: mealC;
var 0..4: wineA; var 0..4: wineB; var 0..5: wineC;

include "soft_constraints/cost_functions.mzn";

% Albert
constraint cost_function_binary(meal, wine, 
 [
   /* steak, red */       2,
   /* steak, white */     10,
   /* fish, red */        20,
   /* fish, white */      3,
   /* pizza, red */       5,
   /* pizza, white */     5
 ]
 , mealA);

constraint cost_function_unary(wine, [2, 1], wineA);

% Berta
constraint cost_function_binary(meal, wine, 
 [
   /* steak, red */       4,
   /* steak, white */     20,
   /* fish, red */        30,
   /* fish, white */      1,
   /* pizza, red */       5,
   /* pizza, white */     5
 ]
 , mealB);

constraint cost_function_unary(wine, [3, 1], wineB);

% Carl
constraint cost_function_binary(meal, wine, 
 [
   /* steak, red */       4,
   /* steak, white */     12,
   /* fish, red */        10,
   /* fish, white */      4,
   /* pizza, red */       1,
   /* pizza, white */     1
 ]
 , mealC);

constraint cost_function_unary(wine, [1, 2], wineC);

solve minimize mealA + mealB + mealC + wineA + wineB + wineC;
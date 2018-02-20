set M := {read "old.txt" as "<1n>" comment "#"};
set N := {read "new.txt" as "<1n>" comment "#"};

param old[M] := read "old.txt" as "<1n> 2n" comment "#";
param new[N] := read "new.txt" as "<1n> 2n" comment "#";
param string_sum := max((sum <m> in M: m * old[m]), (sum <n> in N: n * new[n]));

var delete[M] binary;
var add[N] binary;
var after_delete[M] integer <= 200;
var before_add[N] integer <=200;
# var after_delete_nozero[M] integer <=200;
# var before_add_nozero[N] integer <=200;
var after_delete_iszero[M] binary;
var before_add_iszero[N] binary;
var difference1[M] integer >=-200 <=200;
var difference2[N] integer >=-200 <=200;

minimize totalcost: sum <m> in M: delete[m] 
                    + sum <n> in N: add[n] 
#                    + vabs(delete[3]-delete[6]) * 20 
#                    + vabs(delete[11] - delete[12]) * 20
#                    - vabs(sum <m> in M: after_delete[m] * m - sum <n> in N: before_add[n] * n) / 2
                    + vabs(sum <m> in M: vabs(difference1[m])*m - sum <n> in N: vabs(difference2[n])*n)
                    ;

subto after_delete: forall <m> in M: after_delete[m] == delete[m] * 0 + (1 - delete[m]) * old[m];
subto before_add: forall <n> in N: before_add[n] == add[n] * 0 + (1 - add[n]) * new[n];
# subto after_delete_nozero: forall <m> in {1..card(M)-1}: vif after_delete[m] == 0 then after_delete_nozero[m] == after_delete[m+1] else after_delete_nozero[m] == after_delete[m] end;
# subto before_add_nozero: forall <n> in {1..card(N)-1}: vif before_add[n] == 0 then before_add_nozero[n] == before_add[n+1] else before_add_nozero[n] == before_add[n] end;
subto almost_equal: sum <m> in M: after_delete[m] == sum <n> in N: before_add[n];
subto after_delete_iszero: forall <m> in M: vif after_delete[m] == 0 then after_delete_iszero[m] == 1 else after_delete_iszero[m] == 0 end;
subto before_add_iszero: forall <n> in N: vif before_add[n] == 0 then before_add_iszero[n] == 1 else before_add_iszero[n] == 0 end;
subto almost_equal2: sum <m> in M: after_delete_iszero[m] == sum <n> in N: before_add_iszero[n];
subto difference_old: forall <m> in {2..card(M)}: difference1[m] == after_delete[m] - after_delete[m-1];
subto difference_old1: difference1[1] == 0;
subto difference_new: forall <n> in {2..card(N)}: difference2[n] == before_add[n] - before_add[n-1];
subto difference_new1: difference2[1] == 0;
# subto almost_equal3: sum <m> in M: vabs(difference1[m]) == sum <n> in N: vabs(difference2[n]);





var test1;
var test2;
subto test1: test1 == vabs(sum <m> in M: vabs(difference1[m]) - sum <n> in N: vabs(difference2[n]));
subto test2: test2 == sum <n> in N: before_add[n];
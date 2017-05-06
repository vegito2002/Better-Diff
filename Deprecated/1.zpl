param l := 20;

set I := {1..l};
set I2 := {1..l*2};

param s1[I2] := <1>10,<2>11,<3>12,<4>10,<5>13,<6>12 default 0;
param s2[I2] := <1>10,<2>11,<3>12,<4>10,<5>14,<6>12,<7>10,<8>13,<9>12 default 0;

# param s1[I2] := <1>1,<2>2,<3>3,<4>4,<5>5,<6>6,<7>7,<8>0,<9>0,<10>0,<11>0,<12>0,<13>0,<14>0;
# param s2[I2] := <1>1,<2>2,<3>3,<4>10,<5>11,<6>12,<7>7,<8>0,<9>0,<10>0,<11>0,<12>0,<13>0,<14>0;

var in1[I] integer >= 1 <= l*2;
var in2[I] integer >= 1 <= l*2;
var c1[I] integer <= 255;
var c2[I] integer <= 255;
var has1[I2] binary;
var has2[I2] binary;

subto in1: forall <i> in {2..l}: in1[i] >= in1[i-1] + 1;
subto in2: forall <i> in {2..l}: in2[i] >= in2[i-1] + 1;

subto c1: forall <i> in I: forall <j> in I2: vif in1[i] == j then c1[i] == s1[j] end;
subto c2: forall <i> in I: forall <j> in I2: vif in2[i] == j then c2[i] == s2[j] end;
subto has1: forall <i> in I: forall <j> in I2: vif in1[i] == j then has1[j] == 1 end;
subto has2: forall <i> in I: forall <j> in I2: vif in2[i] == j then has2[j] == 1 end;
subto equal: forall <i> in I: c1[i] == c2[i];

minimize distance: sum <i> in I: (in1[i] + in2[i]) / l + sum <i> in I2: (has1[i] + has2[i]) * l + vabs(has1[1] - has1[3]) + vabs(has1[4] - has1[6]) + vabs(has2[1] - has2[3]) + vabs(has2[4] - has2[6]) + vabs(has2[7] - has2[9]);

Index arrays `in1` and `in2` point to characters within input strings. We constraint the indices to be ascending, and the corresponding characters (stored in `c1` and `c2`) to be the same. `has1` and `has2` are binary arrays representing the chosen characters. We maximize the number of chosen characters, and use `vabs()` to prioritize pairs being chosen or dropped together.

Strings `s1` and `s2` are represented in arrays of bytes. They are indexed twice as big so that we are always able to find a satisfying solution where we delete all characters in the original string.

This turned out to be very inefficient as two strings of length 9 are translated into 3332 variables and 4718 constraints.
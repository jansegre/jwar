% vim: filetype=prolog et sw=4 ts=4 sts=4
%

:- begin_tests(rules).
:- use_module('rules').

% sample map
%
% -- a - b       g --
%     \ / \     /
%      c   e - f
%      |       |
%      d       h - i
%
% conts:
%   aa: a, b, c, d
%   bb: e, f, g
%   cc: h, i

% basic predicates

test(territory) :- territory(a).
test(territory) :- territory(b).
test(territory) :- findall(T, territory(T), [a, b, c, d, e, f, g, h, i]).
test(territory) :- not(territory(j)).

test(neighbours) :- findall(N, neighbours(a, N), [b, c, g]).
test(neighbours) :- findall(N, neighbours(N, a), [b, c, g]).
test(neighbours) :- findall(N, neighbours(e, N), [b, f]).
test(neighbours) :- not(neighbours(d, e)).
test(neighbours) :- not(neighbours(e, d)).
test(neighbours) :- not(neighbours(h, d)).

test(continent) :- continent(aa).
test(continent) :- continent(bb).
test(continent) :- continent(cc).

test(territory_continent) :- findall(T, territory_continent(T, aa), [a, b, c, d]).
test(territory_continent) :- findall(T, territory_continent(T, bb), [e, f, g]).
test(territory_continent) :- findall(T, territory_continent(T, cc), [h, i]).
test(territory_continent) :- findall(C, territory_continent(a, C), [aa]).

test(players) :- sample1(S), findall(P, player(P, S), [p1, p2, p3, p4]).

% state predicates

sample1([
   [a,  b,  c,  d,  e,  f,  g,  h,  i], % territories
   [1,  3,  6,  2,  2,  4,  1,  3,  1], % armies
   [p2, p2, p2, p2, p1, p2, p3, p3, p4], % owners
   [p1, p2, p3, p4], % players
   [[world], [conts, 1, am], [kill, p5], [min, 3, 2]], % objective
   p1, % current player
   1, % round (0 is initial distribution)
   moving, % stage (placing, attacking, occupying, moving)
   0 % armies to place
]).

sample2([
   [a,  b,  c,  d,  e,  f,  g,  h,  i], % territories
   [1,  3,  6,  2,  2,  4,  1,  3,  1], % armies
   [p2, p2, p2, p2, p1, p2, p3, p3, p4], % owners
   [p1, p2, p3, p4], % players
   [[world], [conts, 1, am], [kill, p5], [min, 3, 2]], % objective
   p4, % current player
   0, % round (0 is initial distribution)
   placing, % stage (placing, attacking, occupying, moving)
   0 % armies to place
]).

test(get) :- sample1(S), get(territories, [a, b, c, d, e, f, g, h, i], S).
test(get) :- sample1(S), get(armies, [1, 3, 6, 2, 2, 4, 1, 3, 1], S).
test(get) :- sample1(S), get(owners, [p2, p2, p2, p2, p1, p2, p3, p3, p4], S).
test(get) :- sample1(S), get(players, [p1, p2, p3, p4], S).
test(get) :- sample1(S), get(objectives, [[world], [conts, 1, am], [kill, p5], [min, 3, 2]], S).
test(get) :- sample1(S), get(player, p1, S).
test(get) :- sample1(S), get(round, 1, S).
test(get) :- sample1(S), get(stage, moving, S).
test(get) :- sample1(S), get(to_place, 0, S).

%TODO: test getall, set, setall, although they're correct, the test

test(owner) :- sample1(S), findall(T, owner(p1, T, S), [e]).
test(owner) :- sample1(S), findall(T, owner(p2, T, S), [a, b, c, d, f]).
test(owner) :- sample1(S), findall(T, owner(p3, T, S), [g, h]).
test(owner) :- sample1(S), findall(T, owner(p4, T, S), [i]).

test(armies) :- sample1(S), findall(A, armies(A, a, S), [1]).
test(armies) :- sample1(S), findall(A, armies(A, b, S), [3]).
test(armies) :- sample1(S), findall(A, armies(A, c, S), [6]).
test(armies) :- sample1(S), findall(A, armies(A, h, S), [3]).
test(armies) :- sample1(S), findall(A, armies(A, i, S), [1]).

test(round) :- sample1(S), get(round, 1, S).
test(round) :- sample2(S), get(round, 0, S).

test(initial_round) :- sample1(S), not(initial_round(S)).
test(initial_round) :- sample2(S), initial_round(S).

test(next_player) :- sample1(S), findall(N, next_player(N, S), [p2]).
test(next_player) :- sample2(S), findall(N, next_player(N, S), [p1]).

% transition predicates

sample3([
   [a,  b,  c,  d,  e,  f,  g,  h,  i], % territories
   [1,  3,  6,  2,  2,  4,  1,  3,  1], % armies
   [p2, p2, p2, p2, p1, p2, p3, p3, p4], % owners
   [p1, p2, p3, p4], % players
   [[world], [conts, 1, am], [kill, p5], [min, 3, 2]], % objective
   p4, % current player
   3, % round (0 is initial distribution)
   placing, % stage (placing, attacking, occupying, moving)
   0 % armies to place
]).

%TODO
test(transition_next) :- true.
test(transition_place) :- true.
test(transition_attack) :- true.

:- end_tests(rules).

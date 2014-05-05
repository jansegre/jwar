% vim: filetype=prolog et sw=4 ts=4 sts=4
%
:- module(rules, [get/3, getall/3, set/4, setall/4, territory/1, neighbours/2, continent/1, territory_continent/2, player/2, owner/3, armies/3, min_armies/3, objective/3, satisfies/2, initial_round/1, next_player/2, transition/4]).

%
% sample map (in the future this will have to be loaded)
%
% -- a - b       g --
%     \ / \     /
%      c   e - f
%      |       |
%      d       h - i
%
sample_map([
    [
        [a, [b, c, g]],
        [b, [a, c, e]],
        [c, [a, b, d]],
        [d, [c]],
        [e, [b, f]],
        [f, [e, g, h]],
        [g, [a, f]],
        [h, [i, f]],
        [i, [h]]
    ],
    [
        [aa, [a, b, c, d], 3],
        [bb, [e, f, g], 2],
        [cc, [h, i], 2]
    ]
]).
%TODO: predicate to load a map
%load_map([T, C]) :-
%:- sample_map(M), load_map(M).

% sample state:
%
% -- a(1) - b(3)        g(1) --
%       \   /  \         /
%        c(6)   e(2) - f(4)
%         |             |
%        d(2)          h(3) - i(1)
%
sample([
   [a,  b,  c,  d,  e,  f,  g,  h,  i], % territories
   [1,  3,  6,  2,  2,  4,  1,  3,  1], % armies
   [p2, p2, p2, p2, p1, p2, p3, p3, p4], % owners
   [p1, p2, p3, p4], % players
   [[world], [conts, 1, aa], [kill, p5], [min, 3, 2]], % objective
   p1, % current player
   1, % round (0 is initial distribution)
   attacking, % stage (placing, attacking, moving)
   0 % armies to place
]).

% helper function
% ref: http://stackoverflow.com/a/8544713/947511
% snth1(+Index, +List, +Elem, -NewList)
snth1(I, L, E, N) :- D =.. [d | L], setarg(I, D, E), D =.. [d | N].

% get(?Prop, ?Value, +State)
get(territories, V, S) :- nth1(1, S, V).
get(armies,      V, S) :- nth1(2, S, V).
get(owners,      V, S) :- nth1(3, S, V).
get(players,     V, S) :- nth1(4, S, V).
get(objectives,  V, S) :- nth1(5, S, V).
get(player,      V, S) :- nth1(6, S, V).
get(round,       V, S) :- nth1(7, S, V).
get(stage,       V, S) :- nth1(8, S, V).
get(to_place,    V, S) :- nth1(9, S, V).
% get_all(+PropList, ?ValueList, +State)
getall([], [], _).
getall([P | Pl], [V | Vl], S) :- get(P, V, S), getall(Pl, Vl, S).
% set(?Prop, +Value, +State, -NewState)
set(territories, V, S, N) :- snth1(1, S, V, N).
set(armies,      V, S, N) :- snth1(2, S, V, N).
set(owners,      V, S, N) :- snth1(3, S, V, N).
set(players,     V, S, N) :- snth1(4, S, V, N).
set(objectives,  V, S, N) :- snth1(5, S, V, N).
set(player,      V, S, N) :- snth1(6, S, V, N).
set(round,       V, S, N) :- snth1(7, S, V, N).
set(stage,       V, S, N) :- snth1(8, S, V, N).
set(to_place,    V, S, N) :- snth1(9, S, V, N).
% set_all(+PropList, ?ValueList, +State, -NewState)
setall([], [], S, S).
setall([P | Pl], [V | Vl], S, N) :- set(P, V, S, Sn), setall(Pl, Vl, Sn, N).

% base constructs, consistency is not enforced
%
% t(territory, neighbours)
:- dynamic t/2.
t(a, [b, c, g]).
t(b, [a, c, e]).
t(c, [a, b, d]).
t(d, [c]).
t(e, [b, f]).
t(f, [e, g, h]).
t(g, [a, f]).
t(h, [i, f]).
t(i, [h]).
% c(continent, territories, bonus)
:- dynamic c/3.
c(aa, [a, b, c, d], 3).
c(bb, [e, f, g], 2).
c(cc, [h, i], 2).
% o(objective, player, special_args, state).
% world domination, no args
o(world, _, _, [_, _, [] | _]).
o(world, X, _, [_, _, [X | L] | _]) :- o(world, X, _, [_, _, L, _]), !.
% certain numbers of territories, args: [MinT, MinArmiesPerT] or [MinT]
o(min, _, [0, _], _).
o(min, X, [Nt, Na], [_, [A | La], [X | Lp] | _]) :- A >= Na, Mt is Nt - 1, o(min, X, [Mt, Na], [_, La, Lp | _]), !.
o(min, X, [Nt, Na], [_, [A | La], [X | Lp] | _]) :- A < Na, o(min, X, [Nt, Na], [_, La, Lp | _]), !.
o(min, X, [Nt, Na], [_, [_ | La], [_ | Lp] | _]) :- o(min, X, [Nt, Na], [_, La, Lp | _]), !.
o(min, X, [Nt], S) :- o(min, X, [Nt, 1], S), !.
% eliminate player, args: [Player]
o(kill, X, [Y], [_, _, L | _]) :- not(member(Y, L)), member(X, L), !.
% certain continents, args: [MinArmies | Conts]
o(conts, _, [_], _).
o(conts, X, [Na, C | Lc], S) :- findall(T, territory_continent(T, C), Lt), findall(T, owner(X, T, S), Lt), o(conts, X, [Na | Lc], S).
% to be used in the future
o(unknown, _, _, _).

% territory(?Territor)
territory(X) :- t(X, _).

% neighbours(?Territory1, ?Territory2)
neighbours(X, Y) :- t(X, Z), member(Y, Z).

% continent(?Continent)
continent(X) :- c(X, _, _).

% territory_continent(?Territory, ?Continent)
territory_continent(T, C) :- t(T, _), c(C, Z, _), member(T, Z).

% player(?Player, +State)
player(X, [_, _, _, P | _]) :- member(X, P).


% State dependent predicates
% ==========================

% owner(?Player, ?Territory, +State)
owner(P, T, [[T | _], _, [P | _] | _]).
owner(P, T, [[_ | L], _, [_ | M] | _]) :- owner(P, T, [L, _, M | _]).

% set_owner(+NewOwner, ?Territory, +State, -NewState)
set_owner(P, T, S, Ns) :-
    getall([territories, owners], [Terrs, Ownrs], S),
    nth1(Ti, Terrs, T),
    snth1(Ti, Ownrs, P, NOwnrs),
    set(owners, NOwnrs, S, Ns).

% armies(?Armies, ?Territory, +State)
armies(N, T, [[T | _], [N | _] | _]).
armies(N, T, [[_ | L], [_ | M] | _]) :- armies(N, T, [L, M | _]).

% add_armies(+AddArmies, ?Territory, +State, -NewState)
add_armies(N, T, S, Ns) :-
    getall([territories, armies], [Terrs, Arms], S),
    nth1(Ti, Terrs, T),
    nth1(Ti, Arms, Ta),
    NTa is Ta + N,
    snth1(Ti, Arms, NTa, NArms),
    set(armies, NArms, S, Ns).

% min_armies(+TerritoryList, ?Min, +State)
% ref: http://stackoverflow.com/a/3966139/947511
min_armies([T], Min, S) :- armies(T, Min, S).
min_armies([T1, T2 | Ts], Min, S) :- armies(T1, N1, S), armies(T2, N2, S), N1 =< N2, min_armies([T1 | Ts], Min, S).
min_armies([T1, T2 | Ts], Min, S) :- armies(T1, N1, S), armies(T2, N2, S), N2 < N1, min_armies([T2 | Ts], Min, S).

% objective(?Player, ?Objective, +State)
% Objective = [type | args]
objective(P, O, [_, _, _, [P | _], [O | _] | _]).
objective(P, O, [_, _, _, [_ | L], [_ | M] | _]) :- objective(P, O, [_, _, _, L, M | _]).

% checks if player P satisfies its objective
satisfies(P, S) :- objective(P, [Obj | Args], S), o(Obj, P, Args, S).


% next_player(?NextPlayer, +State)
% next_player(?NextPlayer, -NextRound, +State)
% it is assumed that there are more than 1 player
next_player(N, 1, [_, _, _, [N | L], _, P | _]) :- last(L, P).
next_player(N, 0, [_, _, _, L, _, P | _]) :- nextto(P, N, L).
next_player(N, S) :- next_player(N, _, S).

% initial_round(+State)
initial_round(S) :- get(round, 0, S).


% The game state machine
% ======================

% partial transitions
% place army on T
transition_([place, T], S, Ns) :-
    getall([player, to_place], [Player, ToPlace], S),
    %getall([player, territories, armies, to_place], [Player, Terrs, Arms, ToPlace], S),
    owner(Player, T, S),
    %nth1(Ti, Terrs, T),
    %nth1(Ti, Arms, Ta),
    %NTa is Ta + 1,
    %snth1(Ti, Arms, NTa, NArms),
    NToPlace is ToPlace - 1,
    %setall([armies, to_place], [NArms, NToPlace], S, Ns).
    add_armies(1, T, S, Sp),
    set(to_place, NToPlace, Sp, Ns).
% attack Tdef from Tatk
transition_([attack, Tdef, Tatk], S, Ns, P, Satk) :-
    getall([stage, player], [attacking, Player], S),
    owner(Player, Tatk, S),
    neighbours(Tatk, Tdef),
    not(owner(Player, Tdef, S)),
    armies(Natk, Tatk, S),
    armies(Ndef, Tdef, S),
    %XXX: the number of dices is fixed to the max possible
    %     so the possibility tree doesn't expand too much,
    %     but officially you could attack with less.
    Datk is min(3, Natk - 1),
    Ddef is min(3, Ndef),
    prob(Datk, Ddef, Catk, Cdef, P),
    Satk is Datk - Catk,
    add_armies(-Catk, Tatk, S, Sp),
    add_armies(-Cdef, Tdef, Sp, Ns).

% transition(?[id | params], +State, -NextState, -Probability)
% place army on T, continue placing
transition([place, T], S, Ns, 1.0) :-
    getall([stage, to_place], [placing, N], S),
    N > 1,
    transition_([place, T], S, Ns).
% place army on T, go to attack stage
transition([place, T], S, Ns, 1.0) :-
    getall([stage, to_place], [placing, 1], S),
    transition_([place, T], S, Sp),
    set(stage, attacking, Sp, Ns).
% attack Tdef from Tatk, leading to an occupation
transition([attack, Tdef, Tatk], S, Ns, P) :-
    transition_([attack, Tdef, Tatk], S, Sp, P, Satk),
    armies(0, Tdef, Sp),
    get(player, Player, S),
    add_armies(-Satk, Tatk, Sp, Sp2),
    add_armies(Satk, Tdef, Sp2, Sp3),
    set_owner(Player, Tdef, Sp3, Ns).
% attack Tdef from Tatk, not leading to an occupation
transition([attack, Tdef, Tatk], S, Ns, P) :-
    transition_([attack, Tdef, Tatk], S, Ns, P, _),
    armies(Ndef, Tdef, Ns),
    Ndef > 0.
% done attacking
transition([done], S, Ns, 1.0) :-
    get(stage, attacking, S),
    set(stage, moving, S, Ns).
% 
transition([next], S, Ns, 1.0) :-
    getall([stage, round], [moving, Round], S),
    next_player(NPlayer, Radd, S),
    NRound is Round + Radd,
    %TODO: calculate armies_to_place
    NToPlace is 3,
    setall([round, to_place, stage, player], [NRound, NToPlace, placing, NPlayer], S, Ns).

% prob(?AtkDices, ?DefDices, ?AtkDeaths, ?DefDeaths, -Prob).
% official dices are D6
% ref: https://en.wikipedia.org/wiki/Risk_game#Dice_probabilities
% ref: https://pt.wikipedia.org/wiki/War#Territ.C3.B3rios_com_tr.C3.AAs_ex.C3.A9rcitos
% ref: http://www.braingle.com/news/hallfame.php?path=competition/games/risk.p&sol=1
prob(A, D, Ad, Dd, P) :-
    md(MaxA, MaxD),
    A =< MaxA,
    D =< MaxD,
    p(A, D, Ad, Dd, P).
% md(MaxAttackDices, MaxDefenceDices)
:- dynamic md/2.
md(3, 3).
% probs
% 1 (atk) vs 1 (def)
p(1, 1, 0, 1, 0.4166666667). %    15 / 36
p(1, 1, 1, 0, 0.5833333333). %    21 / 36
% 2 (atk) vs 1 (def)
p(2, 1, 0, 1, 0.5787037037). %   125 / 216
p(2, 1, 1, 0, 0.4212962963). %    91 / 216
% 3 (atk) vs 1 (def)
p(3, 1, 0, 1, 0.6597222222). %   855 / 1296
p(3, 1, 1, 0, 0.3402777778). %   441 / 1296
% 1 (atk) vs 2 (def)
p(1, 2, 0, 1, 0.2546296296). %    55 / 216
p(1, 2, 1, 0, 0.7453703704). %   161 / 216
% 2 (atk) vs 2 (def)
p(2, 2, 0, 2, 0.2276234568). %   295 / 1296
p(2, 2, 1, 1, 0.3240740741). %   420 / 1296
p(2, 2, 2, 0, 0.4483024691). %   581 / 1296
% 3 (atk) vs 2 (def)
p(3, 2, 0, 2, 0.3716563786). %  2890 / 7776
p(3, 2, 1, 1, 0.3357767490). %  2611 / 7776
p(3, 2, 2, 0, 0.2925668724). %  2275 / 7776
% 1 (atk) vs 3 (def)
p(1, 3, 0, 1, 0.1736111111). %   225 / 1296
p(1, 3, 1, 0, 0.8263888889). %  1071 / 1296
% 2 (atk) vs 3 (def)
p(2, 3, 0, 2, 0.1259002058). %   979 / 7776
p(2, 3, 1, 1, 0.2547582305). %  1981 / 7776
p(2, 3, 2, 0, 0.6193415638). %  4816 / 7776
% 3 (atk) vs 3 (def)
p(3, 3, 0, 3, 0.1376028807). %  6420 / 46656
p(3, 3, 1, 2, 0.2146990741). % 10017 / 46656
p(3, 3, 2, 1, 0.2646604938). % 12348 / 46656
p(3, 3, 3, 0, 0.3830375514). % 17871 / 46656

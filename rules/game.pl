/*
 * This file is part of JWar.
 *
 * JWar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.
 *
 */
:- module(game, [get/3, getall/3, set/4, setall/4, territory/1, neighbours/2, continent/1, territory_continent/2, player/2, owner/3, armies/3, min_armies/3, objective/3, satisfies/2, evaluate/3, initial_round/1, next_player/2, transition/4, expectmultimax/3, possible/3, play/3]).

%
% sample:
%
% -- a(1) - b(3)        g(1) --
%       \   /  \         /
%        c(6)   e(2) - f(4)
%         |             |
%        d(2)          h(3) - i(1)
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
sample_state([
   [a,  b,  c,  d,  e,  f,  g,  h,  i], % territories
   [1,  3,  1,  1,  2,  2,  1,  3,  1], % armies
   %[1,  3,  6,  2,  2,  4,  1,  3,  1], % armies
   [p2, p2, p2, p2, p1, p2, p3, p3, p4], % owners
   [p1, p2, p3, p4], % players
   [[world], [conts, 1, aa], [kill, p5], [min, 3, 2]], % objective
   p2, % current player
   1, % round (0 is initial distribution)
   moving, % stage (placing, attacking, rolling, occupying, moving)
   0, % armies to place
   [], % attack (used to hold an attack state before rolling the dice)
   %[] % prev_armies (used to hold the previous distribution of armies, for the moving stage)
   [1,  3,  1,  1,  2,  2,  1,  3,  1]
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
get(attack,      V, S) :- nth1(10, S, V).
get(prev_armies, V, S) :- nth1(11, S, V).
% getall(+PropList, ?ValueList, +State)
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
set(attack,      V, S, N) :- snth1(10, S, V, N).
set(prev_armies, V, S, N) :- snth1(11, S, V, N).
% setall(+PropList, ?ValueList, +State, -NewState)
setall([], [], S, S).
setall([P | Pl], [V | Vl], S, N) :- set(P, V, S, Sn), setall(Pl, Vl, Sn, N).

% base constructs, consistency is not enforced
%
:- dynamic t/2.
% t(territory, neighbours)
:- dynamic c/3.
% c(continent, territories, bonus)
addt_([Ter, Nei]) :- assertz(t(Ter, Nei)).
addc_([Con, Ters, Bon]) :- assertz(c(Con, Ters, Bon)).
load_map([T, C]) :-
    retractall(t(_, _)),
    retractall(c(_, _, _)),
    maplist(addt_, T),
    maplist(addc_, C).

:- use_module(library(aggregate)).
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
% FIXME: should be [NExtraConts | Conts]
% certain continents, args: [MinArmies | Conts]
o(conts, _, [_], _).
o(conts, X, [Na, C | Lc], S) :- findall(T, territory_continent(T, C), Lt), findall(T, owner(X, T, S), Lt), o(conts, X, [Na | Lc], S).
% to be used in the future
o(unknown, _, _, _).
% v(objective, player, special_args, state, evaluation).
v(world, X, _, S, V) :-
    aggregate_all(count, disowner(X, _, S), V).
v(min, X, [Nt, Na], S, V) :-
    aggregate_all(count,
        (owner(X, T, S), armies(N, T, S), N >= Na), Nx),
    V is Nt - Nx.
v(min, X, [Nt], S, V) :- v(min, X, [Nt, 1], S, V).
v(kill, _, [Y], S, V) :- aggregate_all(count, owner(Y, _, S), V).
v(conts, X, [Nex | Cons], S, V) :-
    aggregate_all(sum(Pv), (member(C, Cons), aggregate_all(count, (territory_continent(T, C), disowner(X, T, S)), Pv)), V1),
    (Nex > 0 *->
        findall(Pc, (continent(C), not(member(C, Cons)), aggregate_all(count, (territory_continent(T, C), disowner(X, T, S)), Pc)), Pl),
        msort(Pl, Pls),
        length(Plm, Nex),
        append(Plm, _, Pls),
        sum_list(Plm, V2);
    V2 is 0),
    V is V1 + V2.
%TODO: what about evaluation of unkown? fallback to world?
v(unknown, X, _, S, V) :- v(world, X, _, S, V).

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

% disowner(?Player, ?Territory, +State)
disowner(P, T, [[T | _], _, [Q | _] | _]) :- Q \= P.
disowner(P, T, [[_ | L], _, [_ | M] | _]) :- disowner(P, T, [L, _, M | _]).

% set_owner(+NewOwner, ?Territory, +State, -NewState)
set_owner(P, T, S, Ns) :-
    getall([territories, owners], [Terrs, Ownrs], S),
    nth1(Ti, Terrs, T),
    snth1(Ti, Ownrs, P, NOwnrs),
    set(owners, NOwnrs, S, Ns).

% armies(?Armies, ?Territory, +State)
armies(N, T, [[T | _], [N | _] | _]).
armies(N, T, [[_ | L], [_ | M] | _]) :- armies(N, T, [L, M | _]).

% prev_armies(?Armies, ?Territory, +State)
prev_armies(N, T, [[T | _], _, _, _, _, _, _, _, _, _, [N | _] | _]).
prev_armies(N, T, [[_ | L], _, _, _, _, _, _, _, _, _, [_ | M] | _]) :- armies(N, T, [L, M | _]).

% add_(+Prop, +AddArmies, ?Territory, +State, -NewState)
add_(Prop, N, T, S, Ns) :-
    getall([territories, Prop], [Terrs, Arms], S),
    nth1(Ti, Terrs, T),
    nth1(Ti, Arms, Ta),
    NTa is Ta + N,
    snth1(Ti, Arms, NTa, NArms),
    set(Prop, NArms, S, Ns).

% add_armies(+AddArmies, ?Territory, +State, -NewState)
add_armies(AddArmies, Territory, State, NewState) :- add_(armies, AddArmies, Territory, State, NewState).

% add_armies(+AddArmies, ?Territory, +State, -NewState)
add_prev_armies(AddArmies, Territory, State, NewState) :- add_(prev_armies, AddArmies, Territory, State, NewState).

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

% evaluate(?Player, -Value, +State)
evaluate(P, V, S) :- objective(P, [Ob | Args], S), v(Ob, P, Args, S, V), !.


% next_player(?NextPlayer, +State)
% next_player(?NextPlayer, -NextRound, +State)
% it is assumed that there are more than 1 player
next_player(N, 1, [_, _, _, [N | L], _, P | _]) :- last(L, P).
next_player(N, 0, [_, _, _, L, _, P | _]) :- nextto(P, N, L).
next_player(N, S) :- next_player(N, _, S).

% initial_round(+State)
initial_round(S) :- get(round, 0, S).

% armies_to_place(+Player, -Armies, +State)
%TODO: take continent bonus into account
armies_to_place(P, A, S) :-
    aggregate_all(count, owner(P, _, S), Nt),
    A is max(ceil(Nt / 3), 3).


% The game state machine
% ======================

%TODO: improve this to avoid same state generated from different paths
% ref: http://stackoverflow.com/questions/10611585/get-list-of-sets-where-the-sum-of-each-set-is-x
% ref: http://www.swi-prolog.org/pldoc/man?section=clpfd
%:- [library(clpfd)].
%get_combos(Sum, Length, List) :-
%    length(List, Length),
%    List ins 0..Sum,
%    sum(List, #=, Sum),
%    label(List).

% partial transitions
% place army on T
transition_([place, T], [S, Ns]) :-
    getall([player, to_place], [Player, ToPlace], S),
    owner(Player, T, S),
    NToPlace is ToPlace - 1,
    add_armies(1, T, S, Sp),
    set(to_place, NToPlace, Sp, Ns).
% attack Tdef from Tatk
transition_([attack, Tdef, Tatk], [S, Ns, P, Satk]) :-
    armies(Natk, Tatk, S),
    armies(Ndef, Tdef, S),
    % the number of dices is fixed to the max possible
    % so the possibility tree doesn't expand too much,
    % in Risk rules you could attack with less.
    Datk is min(3, Natk - 1),
    Ddef is min(3, Ndef),
    prob(Datk, Ddef, Catk, Cdef, P),
    Satk is Datk - Catk,
    add_armies(-Catk, Tatk, S, Sp),
    add_armies(-Cdef, Tdef, Sp, Spp),
    set(attack, [], Spp, Ns).
% next player
transition_([next], [S, Ns]) :-
    get(round, Round, S),
    next_player(NPlayer, Radd, S),
    NRound is Round + Radd,
    armies_to_place(NPlayer, NToPlace, S),
    setall([round, to_place, stage, player, prev_armies], [NRound, NToPlace, placing, NPlayer, []], S, Ns).

% transition(?[id | params], +State, -NextState, -Probability)
% place army on T, continue placing
transition([place, T], S, Ns, 1.0) :-
    getall([stage, to_place], [placing, N], S), N > 1,
    transition_([place, T], [S, Ns]).
% place army on T, go to attack stage
transition([place, T], S, Ns, 1.0) :-
    getall([round, stage, to_place], [R, placing, 1], S), R > 0,
    transition_([place, T], [S, Sp]),
    set(stage, attacking, Sp, Ns).
% place army on T, go to next player, initial round
transition([place, T], S, Ns, 1.0) :-
    getall([round, stage, to_place], [0, placing, 1], S),
    transition_([place, T], [S, Sp]),
    transition_([next], [Sp, Ns]).
% attack Tdef from Tatk, leading to an occupation
transition([attack, Tdef, Tatk], S, Ns, 1.0) :-
    getall([stage, player], [attacking, Player], S),
    owner(Player, Tatk, S),
    armies(Natk, Tatk, S),
    Natk > 1,
    neighbours(Tatk, Tdef),
    not(owner(Player, Tdef, S)),
    setall([attack, stage], [[Tdef, Tatk], rolling], S, Ns).
% attack Tdef from Tatk, leading to an occupation
transition([roll], S, Ns, P) :-
    getall([stage, attack], [rolling, [Tdef, Tatk]], S),
    transition_([attack, Tdef, Tatk], [S, Sp, P, _]),
    armies(0, Tdef, Sp),
    get(player, Player, S),
    set_owner(Player, Tdef, Sp, Sp4),
    set(stage, occupying, Sp4, Ns).
% attack Tdef from Tatk, not leading to an occupation
transition([roll], S, Ns, P) :-
    getall([stage, attack], [rolling, [Tdef, Tatk]], S),
    transition_([attack, Tdef, Tatk], [S, Sp, P, _]),
    armies(Ndef, Tdef, Sp),
    Ndef > 0,
    set(stage, attacking, Sp, Ns).
% occupy recently attacked with N troops
transition([occupy, N], S, Ns, 1.0) :-
    getall([stage, attack], [occupying, [Tdef, Tatk]], S),
    numlist(1, 3, Numl),
    member(N, Numl),
    armies(Natk, Tatk, S),
    N < Natk,
    add_armies(-N, Tatk, S, Sp),
    add_armies(N, Tdef, Sp, Spp),
    set(stage, attacking, Spp, Ns).
% done attacking
transition([done], S, Ns, 1.0) :-
    getall([stage, armies], [attacking, Arms], S),
    setall([stage, prev_armies], [moving, Arms], S, Ns).
% move an army from Torig to Tdest
transition([move, Torig, Tdest], S, Ns, 1.0) :-
    getall([stage, player], [moving, Player], S),
    owner(Player, Torig, S),
    neighbours(Torig, Tdest),
    owner(Player, Tdest, S),
    prev_armies(N, Torig, S), N > 1,
    add_armies(-1, Torig, S, Sp),
    add_prev_armies(-1, Torig, Sp, Spp),
    add_armies(1, Tdest, Spp, Ns).
% next player
transition([next], S, Ns, 1.0) :-
    get(stage, moving, S),
    transition_([next], [S, Ns]).

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


% The artificial intelligence(s)
% ==============================

% Helper predicates to draw the state tree
% ----------------------------------------

:- use_module(library('http/json')).
:- use_module(library('http/json_convert')).
possible(S, _, 0, @null, _, S).
possible(S, 0, _, @null, _, S).
possible(S, D, MaxD, Tj, Lt, Fs) :-
    D > 0, MaxD > 0,
    get(player, Pl, S), (
        % cut move
        get(stage, moving, S) *-> T = [next | _];
        % cut attacks
        get(stage, attacking, S) *-> T = [done | _];
        true
    ),
    transition(T, S, Ns, P), (
        T = [next | _] *-> NLt = false;
        T = [place, Ter], Lt \= false *-> Lt = Ter, NLt = Ter;
        NLt = Lt
    ),
    get(player, Np, Ns),
    (Pl \= Np *->
        Nd is D - 1;
        Nd is D),
    NMaxD is MaxD - 1,
    findall(Tc, possible(Ns, Nd, NMaxD, Tc, NLt, Fs), Lc),
    build_stat(T, P, Lc, Tj, S).
% build_stat(+Trans, +Prob, +Children, -JsonObject).
build_stat(T, P, [@null], J, S) :-
    P \= 1.0 *->
        J = json([s=S, t=T, p=P]);
        J = json([s=S, t=T]).
build_stat(T, P, [N | L], J, S) :-
    P \= 1.0 *->
        N \= @null, J = json([s=S, t=T, p=P, children=[N | L]]);
        N \= @null, J = json([s=S, t=T, children=[N | L]]).

possible(S, D, MD) :-
    open('t.json', write, Out),
    findall(T, possible(S, D, MD, T, _, _), Lt),
    json_write(Out, json([s=S, t=[''], children=Lt]), [step=4, tab=0]),
    close(Out).

% The ExpectMultiMax
% ------------------

% helper function to sum two lists [a, b, c] + [d, e, f] = [a + d, b + e, c + f]
% ref: http://stackoverflow.com/questions/15933103/prolog-summing-numbers-from-two-lists
list_sum([], [], []).
list_sum([H1 | T1], [H2 | T2], [X | L3]) :- list_sum(T1, T2, L3), X is H1 + H2.
list_sum([H1 | T1], [H2 | T2], [X | L3]) :- list_sum(T1, T2, L3), X is H1 + H2.
list_sum([L], L).
list_sum([L1, L2 | LL], L3) :- list_sum(L1, L2, LR), list_sum([LR | LL], L3), !.
multiply_list([], _, []).
multiply_list([N | Ns], P, [M | Ms]) :-
    M is N * P,
    multiply_list(Ns, P, Ms).

expectmultimax_eval(S, V) :-
    findall(Ev, (player(P, S), evaluate(P, Ev, S)), V).

% expectmultimax(+State, +Depth, -Transition)
expectmultimax(S, D, T) :- expectmultimax(S, D, T, [], _).

% expectmultimax(+State, +Depth, -Transition, _, +State)
expectmultimax(S, D, T, _, V) :-
    D > 0,
    get(stage, rolling, S) *->
        Nd is D - 1,
        findall(PVi, (
            transition(_, S, Ns, Pi),
            (Nd == 0 *->
                expectmultimax_eval(Ns, Vi);
                expectmultimax(Ns, Nd, _, _, Vi)),
            multiply_list(Vi, Pi, PVi)
        ), Lpv),
        list_sum(Lpv, V);
        Nd is D - 1,
        % max because evaluation is inversed
        aggregate_all(min(Pv, [Ti, Vi]), (
            transition(Ti, S, Ns, 1.0),
            (Nd == 0 *->
                expectmultimax_eval(Ns, Vi);
                expectmultimax(Ns, Nd, _, _, Vi)),
            sum_list(Vi, PPv),
            getall([players, player], [Pl, Pi], S),
            nth1(Np, Pl, Pi),
            nth1(Np, Vi, CPv),
            Pv is PPv - 2 * CPv
        ), min(_, [T, V])).



% Interfacing with the server
% ===========================

:- use_module(library(socket)).

% sgame(-Game, +Room), with local server and default port
sgame(['localhost', 4242, Room], Room).
% sgame(-Game, +Host, +Port, +Room)
sgame([H, P, R], H, P, R).

% query_cmd(+Game, +Command, -Result)
query_cmd([H, P, Ro], Q, T) :-
    tcp_socket(So),
    tcp_connect(So, H:P, S),
    stream_pair(S, R, W),
    format(W, "CMD ~w ~w~n", [Ro, Q]),
    flush_output(W),
    read_term(R, T, [double_quotes(string)]),
    close(S).

% query_state(+Game, -Result)
query_state([H, P, Ro], T) :-
    tcp_socket(So),
    tcp_connect(So, H:P, S),
    stream_pair(S, R, W),
    format(W, "STATE ~w~n", [Ro]),
    flush_output(W),
    read_term(R, T, [double_quotes(string)]),
    close(S).

% query_map(+Game, -Result)
query_map([H, P, Ro], M) :-
    tcp_socket(So),
    tcp_connect(So, H:P, S),
    stream_pair(S, R, W),
    format(W, "MAP ~w~n", [Ro]),
    flush_output(W),
    read_term(R, M, [double_quotes(string)]),
    close(S).

% Playing the game
% ================

% play(+Game, +Player, +Strategy)
play(G, P, S) :-
    query_map(G, [200, M]),
    load_map(M),
    play_loop(G, P, S).

play_loop(G, P, S) :-
    % sleep for 1 second
    sleep(1.0),
    query_state(G, [200, St]),
    (get(player, P, St) *->
        nl,
        step(S, St, Cmd),
        write(Cmd),
        write(': '),
        query_cmd(G, Cmd, Out),
        write(Out), nl;
        write('.')),
    flush_output,
    play_loop(G, P, simple).

step(expectmultimax, S, T) :-
    expectmultimax(S, 2, T),
    !.

step(simple, S, T) :-
    transition(T, S, _, _),
    !.

:- use_module(library(random)).
step(random, S, T) :-
    findall(Tp, transition(Tp, S, _, _), Tlist),
    write(Tlist), nl,
    random_member(T, Tlist),
    !.



% Load samples
% ============

% uncomment to use sample map:
:- sample_map(M), load_map(M).

% real samples:
% Map = [[[ka,[mo,ir,ja,ya,al]],[af,[me,in,uk,ur,ch]],[in,[af,me,si,ch]],[me,[af,in,uk,eg,ef,se]],[si,[in,ng,id,ch]],[sb,[ir,mo,ya,ur,ch]],[mo,[ka,ir,sb,ch]],[ir,[ka,mo,sb,ya,ch]],[ja,[ka,ch]],[ya,[ka,ir,sb]],[ur,[af,uk,sb,ch]],[ch,[af,in,si,ir,sb,mo,ja,ur]],[co,[sa,na,ef]],[sa,[co,ef,ma]],[na,[co,br,eg,ef,se,we]],[eg,[na,me,ef,se,we]],[ef,[co,na,sa,me,eg,ma]],[ma,[sa,ef]],[wa,[ea,ng,id]],[ea,[wa,ng]],[ng,[wa,ea,id]],[id,[wa,si,ng]],[ve,[br,ca,pe]],[br,[na,ve,ar,pe]],[ar,[br,pe]],[pe,[ve,br,ar]],[ab,[on,wu,nt,al]],[ca,[ve,eu,wu]],[eu,[ca,qu,on,wu]],[gr,[ic,qu,nt]],[qu,[eu,gr,on]],[on,[ab,eu,qu,wu,nt]],[wu,[ab,eu,ca,on]],[nt,[ab,gr,on,al]],[al,[ka,ab,nt]],[ne,[sc,uk,gb,se,we]],[sc,[ne,uk,gb]],[uk,[ne,sc,af,me,ur,se]],[ic,[gr,gb]],[gb,[ne,sc,ic,we]],[se,[ne,me,eg,uk,we]],[we,[ne,na,eg,gb,se]]],[[as,[ka,af,in,me,si,sb,mo,ir,ja,ya,ur,ch],7],[af,[co,sa,na,eg,ef,ma],3],[au,[wa,ea,ng,id],2],[sa,[ve,br,ar,pe],2],[na,[ab,ca,eu,gr,qu,on,wu,nt,al],5],[eu,[ne,sc,uk,ic,gb,se,we],5]]]
% State = [[ea,br,gb,nt,ca,ve,on,ka,qu,me,id,ic,ma,sc,ur,ar,se,in,wu,si,ir,uk,eu,mo,ya,ng,ab,ch,wa,gr,ja,we,af,al,pe,eg,co,ef,na,sa,ne,sb],[1,4,1,1,1,1,1,1,4,1,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,4,1,1,1,1,1,4,1,1,1,1,1,7,1,1],[verde,amarelo,azul,azul,branco,amarelo,branco,preto,verde,preto,vermelho,vermelho,verde,verde,preto,preto,verde,branco,amarelo,branco,azul,azul,azul,preto,vermelho,amarelo,vermelho,azul,amarelo,branco,preto,verde,vermelho,preto,amarelo,verde,azul,vermelho,vermelho,branco,amarelo,branco],[branco,amarelo,verde,azul,preto,vermelho],[[conts,1,eu,sa],[kill,preto],[conts,0,as,sa],[kill,vermelho],[conts,0,as,af],[conts,1,eu,au]],branco,1,attacking,0,[],[1,4,1,1,1,1,1,1,4,1,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,4,1,1,1,1,1,4,1,1,1,1,1,7,1,1]]

%
% vim: filetype=prolog et sw=4 ts=4 sts=4

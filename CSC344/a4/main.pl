:- use_module(library(clpfd)).

checkdistance([PX|[PY|_]], [QX|[QY|_]], MINDISTANCE) :-
    Distance is sqrt( (QX - PX)*(QX - PX) + (QY - PY)*(QY - PY) ),
    Distance >= MINDISTANCE.

socialDistanced([X|[Y|_]],[[Xo|[Yo|_]]|P]) :-
    length(P, L),
    L == 0 ->	checkdistance([X,Y],[Xo,Yo],6);
                checkdistance([X,Y],[Xo,Yo],6), socialDistanced([X, Y], P).    

bounded([X|[Y|_]],[A|[B|_]]) :- X >= 0, X < A, Y >= 0, Y < B.

safe(State, Q, Grid) :- bounded(State, Grid), socialDistanced(State, Q). 

move([X|[Y|_]], [NewX, NewY]) :- NewX is X, NewY is Y.
move([X|[Y|_]], [X1, Y1], Q, Grid) :- NewY is Y + 1, safe([X, NewY], Q, Grid), move([X, NewY], [X1, Y1]);
                                      NewX is X + 1, safe([NewX, Y], Q, Grid), move([NewX, Y], [X1, Y1]);
                                      NewX is X - 1, safe([NewX, Y], Q, Grid), move([NewX, Y], [X1, Y1]). 



checkFinal([_|[Y|_]], Final) :- Y = Final. 
solve(State, Final, Grid, Q, P).
solve(state, final, grid, q, p).

solve(State, Final, Grid, Q, [NextState|P]) :- checkFinal(State, Final) -> solve(state, final, grid, q, p);
                                                                           move(State, NextState, Q, Grid), solve(NextState, Final, Grid, Q, P).
                                   
                                  

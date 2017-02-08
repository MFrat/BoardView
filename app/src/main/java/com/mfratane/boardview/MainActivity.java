package com.mfratane.boardview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BoardView boardView = (BoardView) findViewById(R.id.checkers_board);

        boardView.setPiece(3, 3, R.drawable.torre);
        boardView.setPiece(0, 6, R.drawable.peao);
        boardView.setPiece(6, 2, R.drawable.horse);

        boardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onClickPiece(BoardView.Pos pos) {
                List<BoardView.Pos> positions = getPos(pos);
                boardView.markTiles(positions);

            }

            @Override
            public void onClickTile(BoardView.Pos posPiece, BoardView.Pos posTile) {
                boardView.movePiece(posPiece, posTile);
            }
        });
    }

    private List<BoardView.Pos> getPos(BoardView.Pos pos){
        if(pos.equals(new BoardView.Pos(3, 3))){
            return getPos1();
        }else if(pos.equals(new BoardView.Pos(6, 2))){
            return getPos2();
        }else{
            return getPos3();
        }
    }

    /*
    Stub
     */
    private List<BoardView.Pos> getPos1(){

        List<BoardView.Pos> positions = new ArrayList<>();

        positions.add(new BoardView.Pos(3, 0));
        positions.add(new BoardView.Pos(3, 1));
        positions.add(new BoardView.Pos(3, 2));

        positions.add(new BoardView.Pos(3, 4));
        positions.add(new BoardView.Pos(3, 5));
        positions.add(new BoardView.Pos(3, 6));
        positions.add(new BoardView.Pos(3, 7));

        positions.add(new BoardView.Pos(0, 3));
        positions.add(new BoardView.Pos(1, 3));
        positions.add(new BoardView.Pos(2, 3));

        positions.add(new BoardView.Pos(4, 3));
        positions.add(new BoardView.Pos(5, 3));
        positions.add(new BoardView.Pos(6, 3));
        positions.add(new BoardView.Pos(7, 3));

        return positions;
    }

    /*
    Stub2
     */
    private List<BoardView.Pos> getPos2(){

        List<BoardView.Pos> positions = new ArrayList<>();

        positions.add(new BoardView.Pos(7, 0));
        positions.add(new BoardView.Pos(7, 4));
        positions.add(new BoardView.Pos(4, 1));
        positions.add(new BoardView.Pos(4, 3));
        positions.add(new BoardView.Pos(5, 0));
        positions.add(new BoardView.Pos(5, 4));

        return positions;
    }

    private List<BoardView.Pos> getPos3(){

        List<BoardView.Pos> positions = new ArrayList<>();

        positions.add(new BoardView.Pos(1, 6));

        return positions;
    }

    private class Position{
        int i;
        int j;
    }
}

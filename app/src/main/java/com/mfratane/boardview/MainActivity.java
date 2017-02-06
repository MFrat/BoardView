package com.mfratane.boardview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BoardView boardView = (BoardView) findViewById(R.id.checkers_board);

        //Player one
        for(int i = 0; i <= 2; i++){
            for(int j = 0; j < BoardView.BOARD_DIMENSION; j++){
                if(j%2 == 0) {
                    boardView.setPiece(i, j, BoardView.PLAYER_ONE, BoardView.PIECE);
                }
            }
        }

        boardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onClickPiece(BoardView.Pos pos) {

            }

            @Override
            public void onClickTile(BoardView.Pos posPiece, BoardView.Pos posTile) {
                boardView.movePiece(posPiece, posTile);
            }
        });
    }
}

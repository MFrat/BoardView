# BoardView
Chess/Checkers board for Android.

# Samples
### Movement
<p align="start">
  <img src="http://i.imgur.com/TsJcZCg.gif" width="300"/>
</p>

# QuickStart
``` xml
    <com.mfratane.boardview.BoardView
        android:id="@+id/checkers_board"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:clickAnabled="true"
        app:tileMarkingEnabled="false"
        app:darkTileImage="@drawable/my_light_tile"
        app:lightTileImage="@drawable/my_dark_tile"/>
```


``` java
final BoardView boardView = (BoardView) findViewById(R.id.checkers_board);

boardView.setBoardListener(new BoardView.BoardListener() {
    @Override
    public void onClickPiece(BoardView.Pos pos) {
        /*
         A piece was clicked.
         Here you can mark the valid tiles for this piece movement.
         */

        /*
         Example
         */
        //Get all valid positions for the piece clicked.
        List<Position> validPositions = myChessRule.getValidPositions(pos.getI(), pos.getJ());

        //Marks all valid tiles.
        for(Position position : validPositions){
            boardView.markTile(position.getX(), position.getY());
        }
    }

    @Override
    public void onClickTile(BoardView.Pos posPiece, BoardView.Pos posTile) {

        /*
        If tileMarkingEnabled == true, this method will only be called when the tile is marked,
        otherwise this movement can be invalid for your rules, so you have to verify in your ruuning chess/chekers
        rule if this movement is valid
         */

        try{
            //Move on your rule.
            myChessRule.move(posPiece.getI(), posPiece.getJ(), posTile.getI(), posTile.getJ());
            //Move on the board.
            boardView.movePiece(posPiece, posTile);
        }catch(InvalidMovementException e){
            //TODO
            //Warn user.
            //Invalid movement, do nothing.
        }
    }
});
```

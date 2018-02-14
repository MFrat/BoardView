# BoardView
1. [Samples](#samples)
2. [Features](#features)
    1. [Path Animation](#path_animation)
2. [QuickStart](#quickstart)
3. [Attributes](#attributes)
    1. [TileMarkingEnabled](#tileMarkingEnabled)
    2. [TileMarkingAnimation](#tileMarkingAnimation)
    3. [InvalidPosClickColor](#invalidPosClickColor)
    4. [MarkedTileColor](#markedTileColor)
    5. [LightTileImage](#lightTileImage)
    6. [DarkTileImage](#darkTileImage)
    7. [ClickAnabled](#clickAnabled)

Chess/Checkers board for Android.
IMPORTANT! This is just a interface, there is no checkers/chess rules running.

# Samples
<p align="start">
  <img src="https://i.imgur.com/WofK8k7.gif" width="200"/>
  <img src="https://i.imgur.com/6MTCDtG.gif" width="200"/>
</p>

# Features
### Path Animation
Path animation is a feature that allows you to set some positions for the piece animation.

``` java
boardView.movePiece(posList);
```

Where `posList` is a instance of `List<BoardView.Pos>`.

<p align="start">
  <img src="https://i.imgur.com/w9lIbNs.gif" width="200"/>
</p>

# QuickStart
``` xml
    <com.mfratane.boardview.BoardView
        android:id="@+id/checkers_board"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:clickAnabled="true"
        app:tileMarkingEnabled="true"
        app:darkTileImage="@drawable/casa"
        app:lightTileImage="@drawable/base_g"
        app:markedTileColor="@android:color/holo_blue_light"
        app:invalidPosClickColor="@android:color/holo_red_dark"
        app:tileMarkingAnimation="IterativeFading"/>
```
`app:tileMarkingEnabled="true"` With this enabled, `public void onClickTile` will only be called when a click is perfomed in a marked tile. This is very useful to pre-handle invalid positions.



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
         
         
        /*
        if tileMarkingEnabled == false
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
        
        /*
        if tileMarkingEnabled == true
        */
        boardView.movePiece(posPiece, posTile);
    }
});
```

# Attributes
#### TileMarkingEnabled
It handles click events in invalid positions. If you are marking valid positions for the next move and if `tileMarkingEnabled` is true, then `onClickTile` won't be called if the click was on a unmarked position.

#### TileMarkingAnimation
The animation that will occur in tile marking. There is two animations available:

#### InvalidPosClickColor
Color that will be displayed over a tile when a click on a invalid(unmarked) pos occurs.

#### MarkedTileColor
Color that will be displayed over a tile when it is marked.

#### LightTileImage
Image displayed as a light tile.

#### DarkTileImage
Image displayed as a dark tile.

#### ClickAnabled
If it is `true` disables all clicks on the board.

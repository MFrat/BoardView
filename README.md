# BoardView
Chess/Checkers board for Android.

# Samples
### Movement
<p align="start">
  <img src="http://i.imgur.com/TsJcZCg.gif" width="300"/>
</p>

# Usage

### XML
``` xml
<com.mfratane.boardview.BoardView
        android:id="@+id/checkers_board"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        boardView:clickAnabled="true"
        boardView:tileMarkingEnabled="true"/>
```

### Constructor
``` java
BoardView boardView = (BoardView) findViewById(R.id.checkers_board);
```
or
``` java
BoardView boardView = new BoardView(this);
```

### Listener (Callback interface)
``` java
public interface BoardListener {
    void onClickPiece(Pos pos);
    void onClickTile(Pos posPiece, Pos posTile);
}
```

``` java
void onClickPiece(Pos pos);
```
Is called when a click is performed on a piece.

``` java
void onClickTile(Pos posPiece, Pos posTile);
```
Is called when a click is performed on a tile.

This method is called only if a piece was clicked before it.

If `tileMarkingEnabled == true`, this method will only be called if the clicked tile is marked as a valid position.

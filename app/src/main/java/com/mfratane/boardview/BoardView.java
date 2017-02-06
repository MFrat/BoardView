package com.mfratane.boardview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * View personalizada que representa um tabuleiro de jogo de damas.
 * IMPORTANTE
 * Essa classe não sabe nada de regras. Ela somente move e desenha, peças e tabuleiro.
 */
public class BoardView extends GridLayout {
    /**
     *
     */
    private View[][] piecesMatrix;

    /**
     * Tile background.
     */
    private int lightTile = R.drawable.casa_transparente;
    private int darkTile = R.drawable.casa;

    /**
     * Players images.
     */
    private int playerOnePiece = R.drawable.pt_peao_2;
    private int playerTwoPiece = R.drawable.psdb_peao;
    private int playerOneKing = R.drawable.pt_dama_2;
    private int playerTwoKing = R.drawable.psdb_dama;

    /**
     * Player's static constants.
     */
    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = 2;
    public static final boolean KING = true;
    public static final boolean PIECE = false;

    /**
     * Variáveis que auxiliam no controle da movimentação das peças nas peças.
     */
    private View lastSelectedPiece;

    /**
     * Markedtiles for the last selected piece.
     */
    private List<View> markedTiles;
    private boolean markedTilesEnabled = false;

    /**
     * Board dimension.
     * Default 8x8.
     */
    public static final int BOARD_DIMENSION = 8;

    /**
     * Piece's moviment anim duration.
     */
    private int duracaoAnimMovimento = 500;

    /**
     * Enables/Disables click on the board.
     */
    private boolean clickEnabled = true;

    /**
     * Listener.
     */
    private BoardListener boardListener;


    public BoardView(Context context) {
        super(context);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();
        createBoard();
    }


    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();
        createBoard();

    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();
        createBoard();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();
        createBoard();
    }

    @Override
    public void onMeasure(int width, int height){
        int parentWidth = MeasureSpec.getSize(width);
        int parentHeight = MeasureSpec.getSize(height);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY));
    }

    public void setCasaTransparente(int casa_transparente) {
        this.lightTile = casa_transparente;
    }

    public void setDarkTile(int darkTile) {
        this.darkTile = darkTile;
    }

    public void setPlayerOnePiece(int playerOnePiece) {
        this.playerOnePiece = playerOnePiece;
    }

    public void setPlayerTwoPiece(int playerTwoPiece) {
        this.playerTwoPiece = playerTwoPiece;
    }

    public void setPlayerOneKing(int playerOneKing) {
        this.playerOneKing = playerOneKing;
    }

    public void setPlayerTwoKing(int playerTwoKing) {
        this.playerTwoKing = playerTwoKing;
    }

    protected int larguraTabuleiro() {
        return getDisplay((Activity) getContext()).widthPixels;
    }

    public static DisplayMetrics getDisplay(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public void setLastSelectedPiece(Pos pos) {
        this.lastSelectedPiece = piecesMatrix[pos.getI()][pos.getJ()];
    }

    public void enableMarkedTiles(boolean bool){
        this.markedTilesEnabled = bool;
    }

    protected int larguraCasa() {
        return larguraTabuleiro() / BOARD_DIMENSION;
    }

    public void setDuracaoAnimMovimento(int duracaoAnimMovimento) {
        this.duracaoAnimMovimento = duracaoAnimMovimento;
    }

    private void createBoard(){
        this.setBackgroundResource(R.drawable.base_g);
        this.setRowCount(BOARD_DIMENSION);
        this.setColumnCount(BOARD_DIMENSION);

        setBoardBackground();
    }

    /**
     * Remove a piece from the board.
     * @param i coordenada i.
     * @param j coordenada j.
     */
    public void removePiece(int i, int j){
        if(!isPosValid(i, j)){
            throw new IllegalArgumentException("Invalid position: [" + String.valueOf(i) + ";" + String.valueOf(j) + "].");
        }

        View view = piecesMatrix[i][j];
        view.setVisibility(View.GONE);
        piecesMatrix[i][j] = null;
    }

    /**
     * Create board background.
     */
    private void setBoardBackground(){
        //Para cada coordenada do tabuleiro, cria uma view.
        for(int i = 0; i < BOARD_DIMENSION; i++){
            for(int j = 0; j < BOARD_DIMENSION; j++){
                View tile = createTile(chooseTileColor(i, j), larguraCasa());
                addViewToGrid(tile, i, j);
            }
        }
    }

    /**
     * Create a piece and set it on te given position.
     * @param i position coord i.
     * @param j position coord j.
     * @param player wich team this piece will be.
     * @param isKing if it is king or not.
     */
    public void setPiece(int i, int j, int player, boolean isKing){
        View peca = createPiece(player, isKing);

        if(!isPosValid(i, j)){
            throw new IllegalArgumentException("Invalid position: [" + String.valueOf(i) + ";" + String.valueOf(j) + "].");
        }

        piecesMatrix[i][j] = peca;

        addViewToGrid(peca, i, j);
    }

    /**
     * Aux for background intercalation.
     * @param i tile coord i.
     * @param j tile coord j.
     * @return image id.
     */
    private int chooseTileColor(int i, int j){
        if(i%2 == 0){
            if(j%2 == 0) {
                return lightTile;
            }else{
                return darkTile;
            }
        }else{
            if(j%2 == 0){
                return darkTile;
            }else{
                return lightTile;
            }
        }
    }

    /**
     * Get tile position.
     * @param tile tile being searched.
     * @return Pos instance.
     */
    private Pos getTilePos(View tile){
        int index = this.indexOfChild(tile);

        int linha = index / 8;
        int coluna = index % 8;

        return new Pos(linha, coluna);
    }

    /**
     * Get piece position[
     * @param piece piece being searched.
     * @return Pos instance.
     */
    private Pos getPiecePos(View piece){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                View aux = piecesMatrix[i][j];
                if(aux != null){
                    if(aux.equals(piece)){
                        return new Pos(i, j);
                    }
                }
            }
        }

        return null;
    }

    /**
     *
     * @param view View being added.
     * @param i i coord.
     * @param j j coord.
     */
    private void addViewToGrid(View view, int i, int j){
        GridLayout.Spec indexI = GridLayout.spec(i);
        GridLayout.Spec indexJ = GridLayout.spec(j);

        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(indexI, indexJ);

        this.addView(view, gridParam);
    }

    public void markTile(int i, int j){
        int pos = (i * 8) + j;
        FrameLayout view = (FrameLayout) this.getChildAt(pos);
        ImageView image = (ImageView) view.getChildAt(0);
        image.setImageResource(R.drawable.markedTile);

        markedTiles.add(view);
    }

    public void unmarkTile(int i, int j){
        int pos = (i * 8) + j;
        FrameLayout view = (FrameLayout) this.getChildAt(pos);
        ImageView image = (ImageView) view.getChildAt(0);
        image.setImageResource(chooseTileColor(i, j));
    }

    public void unmarkAllTiles(){
        for(View view : markedTiles){
            Pos posViewTabuleiro = getTilePos(view);
            unmarkTile(posViewTabuleiro.getI(), posViewTabuleiro.getJ());
        }

        markedTiles.clear();
    }


    private FrameLayout createTile(int imageId, int largura){
        FrameLayout tile = new FrameLayout(getContext());
        ImageView imageView = new ImageView(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(largura, largura);

        tile.setLayoutParams(params);
        imageView.setLayoutParams(params);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imageId);
        imageView.setAlpha((float) 0.8);
        tile.addView(imageView);

        tile.setOnClickListener(onClickTile());

        return tile;
    }


    private FrameLayout createPiece(int time, boolean dama){
        int imageId = 0;
        if(time == 1){
            imageId = (dama)? playerOneKing : playerOnePiece;
        }

        if(time == 2){
            imageId = (dama)? playerTwoKing : playerTwoPiece;
        }

        int tamanho = larguraCasa();

        FrameLayout frameLayout = new FrameLayout(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tamanho, tamanho, Gravity.CENTER);
        //params.setMargins(10, 10, 10, 10);

        ImageView piece = new ImageView(getContext());
        piece.setLayoutParams(params);
        piece.setImageResource(imageId);
        piece.setScaleType(ImageView.ScaleType.CENTER_CROP);
        piece.setAlpha((float) 0.85);

        frameLayout.setOnClickListener(onClickPiece());

        frameLayout.addView(piece);

        return frameLayout;
    }

    private View getPiece(Pos pos){
        return piecesMatrix[pos.getI()][pos.getJ()];
    }

    private View getTile(Pos pos){
        return getChildAt((pos.getI()*8) + pos.getJ());
    }


    private View.OnClickListener onClickPiece(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClickEnabled()){
                    return;
                }

                lastSelectedPiece = v;

                unmarkAllTiles();

                if(boardListener != null){
                    Pos pos = getPiecePos(v);
                    boardListener.onClickPiece(pos);
                }
            }
        };
    }


    private View.OnClickListener onClickTile(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClickEnabled()){
                    return;
                }

                if(markedTilesEnabled && markedTiles.indexOf(v) == -1){
                    return;
                }

                if(boardListener != null){
                    Pos pos = getTilePos(v);
                    Pos posPeca = getPiecePos(lastSelectedPiece);
                    boardListener.onClickTile(posPeca, pos);
                }

                unmarkAllTiles();
            }
        };
    }

    public void movePiece(Pos startPos, Pos endPos){
        View view = piecesMatrix[startPos.getI()][startPos.getJ()];

        piecesMatrix[endPos.getI()][endPos.getJ()] = piecesMatrix[startPos.getI()][startPos.getJ()];
        piecesMatrix[startPos.getI()][startPos.getJ()] = null;

        View casa = getChildAt((endPos.getI()*8) + endPos.getJ());

        pieceMovAnim(view, casa);
    }

    public void setKing(Pos pos, int time){
        FrameLayout view = (FrameLayout) piecesMatrix[pos.getI()][pos.getJ()];
        final ImageView imageView = (ImageView) view.getChildAt(0);

        final int img = (time == PLAYER_ONE)? playerOneKing : playerTwoKing;

        imageView.setImageResource(img);
    }

    private void pieceMovAnim(View piece, View tile){
        float finalX = tile.getX();
        float finalY = tile.getY();

        ObjectAnimator animX = ObjectAnimator.ofFloat(piece, "x", finalX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(piece, "y", finalY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        animatorSet.setDuration(duracaoAnimMovimento);
        animatorSet.start();
    }

    private boolean isPosValid(int i, int j){
        int max  = BOARD_DIMENSION - 1;
        return !((i < 0 || i > max) || (j < 0 || j > max));
    }

    public boolean isClickEnabled() {
        return clickEnabled;
    }

    public void setClickEnabled(boolean clickEnabled) {
        this.clickEnabled = clickEnabled;
    }

    public void setBoardListener(BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    public interface BoardListener {
        void onClickPiece(Pos pos);
        void onClickTile(Pos posPiece, Pos posTile);
    }


    public static class Pos{
        private int i;
        private int j;

        public Pos(){

        }

        public Pos(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        @Override
        public String toString() {
            return "Pos{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }
    }
}

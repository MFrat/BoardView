package com.mfratane.boardview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends GridLayout {
    private View[][] piecesMatrix;

    /**
     * Tile background.
     */
    private Drawable lightTile;
    private Drawable darkTile;
    private int markedTileColor;
    private int invalidPosClickColor;


    private View lastSelectedPiece;

    /**
     * Markedtiles for the last selected piece.
     */
    private List<View> markedTiles;
    private boolean markedTilesEnabled;

    /**
     * Marking tile animation.
     */
    private int anim;

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

        initVar(context, attrs);

        createBoard();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();

        initVar(context, attrs);

        createBoard();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        piecesMatrix = new View[BOARD_DIMENSION][BOARD_DIMENSION];
        markedTiles = new ArrayList<>();

        initVar(context, attrs);

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

    private void initVar(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BoardView,
                0, 0);

        try {
            markedTilesEnabled = a.getBoolean(R.styleable.BoardView_tileMarkingEnabled, false);
            clickEnabled = a.getBoolean(R.styleable.BoardView_clickAnabled, true);
            darkTile = a.getDrawable(R.styleable.BoardView_darkTileImage);
            lightTile = a.getDrawable(R.styleable.BoardView_lightTileImage);
            markedTileColor = a.getColor(R.styleable.BoardView_markedTileColor, Color.CYAN);
            anim = a.getInt(R.styleable.BoardView_tileMarkingAnimation, 0);
            invalidPosClickColor = a.getColor(R.styleable.BoardView_invalidPosClickColor, Color.RED);
        } finally {
            a.recycle();
        }
    }

    protected int larguraTabuleiro() {
        return getDisplay((Activity) getContext()).widthPixels;
    }

    private DisplayMetrics getDisplay(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
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
                View tile = createTile(chooseTileColor(i, j));
                addViewToGrid(tile, i, j);
            }
        }
    }

    /**
     * Create a piece and set it on te given position.
     * @param i position coord i.
     * @param j position coord j.
     */
    public void setPiece(int i, int j, int imageid){
        View peca = createPiece(imageid);

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
    private Drawable chooseTileColor(int i, int j){
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

        int linha = index / BOARD_DIMENSION;
        int coluna = index % BOARD_DIMENSION;

        return new Pos(linha, coluna);
    }

    /**
     * Get piece position[
     * @param piece piece being searched.
     * @return Pos instance.
     */
    private Pos getPiecePos(View piece){
        for(int i = 0; i < BOARD_DIMENSION; i++){
            for(int j = 0; j < BOARD_DIMENSION; j++){
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

    private Animation iterativeFadeInAnim(int duration, int offset){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setStartOffset(offset);

        return fadeIn;
    }

    private Animation simpleFadeIn(int duration){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(duration);

        return fadeIn;
    }

    private void fadeViewOut(View view){
        FrameLayout frameLayout = (FrameLayout) view;
        final View image = frameLayout.getChildAt(1);

        Animation animation = simpleFadeIn(100);


        image.setBackgroundColor(invalidPosClickColor);
        image.setVisibility(VISIBLE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        image.startAnimation(animation);
    }

    public void invalidPosAnim(int i, int j){
        View view = getTile(new Pos(i, j));
        FrameLayout frameLayout = (FrameLayout) view;
        final View image = frameLayout.getChildAt(1);

        Animation animation = simpleFadeIn(100);


        image.setBackgroundColor(invalidPosClickColor);
        image.setVisibility(VISIBLE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        image.startAnimation(animation);
    }

    private Animation selectAnimation(int duration, int offset){
        switch (anim){
            case 0:
                return simpleFadeIn(duration);
        }

        return iterativeFadeInAnim(duration, offset);
    }

    public void markTile(int i, int j, int offset){
        int pos = (i * BOARD_DIMENSION) + j;
        FrameLayout view = (FrameLayout) this.getChildAt(pos);
        View image = view.getChildAt(1);
        image.setBackgroundColor(markedTileColor);
        Animation animation = selectAnimation(350, offset);
        image.setVisibility(VISIBLE);

        image.startAnimation(animation);

        markedTiles.add(view);
    }

    public void unmarkTile(int i, int j){
        int pos = (i * BOARD_DIMENSION) + j;
        FrameLayout view = (FrameLayout) this.getChildAt(pos);
        View image =  view.getChildAt(1);
        image.setVisibility(GONE);
    }

    public void unmarkAllTiles(){
        for(View view : markedTiles){
            Pos posViewTabuleiro = getTilePos(view);
            unmarkTile(posViewTabuleiro.getI(), posViewTabuleiro.getJ());
        }

        markedTiles.clear();
    }


    private FrameLayout createTile(Drawable imageId){
        int tamanho = larguraCasa();

        FrameLayout frameLayout = new FrameLayout(getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tamanho, tamanho);

        ImageView piece = new ImageView(getContext());
        piece.setLayoutParams(params);
        piece.setBackground(imageId);
        piece.setScaleType(ImageView.ScaleType.CENTER_CROP);

        FrameLayout marked = new FrameLayout(getContext());
        marked.setLayoutParams(params);
        marked.setBackgroundColor(markedTileColor);
        marked.setAlpha((float) 0.5);
        marked.setVisibility(GONE);

        frameLayout.setOnClickListener(onClickTile());

        frameLayout.addView(piece);
        frameLayout.addView(marked);

        return frameLayout;
    }


    private LinearLayout createPiece(int imageId){
        int tamanho = larguraCasa();

        LinearLayout frameLayout = new LinearLayout(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamanho, tamanho, Gravity.CENTER);

        ImageView piece = new ImageView(getContext());
        int padding = (int) pxFromDp(getContext(), 5);
        piece.setPadding(padding, padding, padding, padding);
        piece.setLayoutParams(params);
        piece.setImageResource(imageId);
        piece.setScaleType(ImageView.ScaleType.CENTER_CROP);

        frameLayout.setOnClickListener(onClickPiece());

        frameLayout.addView(piece);

        return frameLayout;
    }

    private float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
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
                    fadeViewOut(v);
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

        View casa = getChildAt((endPos.getI()*BOARD_DIMENSION) + endPos.getJ());

        pieceMovAnim(view, casa);
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

    public void markTiles(List<Pos> positions){
        List<Pos> ordered = orderPos(positions);

        int offset = 0;

        for(Pos pos : ordered){
            markTile(pos.getI(), pos.getJ(), offset);
            offset += 50;
        }
    }

    private List<Pos> orderPos(List<Pos> positions){
        for(int i = 0; i < positions.size(); i++){
            for(int j = i; j < positions.size(); j++){
                if(biggerThan(getPiecePos(lastSelectedPiece), positions.get(i), positions.get(j))){
                    Pos aux = positions.get(i);
                    positions.set(i, positions.get(j));
                    positions.set(j, aux);
                }
            }
        }

        return positions;
    }

    private boolean biggerThan(Pos posBase, Pos pos1, Pos pos2){
        int distance1 = distance(posBase, pos1);
        int distance2 = distance(posBase, pos2);

        return distance1 >= distance2;
    }

    private int distance(Pos pos1, Pos pos2){
        double delta1 = Math.pow((pos1.getI() - pos2.getI()), 2);
        double delta2 = Math.pow((pos1.getJ() - pos2.getJ()), 2);

        return (int) Math.sqrt(delta1 + delta2);
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

        public boolean equals(Pos pos){
            return pos.getI() == this.getI() && pos.getJ() == this.getJ();
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

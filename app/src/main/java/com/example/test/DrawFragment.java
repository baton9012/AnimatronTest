package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawFragment extends Fragment {

    private Random random = new Random();
    private int mMaxHex = 20;
    private int mNumberHex = random.nextInt(mMaxHex)+1;
    private Pt[][] mHexs;
    private List<Pt> mCentersHex;
    private List<Pt> mUsedHexCenter;
    private Pt[][] mUsedHex;
    private boolean isDrawebled = true;
    private Hex hex;
    private Paint mFirstSelectedHexPaint = new Paint();
    private Path mFirstSelectedHexPath = new Path();
    private Paint mSecondHexSelectedPaint = new Paint();
    private Path mSecondSelectedHexPath = new Path();
    private Paint mPathPaint = new Paint();
    private Path mPathPath = new Path();
    private Paint mHexPaint = new Paint();
    private Path mHexPath = new Path();
    private boolean isImp;
    private int mX;
    private int mLeftX;
    private int mRightX;
    private int mY;
    private int mSecondY;
    private int mThirtyY;
    private int mFortyY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.draw_fragment, container, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.draw);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        hex = new Hex(getContext(), display.getWidth(), display.getHeight(), true);
        relativeLayout.addView(hex);
        return view;
    }

    public void loadHex(float x, float y, int i, int countHex) {
        if (i == 0) {
            mHexs = new Pt[countHex][7];
            mCentersHex = new ArrayList<>();
        }
        mX = (int) x;
        mY = (int) y;
        mLeftX = mX -25;
        mRightX = mX +25;
        mSecondY = mY +25;
        mThirtyY = mY +50;
        mFortyY = mY +75;
        mHexs[i][0] = new Pt(mX, mY);
        mHexs[i][1] = new Pt(mRightX, mSecondY);
        mHexs[i][2] = new Pt(mRightX, mThirtyY);
        mHexs[i][3] = new Pt(mX, mFortyY);
        mHexs[i][4] = new Pt(mLeftX, mThirtyY);
        mHexs[i][5] = new Pt(mLeftX, mSecondY);
        mHexs[i][6] = new Pt(mX, mY);
        mCentersHex.add(new Pt(mHexs[i][0]._mX, (mHexs[i][1]._mY + mHexs[i][2]._mY) / 2, 0));
        isImp = true;
    }

    public Pt[][] getHexs() {
        return mHexs;
    }

    public List<Pt> getUsedHex() {
        return mUsedHexCenter;
    }

    public void loadUsedHex(int i, float x, float y, int countUsedHex) {
        if (i == 0) {
            mUsedHex = new Pt[countUsedHex][7];
            mUsedHexCenter = new ArrayList<>();
        }
        float secondY = y - 12;
        float firstY = secondY - 25;
        float thirtyY = y + 12;
        float fortyY = thirtyY + 25;
        mUsedHexCenter.add(new Pt(x, y));
        mUsedHex[i][0] = new Pt(x, firstY);
        mUsedHex[i][1] = new Pt(x+25, secondY);
        mUsedHex[i][2] = new Pt(x+25, thirtyY);
        mUsedHex[i][3] = new Pt(x, fortyY);
        mUsedHex[i][4] = new Pt(x-25, thirtyY);
        mUsedHex[i][5] = new Pt(x-25, secondY);
        mUsedHex[i][6] = new Pt(x, firstY);
    }

    public void invalidate() {
        mHexPath.moveTo(mHexs[0][0]._mX, mHexs[0][0]._mY);
        if (mUsedHexCenter != null) {
            mFirstSelectedHexPaint = new Paint();
            mFirstSelectedHexPaint.setColor(Color.RED);
            mSecondHexSelectedPaint = new Paint();
            mSecondHexSelectedPaint.setColor(Color.GREEN);
            mPathPaint = new Paint();
            mPathPaint.setColor(Color.YELLOW);
            mFirstSelectedHexPath = new Path();
            mSecondSelectedHexPath = new Path();
            mPathPath = new Path();
            mPathPath.moveTo(mUsedHex[0][0]._mX, mUsedHex[0][0]._mY);
            for (int i = 0; i < mUsedHex.length; i++) {
                for (int j = 0; j < 7; j++) {
                    mPathPath.lineTo(mUsedHex[i][j]._mX, mUsedHex[i][j]._mY);
                }
            }
            mFirstSelectedHexPath.moveTo(mUsedHex[0][0]._mX, mUsedHex[0][0]._mY);
            mSecondSelectedHexPath.moveTo(mUsedHex[mUsedHex.length-1][0]._mX, mUsedHex[mUsedHex.length-1][0]._mY);
            for (int j = 0; j < 7; j++) {
                mFirstSelectedHexPath.lineTo(mUsedHex[0][j]._mX, mUsedHex[0][j]._mY);
                mSecondSelectedHexPath.lineTo(mUsedHex[mUsedHex.length-1][j]._mX, mUsedHex[mUsedHex.length-1][j]._mY);
            }
        }
        isDrawebled = false;
        hex.invalidate();
    }

    public void createCenterHex(Pt[][] pts) {
        mCentersHex = new ArrayList<>();
        for (Pt[] pt : pts) {
            mCentersHex.add(new Pt(pt[0]._mX, (pt[1]._mY + pt[2]._mY) / 2, 0));
        }
    }

    class Hex extends View {
        int mMaxX;
        int mMinX = 0;
        int mMaxY;
        int mMinY = 0;
        int nextX;
        int nextRightX;
        int nextLeftX;
        int nextY;
        int nextSecondY;
        int nextThirtyY;
        int nextFortyY;
        float firstPressedX;
        float firstPressedY;
        float secondPressedX;
        float secondPressedY;
        int countPressed = 0;
        int mFirstHex;
        int mSecondHex;

        public Hex(Context context, int width, int height, boolean drawebled) {
            super(context);
            mMaxX = width/2;
            mMaxY = height/2;
            isDrawebled = drawebled;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mHexPaint.setColor(Color.BLACK);
            if (isDrawebled) {
                drawHexes();
            }
            for (Pt[] mHex : mHexs) {
                for (int i = 0; i < 7; i++) {
                    mHexPath.lineTo(mHex[i]._mX, mHex[i]._mY);
                    if (i == 6) {
                        canvas.drawPath(mHexPath, mHexPaint);
                    }
                }
                mHexPath.reset();
            }
            if (!mFirstSelectedHexPath.isEmpty() && !mSecondSelectedHexPath.isEmpty() && countPressed == 0 && !mPathPath.isEmpty()){
                canvas.drawPath(mPathPath, mPathPaint);
                canvas.drawPath(mFirstSelectedHexPath, mFirstSelectedHexPaint);
                canvas.drawPath(mSecondSelectedHexPath, mSecondHexSelectedPaint);
            } else if (!mFirstSelectedHexPath.isEmpty() && !mSecondSelectedHexPath.isEmpty() && countPressed == 0) {
                canvas.drawPath(mFirstSelectedHexPath, mFirstSelectedHexPaint);
                canvas.drawPath(mSecondSelectedHexPath, mSecondHexSelectedPaint);
            } else if (!mFirstSelectedHexPath.isEmpty() && countPressed == 1) {
                canvas.drawPath(mFirstSelectedHexPath, mFirstSelectedHexPaint);
            }
        }

        private void genFirstHex(){
            mHexs = new Pt[mNumberHex][7];
            mX = random.nextInt(mMaxX);
            mY = random.nextInt(mMaxY);
            mLeftX = mX -25;
            mRightX = mX +25;
            mSecondY = mY +25;
            mThirtyY = mY +50;
            mFortyY = mY +75;
            if (mY > mMinY && mRightX < mMaxX && mLeftX > mMinX && mFortyY < mMaxY) {
                mHexs[0][0] = new Pt(mX, mY);
                mHexs[0][1] = new Pt(mRightX, mSecondY);
                mHexs[0][2] = new Pt(mRightX, mThirtyY);
                mHexs[0][3] = new Pt(mX, mFortyY);
                mHexs[0][4] = new Pt(mLeftX, mThirtyY);
                mHexs[0][5] = new Pt(mLeftX, mSecondY);
                mHexs[0][6] = new Pt(mX, mY);
                mHexPath.moveTo(mHexs[0][0]._mX, mHexs[0][0]._mY);
            } else {
                genFirstHex();
            }
        }

        private void nextHex(){
            for (int i = 1; i < mNumberHex; i++) {
                int vershina = new Random().nextInt(5);
                if (vershina == 0) {
                    nextX = mRightX;
                    nextRightX = mRightX + 25;
                    nextLeftX = mX;
                    nextY = mY - 50;
                    nextSecondY = mY - 25;
                    nextThirtyY = mY;
                    nextFortyY = mSecondY;
                } else if (vershina == 1) {
                    nextX = mRightX + 25;
                    nextRightX = mRightX + 50;
                    nextLeftX = mRightX;
                    nextY = mY;
                    nextSecondY = mSecondY;
                    nextThirtyY = mThirtyY;
                    nextFortyY = mFortyY;
                } else if (vershina == 2) {
                    nextX = mRightX;
                    nextRightX = mRightX + 25;
                    nextLeftX = mX;
                    nextY = mThirtyY;
                    nextSecondY = mFortyY;
                    nextThirtyY = mFortyY + 25;
                    nextFortyY = mFortyY + 50;
                } else if (vershina == 3) {
                    nextX = mLeftX;
                    nextRightX = mX;
                    nextLeftX = mLeftX - 25;
                    nextY = mThirtyY;
                    nextSecondY = mFortyY;
                    nextThirtyY = mFortyY + 25;
                    nextFortyY = mFortyY + 50;
                } else if (vershina == 4) {
                    nextX = mLeftX - 25;
                    nextRightX = mLeftX;
                    nextLeftX = mLeftX - 50;
                    nextY = mY;
                    nextSecondY = mSecondY;
                    nextThirtyY = mThirtyY;
                    nextFortyY = mFortyY;
                } else if (vershina == 5) {
                    nextX = mLeftX;
                    nextRightX = mX;
                    nextLeftX = mLeftX - 25;
                    nextY = mY - 50;
                    nextSecondY = mY - 25;
                    nextThirtyY = mY;
                    nextFortyY = mSecondY;
                }
                if (nextY > mMinY && nextRightX < mMaxX && nextLeftX > mMinX && nextFortyY < mMaxY) {
                    mHexs[i][0] = new Pt(mX, mY);
                    mHexs[i][1] = new Pt(mRightX, mSecondY);
                    mHexs[i][2] = new Pt(mRightX, mThirtyY);
                    mHexs[i][3] = new Pt(mX, mFortyY);
                    mHexs[i][4] = new Pt(mLeftX, mThirtyY);
                    mHexs[i][5] = new Pt(mLeftX, mSecondY);
                    mHexs[i][6] = new Pt(mX, mY);
                    mX = nextX;
                    mLeftX = nextLeftX;
                    mRightX = nextRightX;
                    mY = nextY;
                    mSecondY = nextSecondY;
                    mThirtyY = nextThirtyY;
                    mFortyY = nextFortyY;
                } else {i--;}
            }
        }

        private void drawHexes(){
            genFirstHex();
            nextHex();
            createCenterHex(mHexs);
            isDrawebled = false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float pressedX = event.getX();
            float pressedY = event.getY();
            for (int i = 0; i < mHexs.length; i++) {
                if (mHexs[i][0]._mY < pressedY && pressedY < mHexs[i][3]._mY
                        && mHexs[i][1]._mX > pressedX && pressedX > mHexs[i][4]._mX) {
                    if (countPressed == 0) {
                        mFirstSelectedHexPaint = new Paint();
                        mFirstSelectedHexPath.reset();
                        mSecondSelectedHexPath.reset();
                        mPathPath.reset();
                        if (mUsedHexCenter != null) {
                            mUsedHexCenter.clear();
                        } else {
                            mUsedHexCenter = new ArrayList<>();
                        }
                        firstPressedX = mCentersHex.get(i)._mX;
                        firstPressedY = mCentersHex.get(i)._mY;
                        mCentersHex.get(i).setIsUsed(1);
                        mUsedHexCenter.add(mCentersHex.get(i));
                        mFirstHex = i;
                        mFirstSelectedHexPaint.setColor(Color.RED);
                        mPathPath.moveTo(mHexs[i][0]._mX, mHexs[i][0]._mY);
                        mFirstSelectedHexPath.moveTo(mHexs[i][0]._mX, mHexs[i][0]._mY);
                        for (int j = 0; j < 7; j++) {
                            mFirstSelectedHexPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                            mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                        }
                        countPressed = 1;
                        invalidate();
                        break;
                    } else if (countPressed == 1) {
                        secondPressedX = mCentersHex.get(i)._mX;
                        secondPressedY = mCentersHex.get(i)._mY;
                        mCentersHex.get(i).setIsUsed(1);
                        mSecondHex = i;
                        mSecondHexSelectedPaint.setColor(Color.GREEN);
                        mUsedHexCenter.add(mCentersHex.get(i));
                        mPathPath.lineTo(mHexs[i][0]._mX, mHexs[i][0]._mY);
                        mSecondSelectedHexPath.moveTo(mHexs[i][0]._mX, mHexs[i][0]._mY);
                        for (int j = 0; j < 7; j++) {
                            mSecondSelectedHexPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                            mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                        }
                        countPressed = 0;
                        findPath();
                        for (int k = 0; k < mCentersHex.size(); k++){
                            mCentersHex.get(k).setIsUsed(0);
                        }
                        invalidate();
                        break;
                    }
                }
            }
            return super.onTouchEvent(event);
        }

        private double rangeBetweenHex(float x1, float y1, float x2, float y2) {
            return Math.sqrt(((x1 - x2)*(x1 - x2))+((y1 - y2)*(y1 - y2)));
        }

        private void findPath() {
            float nextHexX;
            float nextHexY;
            mPathPath.moveTo(mHexs[mFirstHex][0]._mX, mHexs[mFirstHex][0]._mY);
            double rangeBetweenHex = rangeBetweenHex(firstPressedX, firstPressedY, secondPressedX, secondPressedY);
            if (rangeBetweenHex < 60){
                mUsedHexCenter.add(mCentersHex.get(mSecondHex));
                mPathPaint.setColor(Color.YELLOW);
                mPathPath.lineTo(mHexs[mSecondHex][0]._mX, mHexs[mSecondHex][0]._mY);
                return;
            } else {
                if (firstPressedX < secondPressedX && firstPressedY == secondPressedY) { //->
                    nextHexX = firstPressedX + 50;
                    nextHexY = firstPressedY;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX < secondPressedX && firstPressedY < secondPressedY) { //-> v
                    nextHexX = firstPressedX + 25;
                    nextHexY = firstPressedY + 50;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX < secondPressedX && firstPressedY > secondPressedY) { //-> ^
                    nextHexX = firstPressedX + 25;
                    nextHexY = firstPressedY - 50;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX > secondPressedX && firstPressedY == secondPressedY) { //<-
                    nextHexX = firstPressedX - 50;
                    nextHexY = firstPressedY;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX > secondPressedX && firstPressedY < secondPressedY) { //<- v
                    nextHexX = firstPressedX - 25;
                    nextHexY = firstPressedY + 50;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX > secondPressedX && firstPressedY > secondPressedY) { //<- ^
                    nextHexX = firstPressedX - 25;
                    nextHexY = firstPressedY - 50;
                    findNextHexInPath(nextHexX, nextHexY);
                } else if (firstPressedX == secondPressedX && firstPressedY > secondPressedY) { //^
                    nextHexX = firstPressedX + 25;
                    nextHexY = firstPressedY - 50;
                    int sizeUsed = mUsedHexCenter.size();
                    for (int i = 0; i < mCentersHex.size(); i++) {
                        if (mCentersHex.get(i)._mX == nextHexX && mCentersHex.get(i)._mY == nextHexY && mCentersHex.get(i).isUsed == 0) {
                            mCentersHex.get(i).setIsUsed(1);
                            mUsedHexCenter.add(mCentersHex.get(i));
                            for (int j = 0; j < 7; j++) {
                                mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                            }
                            nextHexX = nextHexX - 25;
                            nextHexY = nextHexY - 50;
                        }
                    }
                    if (mUsedHexCenter.size() == sizeUsed){
                        nextHexX = firstPressedX - 25;
                        nextHexY = firstPressedY - 50;
                        for (int i = 0; i < mCentersHex.size(); i++) {
                            if (mCentersHex.get(i)._mX == nextHexX && mCentersHex.get(i)._mY == nextHexY && mCentersHex.get(i).isUsed == 0) {
                                mCentersHex.get(i).setIsUsed(1);
                                mUsedHexCenter.add(mCentersHex.get(i));
                                for (int j = 0; j < 7; j++) {
                                    mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                                }
                                nextHexX = nextHexX + 25;
                                nextHexY = nextHexY - 50;
                            }
                        }
                    }
                    findNextHexInPath(nextHexX, nextHexY);
                } else if(firstPressedX == secondPressedX && firstPressedY < secondPressedY) { //v
                    int sizeUsed = mUsedHexCenter.size();
                    nextHexX = firstPressedX + 25;
                    nextHexY = firstPressedY + 50;
                    for (int i = 0; i < mCentersHex.size(); i++) {
                        if (mCentersHex.get(i)._mX == nextHexX && mCentersHex.get(i)._mY == nextHexY && mCentersHex.get(i).isUsed == 0) {
                            mCentersHex.get(i).setIsUsed(1);
                            mUsedHexCenter.add(mCentersHex.get(i));
                            for (int j = 0; j < 7; j++) {
                                mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                            }
                            nextHexX = nextHexX - 25;
                            nextHexY = nextHexY + 50;
                        }
                    }
                    if (mUsedHexCenter.size() == sizeUsed){
                        nextHexX = firstPressedX - 25;
                        nextHexY = firstPressedY + 50;
                        for (int i = 0; i < mCentersHex.size(); i++) {
                            if (mCentersHex.get(i)._mX == nextHexX && mCentersHex.get(i)._mY == nextHexY && mCentersHex.get(i).isUsed == 0) {
                                mCentersHex.get(i).setIsUsed(1);
                                mUsedHexCenter.add(mCentersHex.get(i));
                                for (int j = 0; j < 7; j++) {
                                    mPathPath.lineTo(mHexs[i][j]._mX, mHexs[i][j]._mY);
                                }
                                nextHexX = nextHexX + 25;
                                nextHexY = nextHexY - 50;
                            }
                        }
                    }
                    findNextHexInPath(nextHexX, nextHexY);
                } else {
                    for (int i = 0; i < mCentersHex.size(); i++) {
                        if (mCentersHex.get(i).isUsed == 0 && rangeBetweenHex(firstPressedX, firstPressedY, mCentersHex.get(i)._mX, mCentersHex.get(i)._mY) < 60) {
                            mCentersHex.get(i).setIsUsed(1);
                            mUsedHexCenter.add(mCentersHex.get(i));
                            mFirstHex = i;
                            for (int j = 0; j < 7; j++) {
                                mPathPath.lineTo(mHexs[mFirstHex][j]._mX, mHexs[mFirstHex][j]._mY);
                            }
                            findPath();
                        } else if (i == mCentersHex.size()-1) {
                            mCentersHex.get(mFirstHex).setIsUsed(2);
                            mUsedHexCenter.remove(mUsedHexCenter.size()-1);
                            break;
                        }
                    }
                }
            }
        }

        private void findNextHexInPath(float nextHexX, float nextHexY) {
            for (int i = 0; i < mCentersHex.size(); i++) {
                if (mCentersHex.get(i)._mX == nextHexX && mCentersHex.get(i)._mY == nextHexY && mCentersHex.get(i).isUsed == 0) {
                    mCentersHex.get(i).setIsUsed(1);
                    mUsedHexCenter.add(mCentersHex.get(i));
                    mFirstHex = i;
                    firstPressedX = nextHexX;
                    firstPressedY = nextHexY;
                    for (int j = 0; j < 7; j++) {
                        mPathPath.lineTo(mHexs[mFirstHex][j]._mX, mHexs[mFirstHex][j]._mY);
                    }
                    findPath();
                }
            }
        }
    }
    class Pt {
        float _mX, _mY;
        //0-not used 1-used 2-used but deadlock
        private int isUsed;

        Pt(float x, float y) {
            _mX = x;
            _mY = y;
        }

        Pt(float x, float y, int used){
            _mX = x;
            _mY = y;
            isUsed = used;
        }

        public void setIsUsed(int isUsed) {
            this.isUsed = isUsed;
        }
    }
}

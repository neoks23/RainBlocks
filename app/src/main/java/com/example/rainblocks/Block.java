package com.example.rainblocks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.rainblocks.GameView.screenRatioX;
import static com.example.rainblocks.GameView.screenRatioY;

public class Block {

    int x, y, width, height;
    Bitmap block;

    Block(Resources res){
        block = BitmapFactory.decodeResource(res, R.drawable.block);

        width = block.getWidth();
        height = block.getHeight();

        width = (int)(width * screenRatioX);
        height = (int)(height * screenRatioY);

        block = Bitmap.createScaledBitmap(block,width,height, false);
    }
    Rect getCollisionShape(){
        return new Rect(x,y,x + width, y + height);
    }
}

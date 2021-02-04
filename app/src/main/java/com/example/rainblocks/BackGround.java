package com.example.rainblocks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.rainblocks.R;

public class BackGround {
    int x = 0, y = 0;
    Bitmap backGround;

    BackGround(int screenX, int screenY, Resources res){
        backGround = BitmapFactory.decodeResource(res, R.drawable.b1);
        backGround = Bitmap.createScaledBitmap(backGround, screenX, screenY, false);
    }
}
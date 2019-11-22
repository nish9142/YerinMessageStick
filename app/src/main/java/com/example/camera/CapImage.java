package com.example.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CapImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capturedimage);


        Bundle b = getIntent().getExtras();
        byte[] img = b.getByteArray("image");
        if(img!=null){

            Bitmap image = BitmapFactory.decodeByteArray(img,0,img.length);
            ImageView capimg = findViewById(R.id.capimage);
            int w = image.getWidth();
            int h = image.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap rotatedimg = Bitmap.createBitmap(image,0,0,w,h,matrix,true);
            capimg.setImageBitmap(rotatedimg);

        }


    }
}

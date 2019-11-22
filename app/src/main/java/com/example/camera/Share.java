package com.example.camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.example.camera.Files.FilePaths;
import com.example.camera.Files.FileSearch;
import com.example.camera.Files.myGridView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Share extends AppCompatActivity implements SurfaceHolder.Callback {
    ViewPager viewPager;
    TabLayout tabLayout;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    Camera.PictureCallback jpegcallback;
    ImageView capture;
    private Spinner directorySpinner;
    RecyclerView gridView;
    GridviewAdapter gridviewAdapter;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private static final String TAG = "ShareActivity";
    ScrollView scrollView;

    private SlidingUpPanelLayout mLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


       //photo
        if(checkPermissionArray(Permissions.permissions))
        {
            setupCamera();
        }
        else {
            verifyPermission(Permissions.permissions);
        }

        jpegcallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Intent intent = new Intent(Share.this,CapImage.class);
                intent.putExtra("image",data);
                startActivity(intent);

            }
        };

        capture = findViewById(R.id.shutter);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        //gallery
//        scrollView= findViewById(R.id.scroll);
        mLayout = findViewById(R.id.sliding_layout);
        mLayout.setNestedScrollingEnabled(true);


        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(final View panel, float slideOffset) {
                Log.e(TAG, "onPanelSlide, offset " + slideOffset);
                Log.e(TAG, "onPanelSlide: hfbnvhiurbnvhuinviuhn"+mLayout.isNestedScrollingEnabled() );
                GridLayoutManager gridLayoutManager = (GridLayoutManager) gridView.getLayoutManager();
                if(slideOffset==1 && gridLayoutManager.findLastVisibleItemPosition()>14 )
                {

                    mLayout.setEnabled(false);
                }

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.e(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        ImageView galleryicon = findViewById(R.id.galleryic);
        directorySpinner=findViewById(R.id.spinner);
        galleryicon.setImageResource(R.drawable.ic_gallery);
        gridView = findViewById(R.id.grid_view);
        gridView.setNestedScrollingEnabled(false);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        init();

    }

    private void captureImage() {

        camera.takePicture(null,null,jpegcallback);
    }


    // camera Setup like open
    private void setupCamera() {

        surfaceView = findViewById(R.id.camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }


    // permissions

    private void verifyPermission(String[] permissions) {
        Log.e("verify ", "verifyPermission: ");

        ActivityCompat.requestPermissions(this,permissions,1);
    }

    private boolean checkPermissionArray(String[] permission) {

        for (int i=0;i<permission.length;i++){
            String singlep = permission[i];
            if(!checksinglep(singlep)){
                return false;
            }

        }

      return true;
    }

    private boolean checksinglep(String singlep) {

        int PermissionGranted = ActivityCompat.checkSelfPermission(this,singlep);
        if(PermissionGranted!= PackageManager.PERMISSION_GRANTED){
            return false;
        }
        else {
            return true;
        }

    }


    // setting camera as eyes for surface view

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters parameters =camera.getParameters();
        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        Camera.Size bestSize=null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        bestSize= sizeList.get(0);
        for (int i=0;i<sizeList.size();i++)
        {
            if(sizeList.get(i).width * sizeList.get(i).height > bestSize.width * bestSize.height)
            bestSize = sizeList.get(i);
        }

        parameters.setPictureSize(bestSize.width,bestSize.height);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
// gallery
    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            Log.e(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory){
        Log.e(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width

         gridView.setLayoutManager(new GridLayoutManager(this,3));

        //use the grid adapter to adapter the images to gridview
        gridviewAdapter = new GridviewAdapter(this,R.layout.layout,imgURLs, mAppend);
        gridView.setAdapter(gridviewAdapter);
        //set the first image to be displayed when the activity fragment view is inflate




    }


}




//gallery  methods



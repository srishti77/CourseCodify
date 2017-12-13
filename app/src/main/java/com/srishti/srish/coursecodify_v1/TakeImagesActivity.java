package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.text.format.Time;
public class TakeImagesActivity extends AppCompatActivity {

    private android.util.Size previewsize;
    private android.util.Size[] jpegSizes=null;

    private CameraDevice cameraDevice;

    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    CreateDirectories createMainFile = new CreateDirectories();
    private static final SparseIntArray ORIENTATIONS=new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    TextureView imageTextureView;
    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(TakeImagesActivity.this);

    //save byte image so that we can save the image when the event is created
     byte[] saveImageByte = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_images);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageTextureView = (TextureView) findViewById(R.id.imageTextureView);

        ImageButton buttonTakePicture = (ImageButton) findViewById(R.id.buttonTakePicture);


        imageTextureView.setSurfaceTextureListener(surfaceTextureListener);

        // to click the picture
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getPicture();
                // Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                //startActivity(intent);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getPicture(){

        if(cameraDevice==null)
        {
            return;
        }

        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            if(characteristics!=null)
            {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                Log.i("JpegSizes",jpegSizes+"");
            }

            //image size is too big
            int width=640,height=480;
            if(jpegSizes!=null && jpegSizes.length>0)
            {
                Log.i("jpegSizes Widrh",jpegSizes[0].getWidth()+"");
                Log.i("jpegSizes height",jpegSizes[0].getHeight()+"");
                width=jpegSizes[1].getWidth();
                height=jpegSizes[1].getHeight();
                if(width >1080)
                    width = 900;
                if(height > 1080)
                    height = 900;

            }
            ImageReader reader=ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces=new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(imageTextureView.getSurfaceTexture()));
            final CaptureRequest.Builder capturebuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation=getWindowManager().getDefaultDisplay().getRotation();
            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener imageAvailableListener=new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        saveImageByte = bytes;
                        save(bytes);
                    } catch (Exception ee) {
                        Log.i("Exception-gettingbytes","save error");
                        ee.printStackTrace();
                    } finally {
                        if (image != null)
                            image.close();
                    }
                }
                void save(final byte[] bytes)
                {
                    final File file12=getOutputMediaFile();
                    /*
                    If  there is no current event we direct them to the google calendar
                     */
                    setByteOfImage(bytes);

                    if(file12 == null) {
                        //                        //saveImageByte = bytes;
                        goTOCalendar();
                        Toast.makeText(getApplicationContext(), "You have to create an event to save the file", Toast.LENGTH_LONG).show();

                    }
                    else {
                       askConfirmationToSave(file12);
                    }
                }


            };

            HandlerThread handlerThread=new HandlerThread("takepicture");
            handlerThread.start();
            final Handler handler=new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener,handler);

            final CameraCaptureSession.CaptureCallback  previewSSession=new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try
                    {
                        session.capture(capturebuilder.build(),previewSSession,handler);
                    }catch (Exception e)
                    {
                        Log.i("Session Capture ", "Failed");
                        e.printStackTrace();

                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            },handler);




        }
        catch (Exception e){

            e.printStackTrace();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  void openCamera()
    {
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try
        {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewsize=map.getOutputSizes(SurfaceTexture.class)[0];
            Log.i("Preview Size Width", previewsize.getWidth()+"");
            Log.i("Preview Size Height", previewsize.getHeight()+"");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TakeImagesActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);
        }catch (Exception e)
        {
            Log.i("Error Msg", "openCamera Failed");
            e.printStackTrace();
        }
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            openCamera();



        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            startCamera();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
        }
        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice!=null)
        {
            cameraDevice.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void  startCamera()
    {
        if(cameraDevice==null||!imageTextureView.isAvailable()|| previewsize==null)
        {
            return;
        }
        SurfaceTexture texture = imageTextureView.getSurfaceTexture();
        if(texture==null)
        {
            return;
        }

        //texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
        Log.i("Preview Width", previewsize.getWidth()+"");
        Log.i("Preview Height", previewsize.getHeight()+"");


        texture.setDefaultBufferSize(900, 900);
        Surface surface=new Surface(texture);
        try
        {
            previewBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Log.i("PreviewBuilder","here it is");
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(TakeImagesActivity.this, "Preview Builder not working", Toast.LENGTH_SHORT).show();

        }
        previewBuilder.addTarget(surface);
        try
        {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession=session;
                    getChangedPreview();
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            },null);
        }catch (Exception e)
        {
            Log.i("after addTarget()", "failed");
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void getChangedPreview()
    {
        if(cameraDevice==null)
        {
            return;
        }
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);


        HandlerThread thread=new HandlerThread("changed Preview");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        try
        {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        }catch (Exception e){
            Log.i("SetRepeatingRequest","error");
            e.printStackTrace();
        }
    }


    private  File getOutputMediaFile() {
        File mediaStorageDir = null;
        if(getCalendarDetails.getCurrentEvent() == null){

           return mediaStorageDir;
        }
        else {
            Log.i("GetOutputMediaFile", createMainFile.createCourseCodifyFile());

             mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    createMainFile.createCourseCodifyFile() + "/" + getCalendarDetails.getCurrentEvent());
            if (!mediaStorageDir.exists()) {
                Log.i("Make ","Directory");
                mediaStorageDir.mkdirs();


            }


            return mediaStorageDir;
        }

    }

    public File getImageName(){
        File mediaStorageDir = getOutputMediaFile();
         String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

       File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        MediaScannerConnection.scanFile(TakeImagesActivity.this, new String[]{mediaFile.toString()}, null, null);

        return mediaFile;
    }

    public void goTOCalendar(){
       /* Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        Date currentDate = new Date();
        cal.add(Calendar.MONTH, 2);
        long time = cal.getTime().getTime();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        builder.appendPath(Long.toString(time));
        Intent intent = new Intent(Intent.ACT, builder.build());
        intent.setData(CalendarContract.Events.CONTENT_URI);
        Time currentTime = new Time();
        currentTime.setToNow();
        long dstart = currentTime.toMillis(false);

        currentTime.set(59, 59, 18, currentTime.monthDay, currentTime.month, currentTime.year );
        long dEnd = currentTime.toMillis(false);
        intent.putExtra(CalendarContract.Events.DTSTART, dstart);
        intent.putExtra(CalendarContract.Events.DTEND,  dEnd);
        intent.putExtra(CalendarContract.Events.TITLE, "MyNewEvent");
        startActivity(intent);
        */
       long startMills = System.currentTimeMillis();
       Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
       builder.appendPath("time");
       ContentUris.appendId(builder, startMills);
       Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
       startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(TakeImagesActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_list_of_images, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.home){
            this.finish();
            return true;
        }
        if(id == R.id.viewlistOfPicture){
            Intent intent = new Intent(TakeImagesActivity.this,  ViewListOfPicturesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setByteOfImage(byte[] imageByte){
        saveImageByte = imageByte;

    }

    public byte[] getByteOfImage() {
        return saveImageByte;
    }


    public void askConfirmationToSave( File file){

        AlertDialog.Builder builder = new AlertDialog.Builder(TakeImagesActivity.this);
        builder.setMessage("Your image will be saved in" + file.getName())
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("OnClick","Save is pressed");
                        //Log.i("OnClick",getByteOfImage().length+"");
                        OutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(getImageName());
                            outputStream.write(getByteOfImage());

                        } catch (Exception e) {
                            Log.i("Could not", "save");
                            e.printStackTrace();
                        } finally {
                            try {
                                if (outputStream != null)
                                    outputStream.close();
                            } catch (Exception e) {
                               Log.i("Could not close","outputstream");
                               e.printStackTrace();
                            }
                        }

                        Toast.makeText(getApplicationContext(), "Save Pressed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Create Event", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goTOCalendar();
                        Toast.makeText(getApplicationContext(), "Create Event Pressed", Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("On Resume", "called");

        File file;
        if(getByteOfImage() != null) {
            Log.i("getByteImage", "is not null");
            file = getOutputMediaFile();
            Log.i("FileName is", file.getParent());
            askConfirmationToSave(file);

        }

       // getPicture();
    }



}


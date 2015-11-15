package com.example.arun.myvideoapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Use semaphores to prevent app from closing without releasing camera???

public class MainActivity extends Activity {

    CameraManager cameraManager;
    CameraDevice cDevice;
    CameraCharacteristics cameraCharacteristics;
    SurfaceView texture;
    Surface surfacePreview;
    CameraCaptureSession cCaptureSession;
    MediaRecorder mediaRecorder;
    CaptureRequest.Builder previewRequestBuilder,recordRequestBuilder;
    File mFile;

    static boolean recFlag = false;

    public CameraDevice.StateCallback camStateCallback= new CameraDevice.StateCallback(){

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.

            cDevice = cameraDevice;
            Toast.makeText(getApplicationContext(), "Camera Opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
        }
    };

    private CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {

            cCaptureSession = cameraCaptureSession;
            Toast.makeText(getApplicationContext(), "CaptureSession linked!", Toast.LENGTH_SHORT).show();
/*
            try {
                //Setup the request
                //Setup builder basics
                previewRequestBuilder = cDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(surfacePreview);

                //Setup more builder stuff here
                // Auto-focus
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                // Auto-exposure
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                Toast.makeText(getApplicationContext(), "Making repeating request!", Toast.LENGTH_SHORT).show();
                cCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), captureCallback, null);
                //For some reason transitioning from a straight preview to a new one doesnt work well...use onReady for session?
            }
            catch(CameraAccessException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Camera Access Exception hit at onConfigured!!", Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Null Pointer Exception hit at onConfigured!", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong at onConfigured!", Toast.LENGTH_SHORT).show();
            }
            */
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession){
        }

        @Override
        public void onReady(CameraCaptureSession cameraCaptureSession){
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback(){
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result){
        }
    };

    public void stopCamera(View view){
        // Stops the camera capturing, and closes the device
        mediaRecorder.release();
        cCaptureSession.close();
        cDevice.close();
        Toast.makeText(getApplicationContext(), "Camera stopped!", Toast.LENGTH_SHORT).show();
    }

	public void setupRecorder(MediaRecorder recorder, File xFile, String fileName)
	{
        try{
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoSize(1280, 720);
            recorder.setVideoEncodingBitRate(10000000);
            recorder.setVideoFrameRate(30);
            recorder.setAudioEncodingBitRate(32000);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            xFile =  new File(getExternalFilesDir(null),fileName);
            recorder.setOutputFile(xFile.getAbsolutePath());
            recorder.prepare();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong when setting up recording!", Toast.LENGTH_SHORT).show();
        }
	}

    public void startCamera(View view) {
        try{
            //Get list of devices (we will want "0" on the Nexus5)
            /*
			String[] idList;
            idList= (String[]) cameraManager.getCameraIdList();

            //Get some info on the devices we see
            cameraCharacteristics = cameraManager.getCameraCharacteristics(idList[0]);

            if(idList.length>0) {
                for (int i = 0; i < idList.length; i++) {
                    Toast.makeText(getApplicationContext(), idList[i] + ": " + (i + 1) + " of " + idList.length, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), idList[i]+ ": " + cameraManager.getCameraCharacteristics(idList[i]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE).toString(), Toast.LENGTH_LONG).show();
                }
            }
            */

            //Surface stuff:
            // Preview surfaceTexture & mediaRecorder
            // Configure surfaces to the right camera sizes
            //Setup mediaRecorder surface to record video....s
            mediaRecorder = new MediaRecorder();
            setupRecorder(mediaRecorder, mFile, "myCrapVid.mp4");

            //Get stream configs for surfaces
            /*
			StreamConfigurationMap configs = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = configs.getOutputSizes(ImageFormat.JPEG);
            int sizeIndex = 8;
			*/
            //Iterate through the sizes on the phone
            //N5: size index 8 is 1280x720
            /*
            for(int i=5; i< sizes.length;i++){
                Toast.makeText(getApplicationContext(), i+ ": "+sizes[i].toString(), Toast.LENGTH_SHORT).show();
            }*/

            //Surface list
            List<Surface> surfaceList = new ArrayList<>();
            texture = (SurfaceView) findViewById(R.id.surfaceView);
            surfacePreview = texture.getHolder().getSurface();
            surfaceList.add(surfacePreview);
            surfaceList.add(mediaRecorder.getSurface());

            cDevice.createCaptureSession(surfaceList, captureStateCallback, null);
        }
        catch(CameraAccessException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Camera Access Exception hit when setting up recording!", Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Null Pointer Exception hit when setting up recording!", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong when setting up recording!", Toast.LENGTH_SHORT).show();
        }
    }

    public void recordVid(View view){
        if(cDevice == null) {
            Toast.makeText(getApplicationContext(), "Camera not ready yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            cCaptureSession.stopRepeating();
            if(!recFlag){
                //Not recording yet
                recordRequestBuilder = cDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                recordRequestBuilder.addTarget(surfacePreview);
                recordRequestBuilder.addTarget(mediaRecorder.getSurface());

                //Setup more builder stuff here
                // Auto-focus
                recordRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                // Auto-exposure
                recordRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                // Quality
                recordRequestBuilder.set(CaptureRequest.JPEG_QUALITY, Byte.decode("0x00000055") );
                recordRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);

                Toast.makeText(getApplicationContext(), "Making repeating request!", Toast.LENGTH_SHORT).show();
                cCaptureSession.setRepeatingRequest(recordRequestBuilder.build(), captureCallback, null);

                //Record!
                mediaRecorder.start();

                recFlag = true;
                Toast.makeText(getApplicationContext(), "Recording!", Toast.LENGTH_SHORT).show();
            }
            else if(recFlag){
                //Currently recording
                Toast.makeText(getApplicationContext(), "Stopping recording!", Toast.LENGTH_SHORT).show();
                mediaRecorder.stop();
                mediaRecorder.reset();

                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                recFlag = false;
            }
            else{
                Toast.makeText(getApplicationContext(), "Flag value not right...", Toast.LENGTH_SHORT).show();
            }
        }
        catch (RuntimeException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "RuntimeException hit while trying to record video!", Toast.LENGTH_SHORT).show();
            mFile.delete();
        }catch
                (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception hit while trying to record video!", Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public void onPause(){
        super.onPause();
    }*/

    /*@Override
    public void onResume(){
        super.onResume()
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            //Setup the camera manager
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            //Get list of devices (we will want "0" on the Nexus5)
            String[] idList;
            idList= (String[]) cameraManager.getCameraIdList();

            // Open the camera
            cameraManager.openCamera(idList[0], camStateCallback, null);
        }
        catch(CameraAccessException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Camera Access Exception hit when setting up camera!", Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Null Pointer Exception hit when setting up camera!", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong when setting up camera!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

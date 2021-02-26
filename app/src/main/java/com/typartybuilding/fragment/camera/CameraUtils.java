package com.typartybuilding.fragment.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.widget.Toast;

import com.typartybuilding.activity.plusRelatedActivity.Camera2Activity;
import com.typartybuilding.base.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangyt on 2019/4/28
 */
public class CameraUtils {

    private static String TAG = "CameraUtils";

    //private static CameraUtils ourInstance = new CameraUtils();

    //private static Context appContext;
    //private static CameraManager cameraManager;

    public static CameraManager getCameraManager(Context context) {

        CameraManager cameraManager;
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        return cameraManager;
    }


   /* public static CameraUtils getInstance() {
        return ourInstance;
    }*/

    private CameraUtils() {

    }



   /* public static String getFrontCameraId(){
        return getCameraId(true);
    }

    public static String getBackCameraId(){
        return getCameraId(false);
    }*/

    /**
     * 获取相机id
     * @param useFront 是否使用前置相机
     * @return
     */
    public static String getCameraId(boolean useFront,CameraManager cameraManager){
        try {
            for (String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (useFront){
                    if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT){
                        return cameraId;
                    }
                }else {
                    if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK){
                        return cameraId;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据输出类获取指定相机的输出尺寸列表，降序排序
     * @param cameraId 相机id
     * @param clz 输出类
     * @return
     */
    public static List<Size> getCameraOutputSizes(String cameraId, Class clz,CameraManager cameraManager){
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            List<Size> sizes = Arrays.asList(configs.getOutputSizes(clz));
            Collections.sort(sizes, new Comparator<Size>() {
                @Override
                public int compare(Size o1, Size o2) {
                    return o1.getWidth() * o1.getHeight() - o2.getWidth() * o2.getHeight();
                }
            });
            Collections.reverse(sizes);
            return sizes;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据输出格式获取指定相机的输出尺寸列表
     * @param cameraId 相机id
     * @param format 输出格式
     * @return
     */
    public static List<Size> getCameraOutputSizes(CameraManager cameraManager,String cameraId, int format){
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            return Arrays.asList(configs.getOutputSizes(format));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 释放相机资源
     * @param cameraDevice
     */
    public void releaseCameraDevice(CameraDevice cameraDevice){
        if (cameraDevice != null){
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    /**
     * 关闭相机会话
     * @param session
     */
    public void releaseCameraSession(CameraCaptureSession session){
        if (session != null){
            session.close();
            session = null;
        }
    }

    /**
     * 关闭 ImageReader
     * @param reader
     */
    public void releaseImageReader(ImageReader reader){
        if (reader != null){
            reader.close();
            reader = null;
        }
    }

    public static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    /**
     *  筛选 与屏幕 宽高比 相同的 摄像头的分辨率
     * @param cameraManager
     * @param mCameraId
     * @param width
     * @param heignt
     * @return
     */
    public static Size getMatchingSize(CameraManager cameraManager,String mCameraId,int width,int heignt){
        Size selectSize = null;
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            //保存，筛选出的可用 size
            List<Size> sizeList = new ArrayList<>();

            Log.e(TAG, "getMatchingSize: 屏幕宽度="+width);
            Log.e(TAG, "getMatchingSize: 屏幕高度="+heignt);
            float n = (float)heignt/width;
            Log.i(TAG, "getMatchingSize: n : " + n);

            //屏幕的高 宽比
            float aspectRatio = (float)(Math.round(n*10))/10;

            List<Float> ratioList = new ArrayList<>();  //保存所有宽高比率
            Log.i(TAG, "getMatchingSize: aspectRatio : " + aspectRatio);
            if (sizes != null) {
                for (int j = 0; j < sizes.length; j++) {
                    float h = (float)sizes[j].getHeight();
                    float w = (float)sizes[j].getWidth();
                    float ratio = (float)(Math.round((w/h)*10))/10;
                    ratioList.add(ratio);
                    Log.i(TAG, "getMatchingSize: h : " + h);
                    Log.i(TAG, "getMatchingSize: w : " + w);
                    Log.i(TAG, "getMatchingSize: ratio : " + ratio);
                    //保存筛选出的size
                    if (aspectRatio == ratio){
                        sizeList.add(sizes[j]);
                    }
                }
                if (sizeList.size() == 0){
                    //小于2的，选 16 ： 9
                    if (aspectRatio <= 2) {
                        selectSize = new Size(1280, 720);
                    }else if (aspectRatio > 2){
                        for (int i = 0; i < ratioList.size(); i++){
                            if (ratioList.get(i) == 2){
                                Log.i(TAG, "getMatchingSize: ratioList.get(i) == 2 : " + i);
                                selectSize = sizes[i];
                                Log.i(TAG, "getMatchingSize: sizes width : " + sizes[i].getWidth());
                                Log.i(TAG, "getMatchingSize: sizes height : " + sizes[i].getHeight());
                                break;
                            }
                        }
                    }
                    if (selectSize == null){
                        selectSize = new Size(1280, 720);
                    }


                }else if (sizeList.size() >= 1){
                    selectSize = sizeList.get(0);
                }

            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "getMatchingSize2: 选择的分辨率宽度="+selectSize.getWidth());
        Log.e(TAG, "getMatchingSize2: 选择的分辨率高度="+selectSize.getHeight());
        return selectSize;
    }


    public static Size getMatchingSize2(CameraManager cameraManager,String mCameraId){
        Size selectSize = null;
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);

            DisplayMetrics displayMetrics = MyApplication.getContext().getResources().getDisplayMetrics(); //因为我这里是将预览铺满屏幕,所以直接获取屏幕分辨率
            int deviceWidth = displayMetrics.widthPixels; //屏幕分辨率宽
            int deviceHeigh = displayMetrics.heightPixels; //屏幕分辨率高


            Log.e(TAG, "getMatchingSize2: 屏幕密度宽度="+deviceWidth);
            Log.e(TAG, "getMatchingSize2: 屏幕密度高度="+deviceHeigh );
            /**
             * 循环40次,让宽度范围从最小逐步增加,找到最符合屏幕宽度的分辨率,
             * 你要是不放心那就增加循环,肯定会找到一个分辨率,不会出现此方法返回一个null的Size的情况
             * ,但是循环越大后获取的分辨率就越不匹配
             */
            for (int j = 1; j < 41; j++) {
                for (int i = 0; i < sizes.length; i++) { //遍历所有Size
                    Size itemSize = sizes[i];
                    Log.e(TAG,"当前itemSize 宽="+itemSize.getWidth()+"高="+itemSize.getHeight());
                    //判断当前Size高度小于屏幕宽度+j*5  &&  判断当前Size高度大于屏幕宽度-j*5  &&  判断当前Size宽度小于当前屏幕高度
                    if (itemSize.getHeight() < (deviceWidth + j*5) && itemSize.getHeight() > (deviceWidth - j*5)) {
                        if (selectSize != null){ //如果之前已经找到一个匹配的宽度
                            if (Math.abs(deviceHeigh-itemSize.getWidth()) < Math.abs(deviceHeigh - selectSize.getWidth())){ //求绝对值算出最接近设备高度的尺寸
                                selectSize = itemSize;
                                continue;
                            }
                        }else {
                            selectSize = itemSize;
                        }

                    }
                }
                if (selectSize != null){ //如果不等于null 说明已经找到了 跳出循环
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "getMatchingSize2: 选择的分辨率宽度="+selectSize.getWidth());
        Log.e(TAG, "getMatchingSize2: 选择的分辨率高度="+selectSize.getHeight());
        return selectSize;
    }

    /**
     * 获取匹配的大小 这里是Camera2获取分辨率数组的方式,Camera1获取不同,计算一样
     * @return
     */
    private Size getMatchingSize(TextureView mPreviewView,String mCameraId,CameraManager mCameraManager){
        Size selectSize = null;
        float selectProportion = 0;
        try {
            float viewProportion = (float)mPreviewView.getWidth() / (float)mPreviewView.getHeight();//计算View的宽高比
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            for (int i = 0; i < sizes.length; i++){
                Size itemSize = sizes[i];
                float itemSizeProportion = (float)itemSize.getHeight() / (float)itemSize.getWidth();//计算当前分辨率的高宽比
                float differenceProportion = Math.abs(viewProportion - itemSizeProportion);//求绝对值
                Log.e(TAG, "相减差值比例="+differenceProportion );
                if (i == 0){
                    selectSize = itemSize;
                    selectProportion = differenceProportion;
                    continue;
                }
                if (differenceProportion <= selectProportion){ //判断差值是不是比之前的选择的差值更小
                    if (differenceProportion == selectProportion){ //如果差值与之前选择的差值一样
                        if (selectSize.getWidth() + selectSize.getHeight() < itemSize.getWidth() + itemSize.getHeight()){//选择分辨率更大的Size
                            selectSize = itemSize;
                            selectProportion = differenceProportion;
                        }

                    }else {
                        selectSize = itemSize;
                        selectProportion = differenceProportion;
                    }
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "getMatchingSize: 选择的比例是="+selectProportion);
        Log.e(TAG, "getMatchingSize: 选择的尺寸是 宽度="+selectSize.getWidth()+"高度="+selectSize.getHeight());
        return selectSize;
    }




  /*  private void setUpCameraOutputs(int width, int height)
    {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            // 获取指定摄像头的特性
            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(mCameraId);
            // 获取摄像头支持的配置属性
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 获取摄像头支持的最大尺寸
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Log.i(TAG, "setUpCameraOutputs: largest width: " + largest.getWidth() );
            Log.i(TAG, "setUpCameraOutputs: largest heigtt: " + largest.getHeight());
            // 创建一个ImageReader对象，用于获取摄像头的图像数据
            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(
                    new ImageReader.OnImageAvailableListener()
                    {
                        // 当照片数据可用时激发该方法
                        @Override
                        public void onImageAvailable(ImageReader reader)
                        {
                            // 获取捕获的照片数据
                            Image image = reader.acquireNextImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            // 使用IO流将照片写入指定文件
                            File file = new File(getExternalFilesDir(null), "pic.jpg");
                            buffer.get(bytes);
                            try (
                                    FileOutputStream output = new FileOutputStream(file))
                            {
                                output.write(bytes);
                                Toast.makeText(Camera2Activity.this, "保存: "
                                        + file, Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            finally
                            {
                                image.close();
                            }
                        }
                    }, null);
            // 获取最佳的预览尺寸
            previewSize = chooseOptimalSize(map.getOutputSizes(
                    SurfaceTexture.class), width, height, largest);
            // 根据选中的预览尺寸来调整预览组件（TextureView）的长宽比
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                mPreviewView.setAspectRatio(previewSize.getWidth(), previewSize.
                        getHeight());
            }
            else
            {
                mPreviewView.setAspectRatio(previewSize.getHeight(),
                        previewSize.getWidth());
            }
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            System.out.println("出现错误。");
        }
    }


    private static Size chooseOptimalSize(Size[] choices
            , int width, int height, Size aspectRatio)
    {

        Log.i(TAG, "chooseOptimalSize: choices size : " + choices.length);
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices)
        {
            Log.i(TAG, "chooseOptimalSize: width : " + option.getWidth());
            Log.i(TAG, "chooseOptimalSize: height : " + option.getHeight());
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height)
            {
                bigEnough.add(option);
                Log.i(TAG, "chooseOptimalSize: width : " + option.getWidth());
                Log.i(TAG, "chooseOptimalSize: height : " + option.getHeight());
                Log.i(TAG, "chooseOptimalSize: bigEnough size : " + bigEnough.size());
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最小的
        if (bigEnough.size() > 0)
        {
            return Collections.min(bigEnough, new CompareSizesByArea());
        }
        else
        {
            System.out.println("找不到合适的预览尺寸！！！");
            return choices[0];
        }
    }

    // 为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }*/



}

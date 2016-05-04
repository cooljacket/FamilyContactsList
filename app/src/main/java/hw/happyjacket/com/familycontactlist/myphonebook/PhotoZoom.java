package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-16.
 */
public class PhotoZoom {

    final static int aspectX = 1;
    final static int aspectY = 1;
    final static int outputX = 340;
    final static int outputY = 340;

    public static void startPhotoZoom(Activity context, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);
        context.startActivityForResult(intent, PhoneDictionary.RESULT_REQUEST_CODE);
    }

    public static Bitmap getImageToView(Activity context, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            return photo;
        }
        return null;
    }

    public static Bitmap createCircleImage(Bitmap source,int width,int height){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        /*
        产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /*
        首先绘制圆形
         */
        canvas.drawCircle(width/2,height/2,Math.min(width,height)/2,paint);
        /*
        使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /*
        绘制图片
         */
        canvas.drawBitmap(source,0,0,paint);

        return target;
    }


    public static Bitmap ratio(Activity context,int imgPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 4;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap ratio(Context context,int imgPath,int w, int h){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(context.getResources(),imgPath,options);
    }




    public static Bitmap getBitmap(int id,int photo,Bitmap [] picutres) {
        if(photo < 0) {
            File d = new File(Environment.getExternalStorageDirectory(), "image");
            File filename = new File(d, id + ".jpg");
            Bitmap bitmap1 = BitmapFactory.decodeFile(filename.getPath());
            bitmap1 = createCircleImage(bitmap1,bitmap1.getWidth(),bitmap1.getHeight());
            return bitmap1;
        }
        else{
            return picutres[photo];
        }
    }

    public static Bitmap getBitmap(String number,int photo,Bitmap [] picutres) {
        if(photo < 0) {
            File d = new File(Environment.getExternalStorageDirectory(), "image");
            File filename = new File(d, number + ".jpg");
            Bitmap bitmap1 = BitmapFactory.decodeFile(filename.getPath());
            bitmap1 = createCircleImage(bitmap1,bitmap1.getWidth(),bitmap1.getHeight());
            return bitmap1;
        }
        else{
            return picutres[photo];
        }
    }

    public static void saveBitmap(int id,Bitmap image){
        String path;
        File d = new File(Environment.getExternalStorageDirectory(),"image");
        if(!d.exists())
            d.mkdir();
        File location = new File(d,id + ".jpg");
        FileOutputStream fileOutputStream = null;

        try{
            if(location.exists()){
                location.delete();
            }
            location.createNewFile();
            fileOutputStream = new FileOutputStream(location);
            image.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.i("error","error");
            e.printStackTrace();
        }
    }

    public static void saveBitmap(String id,Bitmap image){
        String path;
        File d = new File(Environment.getExternalStorageDirectory(),"image");
        if(!d.exists())
            d.mkdir();
        File location = new File(d,id + ".jpg");
        FileOutputStream fileOutputStream = null;

        try{
            if(location.exists()){
                location.delete();
            }
            location.createNewFile();
            fileOutputStream = new FileOutputStream(location);
            image.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.i("error","error");
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(int id,int width,int height){
        String path;
        Bitmap bitmap = null;
        File f = new File(Environment.getExternalStorageDirectory(),"image");
        File inputStream = new File(f,id + ".jpg");
        path = inputStream.getPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inJustDecodeBounds = false;
        int sampleSize = 1;
        while(options.outHeight * options.outWidth / sampleSize >= width * height) sampleSize *= 2;
        options.inSampleSize = sampleSize;
        try{
            bitmap = BitmapFactory.decodeFile(path,options);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getBitmap(String  number,int width,int height){
        String path;
        Bitmap bitmap = null;
        File f = new File(Environment.getExternalStorageDirectory(),"image");
        File inputStream = new File(f,number + ".jpg");
        path = inputStream.getPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inJustDecodeBounds = false;
        int sampleSize = 1;
        while(options.outHeight * options.outWidth / sampleSize >= width * height) sampleSize *= 2;
        options.inSampleSize = sampleSize;
        try{
            bitmap = BitmapFactory.decodeFile(path,options);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}

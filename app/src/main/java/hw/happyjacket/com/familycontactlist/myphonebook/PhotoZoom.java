package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;

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

    public static Drawable getImageToView(Activity context, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            return  new BitmapDrawable(context.getResources(), photo);
        }
        return null;
    }

    public static Bitmap createCircleImage(Bitmap source,int min){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min,min, Bitmap.Config.ARGB_8888);
        /*
        产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /*
        首先绘制圆形
         */
        canvas.drawCircle(min/2,min/2,min/2,paint);
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


    public static Bitmap ratio(Activity context,int imgPath, float pixelW, float pixelH) {
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


}

package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by root on 16-4-17.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;

    private int [] image;

    final int param[] = {200,200};

    final int padding[] = {15,10,15,10};


    public ImageAdapter(Context context, int[] image) {
        this.context = context;
        this.image = image;
    }

    public  ImageAdapter(Context context){
        this.context = context;

    }
    @Override
    public int getCount(){
        return image.length;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return 0;

    }

    @Override
    public View getView(int positoon,View converView,ViewGroup parent){
        ImageView iv = new ImageView(context);
        iv.setImageResource(image[positoon]);
        iv.setLayoutParams(new Gallery.LayoutParams(param[0],param[1]));
        iv.setPadding(padding[0],padding[1],padding[2],padding[3]);

        return iv;

    }


}

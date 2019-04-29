package ir.ashkanabd.filelight.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
    private static final int IMAGE_COUNT = 500; // number of images adapter will show

    private final int[] mImageResIds = Constants.IMAGES;
    private final double[] mImageAspectRatios = new double[Constants.IMAGES.length];

    private Context mContext;

    @Override
    public double aspectRatioForIndex(int index) {
        // Precaution, have better handling for this in greedo-layout
        if (index >= getItemCount()) return 1.0;
        return mImageAspectRatios[getLoopedIndex(index)];
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    public PhotosAdapter(Context context) {
        mContext = context;
        calculateImageAspectRatios();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new PhotoViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mImageResIds[getLoopedIndex(position)])
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return Constants.IMAGES.length;
    }

    private void calculateImageAspectRatios() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        for (int i = 0; i < mImageResIds.length; i++) {
            BitmapFactory.decodeResource(mContext.getResources(), mImageResIds[i], options);
            mImageAspectRatios[i] = options.outWidth / (double) options.outHeight;
        }
    }

    // Index gets wrapped around <code>Constants.IMAGES.length</code> so we can loop content.
    private int getLoopedIndex(int index) {
        return index % Constants.IMAGES.length; // wrap around
    }
}
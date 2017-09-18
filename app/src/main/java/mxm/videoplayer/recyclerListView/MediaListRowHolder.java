package mxm.videoplayer.recyclerListView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mxm.videoplayer.R;


/**
 * Created by Mukesh on 12/20/2015.
 */
public class MediaListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail,options;
    protected TextView title;

    public MediaListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.options= (ImageView) view.findViewById(R.id.option);
    }
}

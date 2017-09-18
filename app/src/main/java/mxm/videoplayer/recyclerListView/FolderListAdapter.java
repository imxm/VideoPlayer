package mxm.videoplayer.recyclerListView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import mxm.videoplayer.ListFromFolder;
import mxm.videoplayer.MainActivity;
import mxm.videoplayer.R;

/**
 * Created by root on 9/12/17.
 */

public class FolderListAdapter  extends RecyclerView.Adapter<MediaListRowHolder>{

    private Context mContext;

    private List<File> itemList;


    private MainActivity activity;

    public FolderListAdapter(Context mContext, List<File> itemList, MainActivity activity) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.activity = activity;
    }

    @Override
    public MediaListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_folder_row_card_view, viewGroup,false);
        MediaListRowHolder mh = new MediaListRowHolder(v);


        return mh;
    }

    @Override
    public void onBindViewHolder(final MediaListRowHolder mediaListRowHolder, final int i) {
        final File item = itemList.get(i);
        try {

            mediaListRowHolder.title.setText(Html.fromHtml(item.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaListRowHolder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity,"clicked ho gya",Toast.LENGTH_SHORT).show();
            }
        });

        mediaListRowHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, ListFromFolder.class);
                intent.putExtra("title",item.getName());
                intent.putExtra("listingTrue",false);

                activity.startActivity(intent);

            }
;        });
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
}

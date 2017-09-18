package mxm.videoplayer.recyclerListView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;
import java.util.List;

import mxm.videoplayer.ListFromFolder;
import mxm.videoplayer.MediaFileInfo;
import mxm.videoplayer.R;

public class MediaRVAdapter extends RecyclerView.Adapter<MediaListRowHolder> {


    private List<MediaFileInfo> itemList;

    private Context mContext;
    private FloatingActionButton main_fab,mini_screen_fab,rotate_fab,fullscreen;
    private MediaController mediaController;
    private ListFromFolder activity;
    private VideoView floatingVideoView;
    private  RelativeLayout floatingLayout;
    LinearLayout listing;
    ImageView  home,listAll;
    int _xDelta,_yDelta;
    public MediaRVAdapter(Context context, List<MediaFileInfo> itemList, ListFromFolder activity) {
        this.itemList = itemList;
        this.mContext = context;
        this.activity=activity;
    }

    @Override
    public MediaListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_card_view,viewGroup,false);
        MediaListRowHolder mh = new MediaListRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(MediaListRowHolder mediaListRowHolder, final int i) {
        final MediaFileInfo item = itemList.get(i);
        try{

            mediaListRowHolder.title.setText(Html.fromHtml(item.getFileName()));
            Uri uri = Uri.fromFile(new File(item.getFilePath()));
            Log.d("========",item.getFileName());
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), item.getVideoiID(), MediaStore.Video.Thumbnails.MINI_KIND, options);
            mediaListRowHolder.thumbnail.setImageBitmap(curThumb);

        }catch (Exception e) {
            e.printStackTrace();
        }
        mediaListRowHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                final int   uiImmersiveOptions = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                final View decorView=activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(uiImmersiveOptions);
                activity.groot.setFitsSystemWindows(false);
                listing=(LinearLayout) activity.findViewById(R.id.listing);
                home=(ImageView) activity.findViewById(R.id.home_in_floder);
                listAll=(ImageView)activity.findViewById(R.id.list_in_floder);
                listing.setVisibility(View.GONE);

                floatingVideoView=(VideoView) activity.findViewById(R.id.floating_video_view);
                floatingLayout=(RelativeLayout)activity.findViewById(R.id.floating_layout);

                if(floatingVideoView.getVisibility()==View.VISIBLE)
                {
                    floatingVideoView.seekTo(floatingVideoView.getDuration());
                    floatingVideoView.setVisibility(View.GONE);
                    floatingLayout.setVisibility(View.GONE);
                }

                String path=item.getFilePath();
                final Uri uri=Uri.parse(path);

                //activity.getSupportActionBar().hide();

                main_fab=(FloatingActionButton) activity.findViewById(R.id.main_fab);
                mini_screen_fab=(FloatingActionButton) activity.findViewById(R.id.miniscreen_fab);
                rotate_fab=(FloatingActionButton) activity.findViewById(R.id.rotate_screen_fab);

                final VideoView videoView=(VideoView) activity.findViewById(R.id.video_view);
                final LinearLayout root= (LinearLayout) videoView.getParent();
                activity.parent.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(uri);
                videoView.start();

                mediaController=new MediaController(mContext);

                videoView.setMediaController(mediaController);
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.setVisibility(View.GONE);
                        activity.parent.setVisibility(View.GONE);
                        main_fab.setVisibility(View.GONE);
                        mini_screen_fab.setVisibility(View.GONE);
                        rotate_fab.setVisibility(View.GONE);
                        activity.recyclerView.setVisibility(View.VISIBLE);
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        activity.getSupportActionBar().show();
                        activity.groot.setFitsSystemWindows(true);
                    }
                });


                final int   uiImmersiveOptions2 = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
               final int prev= decorView.getSystemUiVisibility();;


                final boolean[] doubleTap = {false};
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mediaController.show();
                        activity.getSupportActionBar().hide();
                        main_fab.setVisibility(View.VISIBLE);
                        main_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mini_screen_fab.getVisibility()==View.VISIBLE)
                                {
                                    mini_screen_fab.setVisibility(View.GONE);
                                    rotate_fab.setVisibility(View.GONE);
                                }
                                else if(mini_screen_fab.getVisibility()==View.GONE)
                                {
                                    mini_screen_fab.setVisibility(View.VISIBLE);
                                    rotate_fab.setVisibility(View.VISIBLE);
                                }


                            }
                        });
                        rotate_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(decorView.getSystemUiVisibility()==uiImmersiveOptions) {

                                    decorView.setSystemUiVisibility(prev);
                                }
                                else
                                    decorView.setSystemUiVisibility(uiImmersiveOptions);

                            }
                        });
                        mini_screen_fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mini_screen_fab.setVisibility(View.GONE);
                                rotate_fab.setVisibility(View.GONE);
                                main_fab.setVisibility(View.GONE);
                                int seek=videoView.getCurrentPosition();
                                floatingLayout=(RelativeLayout) activity.findViewById(R.id.floating_layout);
                                root.setVisibility(View.GONE);
                                videoView.setVisibility(View.GONE);
                                decorView.setSystemUiVisibility(prev);
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                floatingLayout.setVisibility(View.VISIBLE);
                                floatingVideoView=(VideoView) activity.findViewById(R.id.floating_video_view);
                                floatingVideoView.setVisibility(View.VISIBLE);
                                listing.setVisibility(View.VISIBLE);
                                activity.recyclerView.setVisibility(View.VISIBLE);
                                fullscreen=(FloatingActionButton) activity.findViewById(R.id.fullscreen_fab);
                                fullscreen.setVisibility(View.VISIBLE);
                                final int   uiImmersiveOptions = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                );
                                final View decorView=activity.getWindow().getDecorView();
                                decorView.setSystemUiVisibility(uiImmersiveOptions);
                                activity.groot.setFitsSystemWindows(true);
                                activity.getSupportActionBar().show();

                                floatingVideoView.setVideoURI(uri);
                                floatingVideoView.seekTo(seek);
                                floatingVideoView.start();
                                fullscreen.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int seekback=floatingVideoView.getCurrentPosition();
                                        floatingLayout.setVisibility(View.GONE);
                                        floatingVideoView.setVisibility(View.GONE);

                                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                        root.setVisibility(View.VISIBLE);
                                        videoView.setVisibility(View.VISIBLE);
                                        videoView.setVideoURI(uri);
                                        videoView.seekTo(seekback);
                                        videoView.start();
                                    }
                                });
                                final float[] x = new float[1];
                                final float[] y = { 0.0f };
                                final boolean[] moving = {false};
                                //Drag
                                floatingLayout.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction())
                                        {
                                            case MotionEvent.ACTION_DOWN :
                                                moving[0] =true;
                                                break;
                                            case
                                            MotionEvent.ACTION_MOVE :
                                                if(moving[0])
                                                {
                                                    x[0] = event.getRawX() - floatingLayout.getWidth() / 2;
                                                    y[0] =event.getRawY()-floatingLayout.getHeight()/2;
                                                    floatingLayout.setX(x[0]);
                                                    floatingLayout.setY(y[0]);
                                                }
                                                break;
                                            case MotionEvent.ACTION_UP :
                                                moving[0]=false;
                                                break;
                                        }
                                        return true;
                                    }
                                });

                            }
                        });



                        return true;
                    }
                });


                RecyclerView recyclerView=(RecyclerView) activity.findViewById(R.id.recycler_view);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public void hideToolBr(){

        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);


         if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//END_INCLUDE (set_ui_flags)

    }
}
package mxm.videoplayer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import mxm.videoplayer.helperClass.Utils;
import mxm.videoplayer.helperClass.VideoData;
import mxm.videoplayer.recyclerListView.FolderListAdapter;
import mxm.videoplayer.recyclerListView.MediaRVAdapter;

public class ListFromFolder extends AppCompatActivity {
    public RecyclerView recyclerView;
    public MediaRVAdapter mediaRVAdapter;
    public FolderListAdapter folderListAdapter;
    public VideoView videoView;
    private Toolbar toolbar;
    private Menu menu;

    public LinearLayout parent;
    RelativeLayout root;
    String layout;
    String title;
   public View groot,decorView;
    RelativeLayout floatinglayout;
    public LinearLayout listing;
    VideoView floatingVideoView;
    FloatingActionButton main_fab,rotate_fab,mini_fab;

    public ArrayList<MediaFileInfo> mediaList;
    String type = "";
    boolean listAll;
    AlertDialog alertDialog1;
    CharSequence[] values = {" large "," Dark "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_list_from_folder);

        videoView = (VideoView) findViewById(R.id.video_view);
        parent = (LinearLayout) videoView.getParent();
        parent.setVisibility(View.GONE);
        root = (RelativeLayout) parent.getParent();
        floatinglayout=(RelativeLayout) findViewById(R.id.floating_layout);
        floatingVideoView=(VideoView) findViewById(R.id.floating_video_view) ;
        main_fab=(FloatingActionButton) findViewById(R.id.main_fab);
        mini_fab=(FloatingActionButton) findViewById(R.id.miniscreen_fab);
        rotate_fab=(FloatingActionButton) findViewById(R.id.rotate_screen_fab);
        groot = findViewById(R.id.rRoot);
        decorView = this.getWindow().getDecorView();

        title = getIntent().getStringExtra("title");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Videos");

        listing=(LinearLayout) findViewById(R.id.listing);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setClipChildren(true);

        type = "video";
        layout = "linear";
        listAll=false;

        changeLayout(layout);

        getSupportActionBar().setTitle(title);
        String[] args = new String[]{type, layout};

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.grid_view:
                String[] args = new String[2];
                args[0] = type;
                if (layout.equals("linear")) {
                    args[1] = "grid";
                    layout = "grid";
                    menu.findItem(R.id.grid_view).setTitle("change to list View");

                } else if (layout.equals("grid")) {
                    args[1] = "linear";
                    layout = "linear";
                    menu.findItem(R.id.grid_view).setTitle("change to grid View");
                }
                changeLayout(layout);
                break;
            case R.id.url:
                showChangeLangDialog();
                break;
            case R.id.all_videos :
                listAll=true;
                changeLayout(layout);
                break;
            case R.id.theme_changer:
                themeDialogue();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    public class MediaAsyncTask extends AsyncTask<String, Void, Integer> {

        private String layout;
        ProgressDialog pd = new ProgressDialog(ListFromFolder.this);

        @Override
        protected void onPreExecute() {
            //setProgressBarIndeterminateVisibility(true);

            pd.setMessage("loading");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            final String type = params[0];
            layout = params[1];
            try {


                if (type.equalsIgnoreCase("video")) {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] args = new String[]{type, layout};
                            if(!listAll)
                            parseAllVideo();
                            else
                                parseAllVideo(args);
                        }
                    });

                    result = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = 0;
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            setProgressBarIndeterminateVisibility(false);

            /* Download complete. Lets update UI */
            if (result == 1) {
                mediaRVAdapter = new MediaRVAdapter(ListFromFolder.this, mediaList, ListFromFolder.this);
                recyclerView.setAdapter(new MediaRVAdapter(ListFromFolder.this, mediaList, ListFromFolder.this));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (layout.equals("linear"))
                            recyclerView.setLayoutManager(new LinearLayoutManager(ListFromFolder.this));
                        if (layout.equals("grid"))
                            recyclerView.setLayoutManager(new GridLayoutManager(ListFromFolder.this, 2));
                        recyclerView.setAdapter(new MediaRVAdapter(ListFromFolder.this, mediaList, ListFromFolder.this));
                        mediaRVAdapter.notifyDataSetChanged();
                    }
                });


            } else {
                Log.e("failed", "Failed to fetch data!");
            }
        }
    }

    private void parseAllVideo() {
        try {

            ArrayList<MediaFileInfo> list = new VideoData(ListFromFolder.this).getListFromFolder(title);
            mediaList = list;
            Log.e("hdhsjdhs", mediaList.get(0).getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final String[] url = {""};

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Play Youtube Video");
        dialogBuilder.setMessage("Enter url below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                url[0] = edt.getText().toString();
                Intent intent = new Intent(ListFromFolder.this, YoutubePlayer.class);
                intent.putExtra("url", url[0]);
                startActivity(intent);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();


    }

    public void changeLayout(String layout) {
        if (layout.equals("linear")) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else
            recyclerView.setLayoutManager(new GridLayoutManager(ListFromFolder.this, 2));
        if(getIntent().getStringExtra("title").equals("AllVideos") || listAll) {
           getSupportActionBar().setTitle("All Videos");
            parseAllVideo(new String[]{"video", layout});
            Toast.makeText(this,getIntent().getStringExtra("title"),Toast.LENGTH_SHORT).show();
        }else
        parseAllVideo();
        recyclerView.setAdapter(new MediaRVAdapter(this, mediaList, this));
    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying() || floatingVideoView.isPlaying()) {
            videoView.seekTo(videoView.getDuration());
            parent.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            floatinglayout.setVisibility(View.GONE);
            floatingVideoView.seekTo(floatingVideoView.getDuration());
            floatingVideoView.setVisibility(View.GONE);
            mini_fab.setVisibility(View.GONE);
            rotate_fab.setVisibility(View.GONE);
            main_fab.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            listing.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            groot.setFitsSystemWindows(true);

            getSupportActionBar().show();
        }else
            finish();
    }

    private void parseAllVideo(String[] type) {
        try {
            String name = null;
            String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                    MediaStore.Video.Thumbnails.VIDEO_ID};

            int video_column_index;
            String[] proj = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE};
            Cursor videocursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
            int count = videocursor.getCount();
            Log.d("No of video", "" + count);
            mediaList=new ArrayList<>();

            for (int i = 0; i < count; i++) {
                MediaFileInfo mediaFileInfo = new MediaFileInfo();
                video_column_index = videocursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                videocursor.moveToPosition(i);
                name = videocursor.getString(video_column_index);



                int column_index = videocursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                videocursor.moveToPosition(i);
                String filepath = videocursor.getString(column_index);
                mediaFileInfo.setFileName(name);
                mediaFileInfo.setFilePath(filepath);
                mediaFileInfo.setFileType(type[0]);
                mediaFileInfo.setVideoiID(videocursor.getInt(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                mediaList.add(mediaFileInfo);
                String parent=new File(filepath).getParent();
                String folerPath=new File(parent).getAbsolutePath();
                // mediaFileInfo.setFileName(parent);
                // mediaFileInfo.setFilePath(folerPath);


                // id += " Size(KB):" +
                // videocursor.getString(video_column_index);


            }
            videocursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void themeDialogue(){


        AlertDialog.Builder builder = new AlertDialog.Builder(ListFromFolder.this);

        builder.setTitle("Select Your Theme");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        Utils.changeToTheme(ListFromFolder.this,Utils.THEME_BLUE);

                        //Toast.makeText(MainActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Utils.changeToTheme(ListFromFolder.this,Utils.THEME_WHITE);
                        //Toast.makeText(MainActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 2:

                        Toast.makeText(ListFromFolder.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }


}
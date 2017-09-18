package mxm.videoplayer;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mxm.videoplayer.helperClass.Utils;
import mxm.videoplayer.helperClass.VideoData;
import mxm.videoplayer.recyclerListView.FolderListAdapter;
import mxm.videoplayer.recyclerListView.MediaRVAdapter;

/**
 * Created by Mukesh on 12/20/2015.
 */

public class MainActivity extends AppCompatActivity {


    public RecyclerView recyclerView;
    public MediaRVAdapter mediaRVAdapter;
    public FolderListAdapter folderListAdapter;
    public VideoView videoView;
    private Toolbar toolbar;
    private Menu menu;
    String layout; //to toogle b/w grid and linear layout
    Set<String> directory;
    LinearLayout parent;
    RelativeLayout root;
    public boolean folder; //for folder lyaout
    boolean listAll;
    public ImageView listAllVideo;

    private Cursor cursor;
    private int columnIndex;
    private static final String TAG = "RecyclerViewExample";

    public ArrayList<MediaFileInfo> mediaList ;
    private List<File> folderList=new ArrayList<>();
    String type = "";
    AlertDialog alertDialog1;
    CharSequence[] values = {" large "," Dark "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Allow activity to show indeterminate progressbar */
       requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        Utils.onActivityCreateSetTheme(this);

        setContentView(R.layout.activity_main);
        folder=true;




        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Directories");
      //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreenPrimary)));

        listAllVideo=(ImageView) findViewById(R.id.list);
        listAllVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ListFromFolder.class);
                intent.putExtra("title","AllVideos");
                startActivity(intent);
            }
        });


        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        type = "video";
        layout="linear";
        listAll=false;
        String[] args=new String[] {type,layout};
        new MediaAsyncTask().execute(args);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        this.menu=menu;
        menu.findItem(R.id.all_videos).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.grid_view :
                String[] args=new String[2];
                args[0]=type;
                if(layout.equals("linear")){
                    args[1]="grid";
                    layout="grid";
                    menu.findItem(R.id.grid_view).setTitle("change to list View");

                }
                else if(layout.equals("grid")){
                    args[1]="linear";
                    layout="linear";
                    menu.findItem(R.id.grid_view).setTitle("change to grid View");
                }
                new MediaAsyncTask().execute(args);
                break;
            case R.id.url :
                 showChangeLangDialog();
                break;
            case R.id.theme_changer:
                themeDialogue();
                break;



        }
        return super.onOptionsItemSelected(item);
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
    public  void parseallvidoes()
    {
        folderList=new VideoData(MainActivity.this).getDirectory();
        folderListAdapter=new FolderListAdapter(MainActivity.this,folderList,MainActivity.this);

    }


    public class MediaAsyncTask extends AsyncTask<String, Void, Integer> {

        private String layout;
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

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
           layout=params[1];
            try {
                mediaList = new ArrayList<>();
                if(type.equalsIgnoreCase("video")) {
                        //parseallvidoes();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] args= new String[]{type, layout};
                            if(!listAll)
                            parseallvidoes();
                            else
                                parseAllVideo(args);
                        }
                    });

                    result =1;
                }
            }catch (Exception e) {
                e.printStackTrace();
                result =0;
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

         setProgressBarIndeterminateVisibility(false);
            pd.dismiss();


            /* Download complete. Lets update UI */
            if (result == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (layout.equals("linear"))
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        if(layout.equals("grid"))
                            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
                        recyclerView.setAdapter(new FolderListAdapter(MainActivity.this,folderList,MainActivity.this));

                    }
                });


            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        }
    }

    @Override
    public void onBackPressed() {

       super.onBackPressed();

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
                url[0] =edt.getText().toString();
                Intent intent = new Intent(MainActivity.this, YoutubePlayer.class);
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
    public void themeDialogue(){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Select Your Theme");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        Utils.changeToTheme(MainActivity.this,Utils.THEME_BLUE);

                        //Toast.makeText(MainActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Utils.changeToTheme(MainActivity.this,Utils.THEME_WHITE);
                        //Toast.makeText(MainActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 2:

                        Toast.makeText(MainActivity.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }
}
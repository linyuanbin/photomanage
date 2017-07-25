package com.ghgk.photomanage.tap_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.ghgk.photomanage.MainActivity;
import com.ghgk.photomanage.R;
import com.ghgk.photomanage.model.UploadGoodsBean;
import com.ghgk.photomanage.util.Config;
import com.ghgk.photomanage.util.DbTOPxUtil;
import com.ghgk.photomanage.util.UploadUtil;
import com.ghgk.photomanage.view.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zzti.fengyongge.imagepicker.PhotoPreviewActivity;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import com.zzti.fengyongge.imagepicker.util.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cool on 2017-03-28.
 */
public class fragment_history extends Fragment {
    private static LayoutInflater inflater1;
    private ImageView add_IB;
    private MyGridView my_imgs_GV;
    private int screen_widthOffset;
    private ArrayList<UploadGoodsBean> img_uri = new ArrayList<UploadGoodsBean>();
    private List<PhotoModel>  single_photos = new ArrayList<PhotoModel>();
    GridImgAdapter gridImgsAdapter;
    private View view;
     private Button commit;
    private Map<String,String> photopaths=new HashMap<String,String>();
    private  String[] photopaths1=new String[20];
    private int photonumbers=0;
    private  int p=0;
    private  int photostart=0;
    private  String stringbutter="";
     // private String url1 = "http://192.168.0.18:8080/TotemDown/managerServer?username=linyuanbin&password=123456";
    // private String url2 ="http://192.168.0.18:8080/TotemDown/UploadShipServlet?username=linyuanbin&password=123456";
   // private String url1 = "http://114.115.210.8:8090/TotemDown/managerServer?username=linyuanbin&password=123456";
   private String url2 ="http://114.115.210.8:8090/TotemDown/UploadShipServlet?username=linyuanbin&password=123456";
    private  String photos;
    private byte[] ss;
    String result="";
    OkHttpClient okHttpClient = new OkHttpClient();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        view=inflater.inflate(R.layout.fragment_history, group, false);
         DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity().getApplicationContext()).defaultDisplayImageOptions(
                defaultOptions).build();
        ImageLoader.getInstance().init(config);
        Config.ScreenMap = Config.getScreenSize(getActivity(), getActivity());
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screen_widthOffset = (display.getWidth() - (3* DbTOPxUtil.dip2px(getActivity(), 2)))/4;
        inflater1 = LayoutInflater.from(getActivity());
        Log.i("sfsfs------","----------"+inflater);
        //inflater1=inflater;
        //my_imgs_GV = (MyGridView) getActivity().findViewById(R.id.my_goods_GV);//null
        my_imgs_GV= (MyGridView) view.findViewById(R.id.my_goods_GV);
        gridImgsAdapter = new GridImgAdapter();
        my_imgs_GV.setAdapter(gridImgsAdapter);
        img_uri.add(null);
        gridImgsAdapter.notifyDataSetChanged();
        // setCommit();
        return view;
    }


    class GridImgAdapter extends BaseAdapter implements ListAdapter {
        @Override
        public int getCount() {
            return img_uri.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater1.inflate(R.layout.photo_activity_addstory_img_item, null);
            add_IB = (ImageView) convertView.findViewById(R.id.add_IB);
            //button
            commit=(Button)getActivity().findViewById(R.id.commit);
            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    for(Map.Entry<String, String> entry:photopaths.entrySet()){
//                        new MyAsyncTask().execute(entry.getKey(),url2);
//                    }
                    for (int i=0; i<photonumbers; i++){
                      //聪哥
                            new MyAsyncTask().execute(photopaths1[i],url2);
                        }
                    img_uri.clear();
                    img_uri.add(null);
                    single_photos.clear();
                    gridImgsAdapter.notifyDataSetChanged();
                    photonumbers=0;
                }
                });



            ImageView delete_IV = (ImageView) convertView.findViewById(R.id.delete_IV);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(screen_widthOffset, screen_widthOffset);
            convertView.setLayoutParams(param);
            if (img_uri.get(position) == null) {
                delete_IV.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.iv_add_the_pic, add_IB);
                add_IB.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        //问题之处
                        Log.i("--------","----onclick");
                        Intent intent = new Intent(getActivity(), PhotoSelectorActivity.class);
                        //界面跳转的动画
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("limit", 18 - (img_uri.size() - 1));
                        startActivityForResult(intent, 0);
                    }
                });

            }
            else {
                ImageLoader.getInstance().displayImage("file://" + img_uri.get(position).getUrl(), add_IB);
                delete_IV.setOnClickListener(new View.OnClickListener() {
                    private boolean is_addNull;
                    @Override
                    public void onClick(View arg0) {
                        is_addNull = true;
                        String img_url = img_uri.remove(position).getUrl();
                              single_photos.remove(position);
                        for (int i = 0; i < img_uri.size(); i++) {
                            if (img_uri.get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            img_uri.add(null);
                        }
                        gridImgsAdapter.notifyDataSetChanged();
                    }
                });

                add_IB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("photos",(Serializable)single_photos);
                        bundle.putInt("position", position);
                        bundle.putString("save","save");
                        CommonUtils.launchActivity(getActivity(), PhotoPreviewActivity.class, bundle);
                    }
                });

            }
            return convertView;
        }
        class ViewHolder {
            ImageView add_IB;
            ImageView delete_IV;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    //path 图片地址
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
                    //转换成bitmap
                     if (img_uri.size() > 0) {
                        img_uri.remove(img_uri.size() - 1);
                    }
                    System.out.println("1111ssscscscscs"+paths.size()+"");
                    for (int i = 0; i < paths.size(); i++) {
//                        File file =
//                        bitmap 图片转化二进制
                     // Bitmap bi
                        // tmap= BitmapFactory.decodeFile(paths.get(i));
                        int po=0;
                        for(Map.Entry<String, String> entry:photopaths.entrySet()){
                          if(!(entry.getKey().toString().equals(paths.get(i).toString()))){
                              String a=entry.getKey().toString();
                              String b=paths.get(i).toString();
                              ++po;
                          }

                            Log.i("pppppppptooooo",entry.getKey() +photopaths.size()); }

                          if(po==photopaths.size()) {
                            img_uri.add(new UploadGoodsBean(paths.get(i), false));
                            String datas = paths.get(i);
                            photopaths.put(datas.trim(), ss+ "");
                            photopaths1[i]=datas;
                              photonumbers++;
                        }
                    }
                        //--------------------------------------------
                   //上传参数
                    for (int i = 0; i < paths.size(); i++) {
                            PhotoModel photoModel = new PhotoModel();
                            photoModel.setOriginalPath(paths.get(i));
                            photoModel.setChecked(true);
                           // Log.i("pathpppppppppp", photopaths[i].toString());
                            Log.i("pathppppp", paths.get(i).toString());
                            //String datas=bitmap2Bytes(photoModel);
                            //photopaths[i]=datas;
                            single_photos.add(photoModel);

                    }
                    if (img_uri.size() < 18) {
                        img_uri.add(null);
                    }
                    gridImgsAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
//    public void setCommit(){
//           commit=(Button)getActivity().findViewById(R.id.commit);
//                commit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(),"kkkk",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
            public String bitmap2Bytes(Bitmap bm) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
             byte[] byteArray=baos.toByteArray();
             String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                return imageString;
         }
   //图片上传
   public void photosupload(){


       //请求
       //登陆的访问
//       String aco = account.getText().toString();
//       String pass = password.getText().toString();
//       String post ="{\"state\":\"login\",\"mName\":\""+aco+"\",\"mPassword\":\""+pass+"\"}";
//       RequestBody requestBody1 = RequestBody
//               .create(MediaType.parse("text/x-markdown; charset=utf-8"),post);
//       Request.Builder builder3 = new Request.Builder();
//       Request request2 = builder3
//               .url(url1)
//               .post(requestBody1)
//               .build();
//       CallHttp(request2);
//       Log.i("info", "post = " + post);

      //while (!photopaths[photostart].equals("")&&p<=photopaths.length){

    //   while (!photopaths[photostart].equals("")){

      // String post ="{\"state\":\"login\",\"mName\":\""+aco+"\",\"mPassword\":\""+pass+"\"}";

          //   String photospost;

           //"{\"photopath+p\":\"photopaths[photostart]\"}"


          //   photostart++;

 //}

 }

    public void CallHttp(Request request)

    {
        okhttp3.Call call1 = okHttpClient.newCall(request);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info", " GET请求失败！！！");
                Log.i("info", " e  = "+ e .toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String res = response.body().string();
                Log.i("info", " GET请求成功！！！");
                Log.i("info", " GET请求成功！！！"+res);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Gson gson = new Gson();
//                        Manager user = gson.fromJson(res, Manager.class);
////                        user.getState();
//                        if ( user.getState().equals("true"))
//                        {
////                            Log.i("info", "res  = " + res.toString());
//                            Toast.makeText(getActivity(), "登录成功！！", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(getActivity(), MainActivity_2.class);
//                            startActivity(intent1);
//                            getActivity().finish();
//                        }//得到的res为用户ID  保存到本地
//                        else
//                            Toast.makeText(getActivity(), "登录失败了哦！！！", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }

    public byte[] image2byte(String path){
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead =0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            String s=new String(data,"utf-8");
            Log.i("data-------------",s);
            output.close();
            input.close();

        }
        catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }
    public void photoupload(){


        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<photonumbers;i++){
            String aa="";


        }

        //String po ="{\"state\":\"upload\",\"files\":["+stringBuilder.toString()+"]}".trim();
        String po ="{\"state\":\"upload\",\"files\":["+stringBuilder.toString()+"]}".trim();
        Log.i("stringBuilder----111",po.toString());
        RequestBody requestBody1 = RequestBody
                .create(MediaType.parse("text/x-markdown; charset=utf-8"),po.trim());
        Request.Builder builder3 = new Request.Builder();
        Request request2 = builder3
                .url(MainActivity.ur)
                .post(requestBody1)
                .build();
        CallHttp(request2);

    }
///聪哥
    class MyAsyncTask extends AsyncTask<String,Void,String>{
    @Override
        protected String doInBackground(String... strings) {
            try {
                File file=new File(strings[0]);
                return UploadUtil.uploadFile(file,strings[1]);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return "failed";
        }


    @Override
        protected void onPostExecute(String s) {
         //   Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
        }
    }
}



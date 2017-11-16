package com.example.readfiledemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadFileActivity extends Activity implements OnItemClickListener {

    private ListView mListView;
    private TextView mPathView;
    private FileListAdapter mFileAdpter;
    private TextView mItemCount;
    private Button mButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(ReadFileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(ReadFileActivity.this,permissions,1);
        }else{
            initView();
        }

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.file_list);
        mPathView = (TextView) findViewById(R.id.path);
        mItemCount = (TextView) findViewById(R.id.item_count);
        mListView.setOnItemClickListener(this);
        String apkRoot = "chmod 777 " + getPackageCodePath();
        //RootCommand(apkRoot);
        File SDPath= Environment.getExternalStorageDirectory();
        //File folder = new File("/storage");
        initData(SDPath);
    }


    private void initData(File folder) {
        boolean isRoot = folder.getParent() == null;
        mPathView.setText(folder.getAbsolutePath());
        ArrayList<File> files = new ArrayList<File>();
        if (!isRoot) {
            files.add(folder.getParentFile());
        }
        File[] filterFiles = folder.listFiles();
        mItemCount.setText(filterFiles.length + "项");
        if(null != filterFiles && filterFiles.length > 0) {
            for (File file : filterFiles) {
                files.add(file);
            }
        }
        mFileAdpter = new FileListAdapter(this, files, isRoot);
        mListView.setAdapter(mFileAdpter);
    }

    private class FileListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<File> files;
        private boolean isRoot;
        private LayoutInflater mInflater;

        public FileListAdapter (Context context, ArrayList<File> files, boolean isRoot) {
            this.context = context;
            this.files = files;
            this.isRoot = isRoot;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount () {
            return files.size();
        }

        @Override
        public Object getItem (int position) {
            return files.get(position);
        }

        @Override
        public long getItemId (int position) {
            return position;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.file_list_item, null);
                convertView.setTag(viewHolder);
                viewHolder.title = (TextView) convertView.findViewById(R.id.file_title);
                viewHolder.type = (TextView) convertView.findViewById(R.id.file_type);
                viewHolder.data = (TextView) convertView.findViewById(R.id.file_date);
                viewHolder.size = (TextView) convertView.findViewById(R.id.file_size);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            File file = (File) getItem(position);
            if(position == 0 && !isRoot) {
                viewHolder.title.setText("返回上一级");
                viewHolder.data.setVisibility(View.GONE);
                viewHolder.size.setVisibility(View.GONE);
                viewHolder.type.setVisibility(View.GONE);
            } else {
                String fileName = file.getName();
                viewHolder.title.setText(fileName);
                if(file.isDirectory()) {
                    viewHolder.size.setText("文件夹");
                    viewHolder.size.setTextColor(Color.RED);
                    viewHolder.type.setVisibility(View.GONE);
                    viewHolder.data.setVisibility(View.GONE);
                } else {
                    long fileSize = file.length();
                    if(fileSize > 1024*1024) {
                        float size = fileSize /(1024f*1024f);
                        viewHolder.size.setText(new DecimalFormat("#.00").format(size) + "MB");
                    } else if(fileSize >= 1024) {
                        float size = fileSize/1024;
                        viewHolder.size.setText(new DecimalFormat("#.00").format(size) + "KB");
                    } else {
                        viewHolder.size.setText(fileSize + "B");
                    }

                }
            }
            return convertView;
        }

        class ViewHolder {
            private TextView title;
            private TextView type;
            private TextView data;
            private TextView size;
        }
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        File file = (File) mFileAdpter.getItem(position);
        if(!file.canRead()) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("权限不足").setPositiveButton(android.R.string.ok, new OnClickListener() {

                @Override
                public void onClick (DialogInterface dialog, int which) {

                }
            }).show();
        } else if(file.isDirectory()) {
            initData(file);
        } else {
            Toast.makeText(this,file.getName(),Toast.LENGTH_SHORT).show();
        }
        // openFile(file);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults)
                    {
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意全部权限",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    initView(); //调出文件管理器
                }else{
                    Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}





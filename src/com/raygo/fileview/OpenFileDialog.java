package com.raygo.fileview;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;

public class OpenFileDialog{

	static final public String sRoot = "/";
	static final public String sParent = "..";
	static final public String sFolder = ".";
	static final public String sEmpty = "";
	
	private String path = "/mnt";
	private List<Map<String, Object>> list = null;
	private String suffix = null;
	private Context context = null;
	// 定义浮动窗口布局
	private LinearLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
	private WindowManager mWindowManager;
	private ListView mFileView;
	private CallbackBundle callback;
	private Map<String, Integer> images =null;
	
	
	OpenFileDialog(Context contexts, String suffixs, CallbackBundle callbacks){
		context = contexts;
		suffix = (suffixs == null ? "" : suffixs.toLowerCase());
		callback = callbacks;
		
		images = new HashMap<String, Integer>();
		// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
		images.put(OpenFileDialog.sRoot, R.drawable.rooticon);	// 根目录图标
		images.put(OpenFileDialog.sParent, R.drawable.shangicon);	//返回上一层的图标
		images.put(OpenFileDialog.sFolder, R.drawable.foldericon);	//文件夹图标
		images.put("png", R.drawable.imgicon);	//png文件图标
		images.put("jpg", R.drawable.imgicon);	//jpg文件图标
		images.put(OpenFileDialog.sEmpty, R.drawable.rooticon);

		//获取的是LocalWindowManager对象  
        mWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        //获取LayoutParams对象  
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG ;  
        wmParams.format = PixelFormat.RGBA_8888;;  
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;// | LayoutParams.FLAG_NOT_FOCUSABLE;  
        wmParams.gravity = Gravity.CENTER | Gravity.TOP;  
        wmParams.x = 0;  
        wmParams.y = 0;  
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
               
		
	}
	
	//Show Dialog
	public void ShowDialog()  
    {       
		LayoutInflater inflater = LayoutInflater.from(context);
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), 
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// 浮动窗口列表
		mFileView = (ListView) mFloatLayout.findViewById(R.id.listViewFile);
		mFileView.setOnItemClickListener(lstner);
		refreshFileList();
	}
	
	private OnItemClickListener lstner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			// 条目选择
			String pt = (String) list.get(position).get("path");
			String fn = (String) list.get(position).get("name");
			if (fn.equals(sRoot) || fn.equals(sParent)) {
				// 如果是更目录或者上一层
				File fl = new File(pt);
				String ppt = fl.getParent();
				if (ppt != null) {
					// 返回上一层
					path = ppt;
				} else {
					// 返回更目录
					path = sRoot;
				}
			} else {
				File fl = new File(pt);
				if (fl.isFile()) {
					// 如果是文件
					Bundle bundle = new Bundle();
					bundle.putString("path", pt);
					bundle.putString("name", fn);
					// 调用事先设置的回调函数
					callback.callback(bundle);

					if(mFloatLayout != null)  
			        {  
			            //移除悬浮窗口  
			            mWindowManager.removeView(mFloatLayout);  
			        }
					return;
				} else if (fl.isDirectory()) {
					// 如果是文件夹
					// 那么进入选中的文件夹
					path = pt;
				}
			}
			refreshFileList();
		}

	};

	private String getSuffix(String filename) {
		int dix = filename.lastIndexOf('.');
		if (dix < 0) {
			return "";
		} else {
			return filename.substring(dix + 1);
		}
	}

	private int getImageId(String s) {
		if (images == null) {
			return 0;
		} else if (images.containsKey(s)) {
			return images.get(s);
		} else if (images.containsKey(sEmpty)) {
			return images.get(sEmpty);
		} else {
			return 0;
		}
	}
	
	private int refreshFileList() {
		// 刷新文件列表
		File[] files = null;
		try {
			files = new File(path).listFiles();
		} catch (Exception e) {
			files = null;
		}
		if (files == null) {
			// 访问出错
			return -1;
		}
		if (list != null) {
			list.clear();
		} else {
			list = new ArrayList<Map<String, Object>>(files.length);
		}

		// 用来先保存文件夹和文件夹的两个列表
		ArrayList<Map<String, Object>> lfolders = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> lfiles = new ArrayList<Map<String, Object>>();

		if (!this.path.equals(sRoot)) {
			// 添加根目录 和 上一层目录
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", sRoot);
			map.put("path", sRoot);
			map.put("img", getImageId(sRoot));
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("name", sParent);
			map.put("path", path);
			map.put("img", getImageId(sParent));
			list.add(map);
		}

		for (File file : files) {
			if (file.isDirectory() && file.listFiles() != null) {
				// 添加文件夹
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", file.getName());
				map.put("path", file.getPath());
				map.put("img", getImageId(sFolder));
				lfolders.add(map);
			} else if (file.isFile()) {
				// 添加文件
				String sf = getSuffix(file.getName()).toLowerCase();
				if(suffix == null || suffix.length()==0 || (sf.length()>0 && suffix.indexOf("."+sf+";")>=0))
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", file.getName());
					map.put("path", file.getPath());
					map.put("img", getImageId(sf));
					lfiles.add(map);
				}
			}
		}

		list.addAll(lfolders); // 先添加文件夹，确保文件夹显示在上面
		list.addAll(lfiles); // 再添加文件

		SimpleAdapter adapter = new SimpleAdapter(
				context,
				list,
				R.layout.file_dialog_item,
				new String[] { "img", "name", "path" },
				new int[] { R.id.filedialogitem_img,
						R.id.filedialogitem_name, R.id.filedialogitem_path });
		mFileView.setAdapter(adapter);
		return files.length;
	}
	
}

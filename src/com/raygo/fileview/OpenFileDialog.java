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
	// ���帡�����ڲ���
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
		// ���漸�����ø��ļ����͵�ͼ�꣬ ��Ҫ���Ȱ�ͼ����ӵ���Դ�ļ���
		images.put(OpenFileDialog.sRoot, R.drawable.rooticon);	// ��Ŀ¼ͼ��
		images.put(OpenFileDialog.sParent, R.drawable.shangicon);	//������һ���ͼ��
		images.put(OpenFileDialog.sFolder, R.drawable.foldericon);	//�ļ���ͼ��
		images.put("png", R.drawable.imgicon);	//png�ļ�ͼ��
		images.put("jpg", R.drawable.imgicon);	//jpg�ļ�ͼ��
		images.put(OpenFileDialog.sEmpty, R.drawable.rooticon);

		//��ȡ����LocalWindowManager����  
        mWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        //��ȡLayoutParams����  
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
		// ��ȡ����������ͼ���ڲ���
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout,null);
		// ���mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), 
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// ���������б�
		mFileView = (ListView) mFloatLayout.findViewById(R.id.listViewFile);
		mFileView.setOnItemClickListener(lstner);
		refreshFileList();
	}
	
	private OnItemClickListener lstner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			// ��Ŀѡ��
			String pt = (String) list.get(position).get("path");
			String fn = (String) list.get(position).get("name");
			if (fn.equals(sRoot) || fn.equals(sParent)) {
				// ����Ǹ�Ŀ¼������һ��
				File fl = new File(pt);
				String ppt = fl.getParent();
				if (ppt != null) {
					// ������һ��
					path = ppt;
				} else {
					// ���ظ�Ŀ¼
					path = sRoot;
				}
			} else {
				File fl = new File(pt);
				if (fl.isFile()) {
					// ������ļ�
					Bundle bundle = new Bundle();
					bundle.putString("path", pt);
					bundle.putString("name", fn);
					// �����������õĻص�����
					callback.callback(bundle);

					if(mFloatLayout != null)  
			        {  
			            //�Ƴ���������  
			            mWindowManager.removeView(mFloatLayout);  
			        }
					return;
				} else if (fl.isDirectory()) {
					// ������ļ���
					// ��ô����ѡ�е��ļ���
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
		// ˢ���ļ��б�
		File[] files = null;
		try {
			files = new File(path).listFiles();
		} catch (Exception e) {
			files = null;
		}
		if (files == null) {
			// ���ʳ���
			return -1;
		}
		if (list != null) {
			list.clear();
		} else {
			list = new ArrayList<Map<String, Object>>(files.length);
		}

		// �����ȱ����ļ��к��ļ��е������б�
		ArrayList<Map<String, Object>> lfolders = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> lfiles = new ArrayList<Map<String, Object>>();

		if (!this.path.equals(sRoot)) {
			// ��Ӹ�Ŀ¼ �� ��һ��Ŀ¼
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
				// ����ļ���
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", file.getName());
				map.put("path", file.getPath());
				map.put("img", getImageId(sFolder));
				lfolders.add(map);
			} else if (file.isFile()) {
				// ����ļ�
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

		list.addAll(lfolders); // ������ļ��У�ȷ���ļ�����ʾ������
		list.addAll(lfiles); // ������ļ�

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

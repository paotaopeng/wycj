package com.golic.wycj.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.golic.wycj.Constans;
import com.golic.wycj.R;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.domain.BzdzPhoto;

public class TakePhotoActivity extends BaseActivity implements OnClickListener
{
	private int maxPhotos = 5;
	// private int minPhotos;
	private String path = Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
			+ "photo/";;
	File currentFile;

	private ArrayList<BzdzPhoto> photos;
	private ArrayList<BzdzPhoto> deletePhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<BzdzPhoto> addPhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<File> photoPaths = new ArrayList<File>();
	private ImageView imageView;
	private View preBtn;
	private View nextBtn;
	private ImageButton takeBtn;
	private TextView currentIndexTv;
	private ImageButton deleteBtn;
	private int currentPhotoIndex;
//	private Building building;
	private String ywId="";
	private boolean isEdit;
	private Button bt_finish;
	private Button bt_back;
	private PhotoDaoImpl photoDaoImpl;
	private int minPhotos=1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_take_photo);
		getData();
		initView();
		 initData(new File(Constans.PROJECT_FOLDER_PATH),
		 Constans.PHOTO_NUM_MIN, Constans.PHOTO_NUM_MAX);
	}

	private void initData(File path, int minPhotos, int maxPhotos) {
		this.maxPhotos = maxPhotos;
		this.minPhotos = minPhotos;
		if (!path.exists())
		{
			path.mkdirs();
		}
		updateView();
	}

	private void getData()
	{
		photoDaoImpl = new PhotoDaoImpl(getApplicationContext());
		// 获取已经拍照的部分
		Intent intent = getIntent();
//		building = (Building) intent.getSerializableExtra("building");
		// update = intent.getBooleanExtra("update", false);
		isEdit = intent.getBooleanExtra("isEdit", false);
		// isNew = intent.getBooleanExtra("isNew", false);
		if (isEdit)
		{
			// 如果是编辑状态，查询已经拍照过的记录
			ywId=intent.getStringExtra("ywid");
			System.out.println("ywId:"+ywId);
			photos = photoDaoImpl.findPhotos(ywId);
			System.out.println("photos:"+photos);
			for (BzdzPhoto photo : photos)
			{
				photoPaths.add(new File(photo.path, photo.fileName));
			}
			currentPhotoIndex = photoPaths.size();
		}
		else
		{
			photos = new ArrayList<BzdzPhoto>();
		}
	}

	private void initView()
	{
		imageView = (ImageView) findViewById(R.id.take_photo_imageview);
		preBtn = findViewById(R.id.take_photo_pre);
		nextBtn = findViewById(R.id.take_photo_next);
		takeBtn = (ImageButton) findViewById(R.id.take_photo_take);
		deleteBtn = (ImageButton) findViewById(R.id.take_photo_delete);
		// infoTv = (TextView) findViewById(R.id.take_photo_info);
		currentIndexTv = (TextView) findViewById(R.id.take_photo_current_index);
		bt_finish = (Button) findViewById(R.id.bt_finish);
		bt_back = (Button) findViewById(R.id.bt_back);

		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		takeBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		bt_finish.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		updateView();
	}

	// /** 获取到拍好的照片文件 */
	// public ArrayList<File> getPhotos()
	// {
	// @SuppressWarnings("unchecked")
	// ArrayList<File> result = (ArrayList<File>) photoPaths.clone(); //
	// 避免外部拿到此集合后修稿集合
	// return result;
	// }

	public static final int REQUEST_CODE_TAKE_PHOTO = 77;

	@Override
	public void onClick(View v)
	{
		if (isUpdateView)
			return;

		switch (v.getId())
		{
		case R.id.take_photo_pre:
			if (currentPhotoIndex > 1)
				currentPhotoIndex--;
			updateView();
			break;
		case R.id.take_photo_next:
			if (currentPhotoIndex < photoPaths.size())
				currentPhotoIndex++;
			updateView();
			break;
		case R.id.take_photo_take:
			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			currentFile = new File(path, System.currentTimeMillis() + ".jpg");
			System.out.println("currentFile:"+currentFile);
			Uri uri = Uri.fromFile(currentFile);
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(takePhotoIntent, REQUEST_CODE_TAKE_PHOTO);
			break;
		case R.id.take_photo_delete:
			if (photoPaths.size() == 0)
				return;
			// photoPaths.get(currentPhotoIndex - 1).delete(); //
			// 不能删除照片文件，不然会出bug
			File remove = photoPaths.remove(currentPhotoIndex - 1);
			// 判断照片是否是旧的，如果是先记下来后面在完成的时候删除数据库数据
			if (isEdit)
			{
				for (BzdzPhoto photo : photos)
				{
					if (photo.fileName.equals(remove.getName()))
					{
						deletePhotos.add(photo);
					}
				}
			}
			if (photoPaths.size() == 0)
			{
				currentPhotoIndex = 0;
			}
			else if (currentPhotoIndex > photoPaths.size())
			{
				currentPhotoIndex = photoPaths.size();
			}
			updateView();
			break;
		case R.id.bt_finish:
			// TODO 保存照片数据
			// 首先检查原有照片是否有被删除
			// for (BzdzPhoto photo : deletePhotos)
			// {
			// photoDaoImpl.deletePhoto(photo);
			// }
			// 再检查是否有新添加的照片

			if (photos.size() == 0)
			{
				// 全部需要添加
				for (File f : photoPaths)
				{
					String id = UUID.randomUUID().toString();
					BzdzPhoto bzdzPhoto = new BzdzPhoto(id,
							ywId, f.getParent(),
							f.getName());
					addPhotos.add(bzdzPhoto);
				}
			}
			else
			{
				for (File f : photoPaths)
				{
					if (!existPhoto(f))
					{
						String id = UUID.randomUUID().toString();
						BzdzPhoto bzdzPhoto = new BzdzPhoto(id,
								ywId, f.getParent(),
								f.getName());
						addPhotos.add(bzdzPhoto);
					}
				}
			}
			// 最后关闭窗口
			Intent intent = new Intent();
			intent.putExtra("delete", deletePhotos);
			intent.putExtra("add", addPhotos);
			System.out.println("takePhoto addPhotos="+addPhotos);
			System.out.println("takePhoto deletePhotos="+deletePhotos);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.bt_back:
			finish();
			break;
		}
	}

	// private void addPhoto(File f)
	// {
	// String id = UUID.randomUUID().toString();
	// BzdzPhoto bzdzPhoto = new BzdzPhoto(id, building.baseBuilding.mphm.ID,
	// f.getParent(), f.getName());
	// photoDaoImpl.insertPhoto(bzdzPhoto);
	// }

	private boolean existPhoto(File f)
	{
		for (BzdzPhoto photo : photos)
		{
			if (photo.fileName.equals(f.getName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE_TAKE_PHOTO
				&& resultCode == Activity.RESULT_OK)
		{
			// TODO 将照片进行压缩然后再处理
			compressImg();
			readPhotoDegree(currentFile.getAbsolutePath());
			photoPaths.add(currentFile);
			currentPhotoIndex = photoPaths.size();
			updateView();
		}
	}

	private void compressImg()
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		System.out.println("currentFile.getAbsolutePath():"+currentFile.getAbsolutePath());
		Bitmap bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath(),
				opts);
		int picWindth = opts.outWidth;
		int picHeight = opts.outHeight;
		int height = imageView.getHeight();
		int width = imageView.getWidth();
		if (picWindth < width && picHeight < height)
		{
			opts.inSampleSize = 1;// 压缩比例的设置也在options对象中
		}
		else
		{
			if (picWindth / width > picHeight / height)
			{
				if (picWindth % width == 0)
				{
					opts.inSampleSize = picWindth / width;
				}
				else
				{
					opts.inSampleSize = picWindth / width + 1;
				}
			}
			else
			{
				if (picHeight % height == 0)
				{
					opts.inSampleSize = picHeight / height;
				}
				else
				{
					opts.inSampleSize = picHeight / height + 1;
				}
			}
		}
		opts.inJustDecodeBounds = false;
		// 原始文件
		bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath(), opts);

		// 将压缩后的bitmap转换成File
		File file = new File(currentFile.getAbsolutePath());
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			// 最后将新文件保存进文件流
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(currentFile.getAbsolutePath()));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean isUpdateView = false;

	private void updateView()
	{
		isUpdateView = true;
		if (currentPhotoIndex >= 1)// TODO 要改成根据内容缩放显示，不然这里会爆内存溢出
		{
			fillImageView(imageView, photoPaths.get(currentPhotoIndex - 1));
		}
		else
		{
			imageView.setImageDrawable(new ColorDrawable(0xffffffff));
		}

		if (currentPhotoIndex == 0)
		{
			currentIndexTv.setText("");
			deleteBtn.setEnabled(false);
		}
		else
		{
			currentIndexTv.setText(currentPhotoIndex + "");
			deleteBtn.setEnabled(true);
		}

		if (currentPhotoIndex == 0 || currentPhotoIndex == 1)
		{
			preBtn.setClickable(false);
			preBtn.setEnabled(false);
		}
		else
		{
			preBtn.setClickable(true);
			preBtn.setEnabled(true);
		}

		if (currentPhotoIndex >= photoPaths.size())
		{
			nextBtn.setClickable(false);
			nextBtn.setEnabled(false);
		}
		else
		{
			nextBtn.setClickable(true);
			nextBtn.setEnabled(true);
		}

		if (photoPaths.size() >= maxPhotos)
		{
			takeBtn.setClickable(false);
			takeBtn.setEnabled(false);
		}
		else
		{
			takeBtn.setClickable(true);
			takeBtn.setEnabled(true);
		}
		isUpdateView = false;
	}

	private static final void fillImageView(ImageView imageView, File f)
	{
		BitmapFactory.Options options = new Options();
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
		imageView.setImageBitmap(bitmap);
	}

	public static final int readPhotoDegree(String path)
	{
		int result = 0;
		try
		{
			ExifInterface eif = new ExifInterface(path);
			int orientation = eif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
			case ExifInterface.ORIENTATION_ROTATE_90:
				result = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				result = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				result = 270;
				break;
			}
			eif.setAttribute(ExifInterface.TAG_ORIENTATION,
					String.valueOf(result));
			eif.saveAttributes();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
}

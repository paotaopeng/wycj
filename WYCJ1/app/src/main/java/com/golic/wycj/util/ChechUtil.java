package com.golic.wycj.util;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.golic.wycj.Source;
import com.golic.wycj.dao.DataAnalyseEngine;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.util.DictDialogUtil.YwzyChooseCallback;

public class ChechUtil
{
	public static void editcheck(final Activity context, final BaseAttrs attrs,
			DataAnalyseEngine analyseEngine, final YwzyDaoImpl ywzyDaoImpl,
			final GgsjDaoImpl ggsjDaoImpl)
	{
		// 检查分类错误,关联业务装用数据
		analyseEngine.nameMatching(attrs);
		if (attrs.level > 1)
		{
			new Builder(context)
					.setTitle("提示")
					.setMessage(attrs.comment)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									ArrayList<Ywzy> ywzyList = ywzyDaoImpl
											.matchYwzy(attrs);
									if (ywzyList.size() > 0)
									{
										DictDialogUtil.sigleYwzyChoose(context,
												ywzyList, attrs,
												new YwzyChooseCallback()
												{
													@Override
													public void call(Ywzy ywzy)
													{
														int update = ywzyDaoImpl
																.update(ywzy);
														if (update > 0)
														{
															Source.ywzys
																	.get(ywzy
																			.getName())
																	.add(ywzy);
															attrs.bs++;
														}
														// ggsjDaoImpl
														// .updateGgsj(attrs);
														// Source.updateGgsj(attrs);
														// context.setResult(Activity.RESULT_OK);
														// context.finish();
													}

													@Override
													public void dismiss()
													{
														ggsjDaoImpl
																.updateGgsj(attrs);
														Source.updateGgsj(attrs);
														context.setResult(Activity.RESULT_OK);
														context.finish();
													}
												});
									}
									else
									{
										ggsjDaoImpl.updateGgsj(attrs);
										Source.updateGgsj(attrs);
										context.setResult(Activity.RESULT_OK);
										context.finish();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		else
		{
			ArrayList<Ywzy> ywzyList = ywzyDaoImpl.matchYwzy(attrs);
			if (ywzyList.size() > 0)
			{
				DictDialogUtil.sigleYwzyChoose(context, ywzyList, attrs,
						new YwzyChooseCallback()
						{
							@Override
							public void call(Ywzy ywzy)
							{
								int update = ywzyDaoImpl.update(ywzy);
								if (update > 0)
								{
									Source.ywzys.get(ywzy.getName()).add(ywzy);
									attrs.bs++;
								}
								// ggsjDaoImpl.updateGgsj(attrs);
								// Source.updateGgsj(attrs);
								// context.setResult(Activity.RESULT_OK);
								// context.finish();
							}

							@Override
							public void dismiss()
							{
								ggsjDaoImpl.updateGgsj(attrs);
								Source.updateGgsj(attrs);
								context.setResult(Activity.RESULT_OK);
								context.finish();
							}
						});
			}
			else
			{
				ggsjDaoImpl.updateGgsj(attrs);
				Source.updateGgsj(attrs);
				context.setResult(Activity.RESULT_OK);
				context.finish();
			}
		}
	}

	// public static void addcheck(final Activity context, final BaseAttrs
	// attrs,
	// DataAnalyseEngine analyseEngine, final YwzyDaoImpl ywzyDaoImpl,
	// final GgsjDaoImpl ggsjDaoImpl, final BzdzDaoImpl bzdzDaoImpl)
	// {
	// analyseEngine.nameMatching(attrs);
	// if (attrs.level > 1)
	// {
	// new Builder(context)
	// .setTitle("提示")
	// .setMessage(attrs.comment)
	// .setPositiveButton("确定",
	// new DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which)
	// {
	// attrs.mphm.ID = UUID.randomUUID()
	// .toString();
	//
	// long badzInsert = bzdzDaoImpl.insert(
	// attrs.mphm, attrs.extraDz);
	//
	// if (badzInsert > 0)
	// {
	// long insert = ggsjDaoImpl.insert(attrs);
	// if (insert > 0)
	// {
	// Source.ggsjs.get(attrs.type).add(
	// attrs);
	//
	// ArrayList<Ywzy> ywzyList = ywzyDaoImpl
	// .matchYwzy(attrs);
	// if (ywzyList.size() > 0)
	// {
	// DictDialogUtil
	// .sigleYwzyChoose(
	// context,
	// ywzyList,
	// attrs,
	// new YwzyChooseCallback()
	// {
	// @Override
	// public void call(
	// Ywzy ywzy)
	// {
	// ywzyDaoImpl
	// .update(ywzy);
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// });
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// }).setNegativeButton("取消", null).show();
	// }
	// else
	// {
	// attrs.mphm.ID = UUID.randomUUID().toString();
	// long badzInsert = bzdzDaoImpl.insert(attrs.mphm, attrs.extraDz);
	//
	// if (badzInsert > 0)
	// {
	// long insert = ggsjDaoImpl.insert(attrs);
	// if (insert > 0)
	// {
	// Source.ggsjs.get(attrs.type).add(attrs);
	// ArrayList<Ywzy> ywzyList = ywzyDaoImpl.matchYwzy(attrs);
	// if (ywzyList.size() > 0)
	// {
	// DictDialogUtil.sigleYwzyChoose(context, ywzyList,
	// attrs, new YwzyChooseCallback()
	// {
	// @Override
	// public void call(Ywzy ywzy)
	// {
	// ywzyDaoImpl.update(ywzy);
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// });
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// else
	// {
	// context.setResult(Activity.RESULT_OK);
	// context.finish();
	// }
	// }
	// }

	public static void addcheck(final Activity context, final BaseAttrs attrs,
			DataAnalyseEngine analyseEngine, final YwzyDaoImpl ywzyDaoImpl,
			final GgsjDaoImpl ggsjDaoImpl)
	{
		analyseEngine.nameMatching(attrs);
		if (attrs.level > 1)
		{
			new Builder(context)
					.setTitle("提示")
					.setMessage(attrs.comment)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									ArrayList<Ywzy> ywzyList = ywzyDaoImpl
											.matchYwzy(attrs);
									if (ywzyList.size() > 0)
									{
										DictDialogUtil.sigleYwzyChoose(context,
												ywzyList, attrs,
												new YwzyChooseCallback()
												{
													@Override
													public void call(Ywzy ywzy)
													{
														int update = ywzyDaoImpl
																.update(ywzy);
														if (update > 0)
														{
															Source.ywzys
																	.get(ywzy
																			.getName())
																	.add(ywzy);
															attrs.bs++;
														}
														// long insert =
														// ggsjDaoImpl
														// .insert(attrs);
														// if (insert > 0)
														// {
														// Source.ggsjs.get(
														// attrs.type)
														// .add(attrs);
														// }
														// context.setResult(Activity.RESULT_OK);
														// context.finish();
													}

													@Override
													public void dismiss()
													{
														long insert = ggsjDaoImpl
																.insert(attrs);
														if (insert > 0)
														{
															Source.ggsjs.get(
																	attrs.type)
																	.add(attrs);
														}
														context.setResult(Activity.RESULT_OK);
														context.finish();
													}
												});
									}
									else
									{
										long insert = ggsjDaoImpl.insert(attrs);
										if (insert > 0)
										{
											Source.ggsjs.get(attrs.type).add(
													attrs);
										}
										context.setResult(Activity.RESULT_OK);
										context.finish();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		else
		{
			ArrayList<Ywzy> ywzyList = ywzyDaoImpl.matchYwzy(attrs);
			if (ywzyList.size() > 0)
			{
				DictDialogUtil.sigleYwzyChoose(context, ywzyList, attrs,
						new YwzyChooseCallback()
						{
							@Override
							public void call(Ywzy ywzy)
							{
								int update = ywzyDaoImpl.update(ywzy);
								if (update > 0)
								{
									Source.ywzys.get(ywzy.getName()).add(ywzy);
									attrs.bs++;
								}
								// long insert = ggsjDaoImpl.insert(attrs);
								// if (insert > 0)
								// {
								// Source.ggsjs.get(attrs.type).add(attrs);
								// }
								// context.setResult(Activity.RESULT_OK);
								// context.finish();
							}

							@Override
							public void dismiss()
							{
								long insert = ggsjDaoImpl.insert(attrs);
								if (insert > 0)
								{
									Source.ggsjs.get(attrs.type).add(attrs);
								}
								context.setResult(Activity.RESULT_OK);
								context.finish();
							}
						});
			}
			else
			{
				long insert = ggsjDaoImpl.insert(attrs);
				if (insert > 0)
				{
					Source.ggsjs.get(attrs.type).add(attrs);
				}
				context.setResult(Activity.RESULT_OK);
				context.finish();
			}
		}
	}
	
	public static void addcheck(final Activity context, final BaseAttrs attrs,
			DataAnalyseEngine analyseEngine, final YwzyDaoImpl ywzyDaoImpl,
			final GgsjDaoImpl ggsjDaoImpl,final PhotoDaoImpl photoDaoImpl, final ArrayList<BzdzPhoto> addPhotos,final ArrayList<BzdzPhoto> deletePhotos)
	{
		analyseEngine.nameMatching(attrs);
		if (attrs.level > 1)
		{
			new Builder(context)
					.setTitle("提示")
					.setMessage(attrs.comment)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									ArrayList<Ywzy> ywzyList = ywzyDaoImpl
											.matchYwzy(attrs);
									if (ywzyList.size() > 0)
									{
										DictDialogUtil.sigleYwzyChoose(context,
												ywzyList, attrs,
												new YwzyChooseCallback()
												{
													@Override
													public void call(Ywzy ywzy)
													{
														int update = ywzyDaoImpl
																.update(ywzy);
														if (update > 0)
														{
															Source.ywzys
																	.get(ywzy
																			.getName())
																	.add(ywzy);
															attrs.bs++;
														}
														// long insert =
														// ggsjDaoImpl
														// .insert(attrs);
														// if (insert > 0)
														// {
														// Source.ggsjs.get(
														// attrs.type)
														// .add(attrs);
														// }
														// context.setResult(Activity.RESULT_OK);
														// context.finish();
													}

													@Override
													public void dismiss()
													{
														insertGgsj(attrs, ggsjDaoImpl, addPhotos, deletePhotos,
																photoDaoImpl);
														context.setResult(Activity.RESULT_OK);
														context.finish();
													}
												});
									}
									else
									{
										insertGgsj(attrs, ggsjDaoImpl, addPhotos, deletePhotos,
												photoDaoImpl);
										context.setResult(Activity.RESULT_OK);
										context.finish();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		else
		{
			ArrayList<Ywzy> ywzyList = ywzyDaoImpl.matchYwzy(attrs);
			if (ywzyList.size() > 0)
			{
				DictDialogUtil.sigleYwzyChoose(context, ywzyList, attrs,
						new YwzyChooseCallback()
						{
							@Override
							public void call(Ywzy ywzy)
							{
								int update = ywzyDaoImpl.update(ywzy);
								if (update > 0)
								{
									Source.ywzys.get(ywzy.getName()).add(ywzy);
									attrs.bs++;
								}
								// long insert = ggsjDaoImpl.insert(attrs);
								// if (insert > 0)
								// {
								// Source.ggsjs.get(attrs.type).add(attrs);
								// }
								// context.setResult(Activity.RESULT_OK);
								// context.finish();
							}

							@Override
							public void dismiss()
							{
								insertGgsj(attrs, ggsjDaoImpl, addPhotos, deletePhotos,
										photoDaoImpl);
								context.setResult(Activity.RESULT_OK);
								context.finish();
							}
						});
			}
			else
			{
				insertGgsj(attrs, ggsjDaoImpl, addPhotos, deletePhotos,
						photoDaoImpl);
				context.setResult(Activity.RESULT_OK);
				context.finish();
			}
		}
	}

	/**
	 * 插入公共数据的同时维护相应的公共数据照片
	 * @param attrs
	 * @param ggsjDaoImpl
	 * @param addPhotos
	 * @param deletePhotos
	 * @param photoDaoImpl
	 */
	private static void insertGgsj(final BaseAttrs attrs,
			final GgsjDaoImpl ggsjDaoImpl, ArrayList<BzdzPhoto> addPhotos,
			ArrayList<BzdzPhoto> deletePhotos, PhotoDaoImpl photoDaoImpl) {
		long insert = ggsjDaoImpl.insert(attrs);
		if (insert > 0)
		{
			Source.ggsjs.get(attrs.type).add(attrs);
		}
		for (BzdzPhoto photo : addPhotos)
		{
			photo.bzdzId = attrs.ID;
		}
		System.out.println("最终插入的数据：" + addPhotos);
		photoDaoImpl.updatePhotos(deletePhotos,
				addPhotos);
	}
	
	public static void editcheck(final Activity context, final BaseAttrs attrs,
			DataAnalyseEngine analyseEngine, final YwzyDaoImpl ywzyDaoImpl,
			final GgsjDaoImpl ggsjDaoImpl,final PhotoDaoImpl photoDaoImpl, final ArrayList<BzdzPhoto> addPhotos,final ArrayList<BzdzPhoto> deletePhotos)
	{
		// 检查分类错误,关联业务装用数据
		analyseEngine.nameMatching(attrs);
		if (attrs.level > 1)
		{
			new Builder(context)
					.setTitle("提示")
					.setMessage(attrs.comment)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									ArrayList<Ywzy> ywzyList = ywzyDaoImpl
											.matchYwzy(attrs);
									if (ywzyList.size() > 0)
									{
										DictDialogUtil.sigleYwzyChoose(context,
												ywzyList, attrs,
												new YwzyChooseCallback()
												{
													@Override
													public void call(Ywzy ywzy)
													{
														int update = ywzyDaoImpl
																.update(ywzy);
														if (update > 0)
														{
															Source.ywzys
																	.get(ywzy
																			.getName())
																	.add(ywzy);
															attrs.bs++;
														}
														// ggsjDaoImpl
														// .updateGgsj(attrs);
														// Source.updateGgsj(attrs);
														// context.setResult(Activity.RESULT_OK);
														// context.finish();
													}

													@Override
													public void dismiss()
													{
														updateGgsj(attrs, ggsjDaoImpl, photoDaoImpl, addPhotos,
																deletePhotos);
														context.setResult(Activity.RESULT_OK);
														context.finish();
													}
												});
									}
									else
									{
										updateGgsj(attrs, ggsjDaoImpl, photoDaoImpl, addPhotos,
												deletePhotos);
										context.setResult(Activity.RESULT_OK);
										context.finish();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		else
		{
			ArrayList<Ywzy> ywzyList = ywzyDaoImpl.matchYwzy(attrs);
			if (ywzyList.size() > 0)
			{
				DictDialogUtil.sigleYwzyChoose(context, ywzyList, attrs,
						new YwzyChooseCallback()
						{
							@Override
							public void call(Ywzy ywzy)
							{
								int update = ywzyDaoImpl.update(ywzy);
								if (update > 0)
								{
									Source.ywzys.get(ywzy.getName()).add(ywzy);
									attrs.bs++;
								}
								// ggsjDaoImpl.updateGgsj(attrs);
								// Source.updateGgsj(attrs);
								// context.setResult(Activity.RESULT_OK);
								// context.finish();
							}

							@Override
							public void dismiss()
							{
								updateGgsj(attrs, ggsjDaoImpl, photoDaoImpl, addPhotos,
										deletePhotos);
								context.setResult(Activity.RESULT_OK);
								context.finish();
							}
						});
			}
			else
			{
				updateGgsj(attrs, ggsjDaoImpl, photoDaoImpl, addPhotos,
						deletePhotos);
				context.setResult(Activity.RESULT_OK);
				context.finish();
			}
		}
	}

	/**
	 * 更新公共数据的同时，维护公共数据的照片
	 * @param attrs
	 * @param ggsjDaoImpl
	 * @param photoDaoImpl
	 * @param addPhotos
	 * @param deletePhotos
	 */
	private static void updateGgsj(final BaseAttrs attrs,
			final GgsjDaoImpl ggsjDaoImpl, final PhotoDaoImpl photoDaoImpl,
			final ArrayList<BzdzPhoto> addPhotos,
			final ArrayList<BzdzPhoto> deletePhotos) {
		ggsjDaoImpl.updateGgsj(attrs);
		Source.updateGgsj(attrs);
		//处理照片更新
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
				return null;
			}

		}.execute();
	}
}
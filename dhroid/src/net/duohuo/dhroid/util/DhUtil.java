package net.duohuo.dhroid.util;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class DhUtil {

	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (scale * dipValue + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String delHtml(String str) {
		String info = str.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		info = info.replaceAll("[(/>)<]", "");
		return info;
	}

	/**
	 * 通过 uri 获取图片的文件
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public File uriToImageFile(Activity context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = context.managedQuery(uri, proj, null, null,
				null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor
				.getString(actual_image_column_index);
		File file = new File(img_path);
		return file;
	}

	public static ObjectMapper getDefaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// 设置将对象转换成JSON字符串时候:包含的属性不能为空或"";
		// Include.Include.ALWAYS 默认
		// Include.NON_DEFAULT 属性为默认值不序列化
		// Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化
		// Include.NON_NULL 属性为NULL 不序列化
		// mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
		// 设置将MAP转换为JSON时候只转换值不等于NULL的
		// mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES,
		// false);
		// mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		// mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		// //设置有属性不能映射成PO时不报错
		// mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}

}

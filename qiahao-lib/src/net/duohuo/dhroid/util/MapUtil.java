package net.duohuo.dhroid.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MapUtil {

	/**
	 * 获取 string, key可用点用分割 ,空返回null
	 * 
	 * @param jo
	 * @param key
	 * @return
	 */
	public static String getString(Map<String, Object> map, String key) {
		if (map == null) {
			return null;
		}

		if (TextUtils.isEmpty("key")) {
			Log.e("duohuo", "请输入key");
			return null;
		}
		String value = null;
		if (map.containsKey(key)) {
			value = map.get(key) + "";
			// if (value.endsWith(".0")) {
			// return value.replace(".0", "");
			// }

		} else {
			return value;
		}
		if ("null".equals(value)) {
			value = "";
		}
		return value;
	}

	public static String getString(Map<String, Object> map, String key,
			String def) {
		String value = getString(map, key);
		if (!TextUtils.isEmpty(value)) {
			return value;
		} else {
			return def;
		}
	}

	public static Integer getInt(Map<String, Object> map, String key) {
		String value = getString(map, key);
		if (!TextUtils.isEmpty(value)) {

			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}

		}
		return 0;
	}

	public static Integer getInt(Map<String, Object> map, String key,
			Integer def) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}

		}
		return def;
	}

	public static Long getLong(Map<String, Object> map, String key) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
			}

		}
		return -1l;
	}

	public static Long getLong(Map<String, Object> map, String key, Long def) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
			}
		}
		return def;
	}

	public static Float getFloat(Map<String, Object> map, String key) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
			}
		}
		return 0f;
	}

	public static Double getDouble(Map<String, Object> map, String key) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
			}

		}
		return 0d;
	}

	/**
	 * 获取boolean 如果没有返回 false
	 * 
	 * @param jo
	 * @param key
	 * @return
	 */
	public static Boolean getBoolean(Map<String, Object> map, String key) {
		String value = getString(map, key);
		if (value != null) {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static Map<String, Object> getMap(Map<String, Object> map, String key) {
		if (map == null || TextUtils.isEmpty(key))
			return null;
		try {
			if (key.contains(".")) {
				String[] tks = key.split("\\.");
				Map<String, Object> mapt = map;
				Map<String, Object> value = null;
				for (int j = 0; j < tks.length; j++) {
					String tk = tks[j];
					if (mapt.containsKey(tk)) {
						if (j == tks.length - 1) {
							value = (Map<String, Object>) mapt.get(tk);
						} else {
							mapt = (Map<String, Object>) mapt.get(tk);
						}
					} else {
						return null;
					}
				}
				return value;
			} else {
				Map<String, Object> value = null;
				if (map.containsKey(key)) {
					value = (Map<String, Object>) map.get(key);
				}
				if ("null".equals(value)) {
					value = null;
				}
				return value;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static List<Map<String, Object>> getMapList(Map<String, Object> map,
			String key) {
		if (map == null || TextUtils.isEmpty(key))
			return null;
		try {
			if (key.contains(".")) {
				String[] tks = key.split("\\.");
				Map<String, Object> mapt = map;
				List<Map<String, Object>> value = null;
				for (int j = 0; j < tks.length; j++) {
					String tk = tks[j];
					if (mapt.containsKey(tk)) {
						if (j == tks.length - 1) {
							value = (List<Map<String, Object>>) mapt.get(tk);
						} else {
							mapt = (Map<String, Object>) mapt.get(tk);
						}
					} else {
						return null;
					}
				}
				return value;
			} else {
				List<Map<String, Object>> value = null;
				if (map.containsKey(key)) {

					value = (List<Map<String, Object>>) map.get(key);
				}
				if ("null".equals(value)) {
					value = null;
				}
				return value;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Map<String, Object> getMap(String result) {
		Gson gson = new Gson();

		Map<String, Object> data = gson.fromJson(result,
				new TypeToken<Map<String, Object>>() {
				}.getType());

		return data;
	}

	// public static List<HashMap<String, Object>> getHashMapList(
	// HashMap<String, Object> map, String key) {
	// if (map == null || TextUtils.isEmpty(key))
	// return null;
	// try {
	// if (key.contains(".")) {
	// String[] tks = key.split("\\.");
	// Map<String, Object> mapt = map;
	// List<HashMap<String, Object>> value = null;
	// for (int j = 0; j < tks.length; j++) {
	// String tk = tks[j];
	// if (mapt.containsKey(tk)) {
	// if (j == tks.length - 1) {
	// value = (List<HashMap<String, Object>>) mapt
	// .get(tk);
	// } else {
	// mapt = (HashMap<String, Object>) mapt.get(tk);
	// }
	// } else {
	// return null;
	// }
	// }
	// return value;
	// } else {
	// List<HashMap<String, Object>> value = null;
	// if (map.containsKey(key)) {
	//
	// value = (List<HashMap<String, Object>>) map.get(key);
	// }
	// if ("null".equals(value)) {
	// value = null;
	// }
	// return value;
	// }
	// } catch (Exception e) {
	// return null;
	// }
	// }

}

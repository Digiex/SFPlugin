package net.digiex.simplefeatures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;

public class SFTranslation {
	private static SFTranslation instance = new SFTranslation();

	public static SFTranslation getInstance() {
		return instance;
	}

	public List<String> allowedLangs = new ArrayList<String>();
	private final HashMap<String, Properties> langMap = new HashMap<String, Properties>();

	private boolean field_46111_e;

	private SFTranslation() {
		allowedLangs.add("en_US");
		allowedLangs.add("fi_FI");
		loadLang("en_US");
	}

	public boolean func_46110_d() {
		return field_46111_e;
	}

	public HashMap<String, Properties> getLangMap() {
		return langMap;
	}

	public void loadLang(String langid) {
		if (!langMap.containsKey(langid)) {
			Properties var2 = new Properties();
			if (!langMap.containsKey("en_US") || langMap.get("en_US") == null) {
				try {
					langMap.remove("en_US");
					loadLangFromFile(var2, "en_US");
					langMap.put("en_US", var2);
				} catch (IOException var8) {
					;
				}
			} else {
				var2 = langMap.get("en_US");
			}
			field_46111_e = false;
			if (!"en_US".equals(langid)) {
				try {
					loadLangFromFile(var2, langid);
					Enumeration<?> var3 = var2.propertyNames();

					while (var3.hasMoreElements() && !field_46111_e) {
						Object var4 = var3.nextElement();
						Object var5 = var2.get(var4);
						if (var5 != null) {
							String var6 = var5.toString();

							for (int var7 = 0; var7 < var6.length(); ++var7) {
								if (var6.charAt(var7) >= 256) {
									field_46111_e = true;
									break;
								}
							}
						}
					}
				} catch (IOException var9) {
					var9.printStackTrace();
					return;
				}
			}
			if (langMap.containsKey(langid)) {
				langMap.remove(langid);
			}
			langMap.put(langid, var2);
		}
	}

	private void loadLangFromFile(Properties properties, String s)
			throws IOException {
		BufferedReader bufferedreader = new BufferedReader(
				new InputStreamReader(((SFPlugin) Bukkit.getServer()
						.getPluginManager().getPlugin("SimpleFeatures"))
						.getResource("sflang/" + s + ".lang"), "UTF-8"));
		for (String s1 = bufferedreader.readLine(); s1 != null; s1 = bufferedreader
				.readLine()) {
			s1 = s1.trim();
			if (s1.startsWith("#")) {
				continue;
			}
			String as[] = s1.split("=");
			if (as != null && as.length == 2) {
				properties.setProperty(as[0], as[1]);
			}
		}
	}

	public String translateKey(String s, String langid) {
		if (!allowedLangs.contains(langid)) {
			langid = "en_US";
		}
		Properties lang = langMap.get(langid);
		if (lang == null) {
			loadLang(langid);
			lang = langMap.get(langid);
			if (lang == null) {
				return s;
			}
		}
		return lang.getProperty(s, s);
	}

	public String translateKeyFormat(String s, String langid, Object... args) {
		if (!allowedLangs.contains(langid)) {
			langid = "en_US";
		}
		Properties lang = langMap.get(langid);
		if (lang == null) {
			loadLang(langid);
			lang = langMap.get(langid);
			if (lang == null) {
				return String.format(s, args);
			}
		}
		String s1 = lang.getProperty(s, s);
		return String.format(s1, args);
	}
}

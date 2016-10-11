package zx.property;

import java.io.*;
import java.util.Properties;

/**
 * 功能描述：
 * 时间：2016/3/27 12:16
 *
 * @author ：zhaokuiqiang
 */
public class PropertyFile {
	public static String readMaxId(String propertyFile, String maxid) throws IOException {
		String id = read(propertyFile, maxid);
		if (id == null)
			return "0";
		else
			return id;

	}

	public static String read(String propertyFile, String key) throws IOException {
		Properties props = getProperty(propertyFile);
		String value = props.getProperty(key);
		return value;

	}

	protected static Properties getProperty(String propertyFile) throws IOException {
		Properties props = new Properties();

		File file = new File(propertyFile);
		if(!file.exists())
			file.createNewFile();

		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		}
		props.load(is);
		is.close();
		return props;
	}

	protected static Properties getProperty(InputStream is) throws IOException {
		Properties props = new Properties();

		props.load(is);
		is.close();
		return props;
	}

	public static void write(String propertyFile, String key, String value) throws IOException {
		Properties props = getProperty(propertyFile);
		OutputStream fos = new FileOutputStream(propertyFile);
		props.setProperty(key, value);

		props.store(fos, "Update '" + key + "' value");

	}

	public static void delete(String propertyFile, String key) throws IOException {
		Properties props = getProperty(propertyFile);
		OutputStream fos = new FileOutputStream(propertyFile);
		props.remove(key);

		props.store(fos, "Delete '" + key + "' value");

	}
}

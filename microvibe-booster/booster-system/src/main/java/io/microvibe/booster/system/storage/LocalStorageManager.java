package io.microvibe.booster.system.storage;

import io.microvibe.booster.commons.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author Qt
 * @since Jul 14, 2018
 */
@Component
public class LocalStorageManager implements StorageManager {

	private File localStorageDir;

	@Value("${system.file.upload.dir:${user.home}/upload}")
	private void setFileDir(String fileDir) {
		File localStorageDir = new File(fileDir);
		if (localStorageDir.exists() && !localStorageDir.isDirectory()) {
			throw new RuntimeException("local storage dir is wrong: " + fileDir);
		}
		if (!localStorageDir.exists()) {
			localStorageDir.mkdirs();
			if (!localStorageDir.exists()) {
				throw new RuntimeException("local storage dir not exists: " + fileDir);
			}
		}
		this.localStorageDir = localStorageDir;
	}

	@Override
	public StorageMode storageMode() {
		return StorageMode.local;
	}

	@Override
	public InputStream input(String filepath) throws IOException {
		File file = new File(localStorageDir, filepath);
		if (!file.exists()) {
			throw new FileNotFoundException(filepath);
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			return fis;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(filepath);
		}
	}

	@Override
	public OutputStream output(String filepath) throws IOException {
		File file = new File(localStorageDir, filepath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
			if (!file.getParentFile().exists()) {
				throw new FileNotFoundException(filepath);
			}
		}
		FileOutputStream fos = new FileOutputStream(file);
		return fos;
	}

	@Override
	public void output(String filepath, byte[] byteArray) throws IOException {
		File file = new File(localStorageDir, filepath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
			if (!file.getParentFile().exists()) {
				throw new FileNotFoundException(filepath);
			}
		}
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		try {
			IOUtils.write(byteArray, bos);
			bos.flush();
		}finally {
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(fos);
		}
	}

}

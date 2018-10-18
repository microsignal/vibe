package io.microvibe.booster.system.storage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StorageManager  {

	@PostConstruct
	default void init() {
		Storages.register(storageMode(),this);
	}

	StorageMode storageMode();

	InputStream input(String filepath) throws IOException;

	OutputStream output(String filepath)throws  IOException;

	void output(String filepath, byte[] byteArray) throws IOException;
}

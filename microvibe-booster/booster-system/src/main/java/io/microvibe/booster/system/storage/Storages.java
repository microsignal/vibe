package io.microvibe.booster.system.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since Jul 14, 2018
 */
public class Storages {

	private static final Map<StorageMode, StorageManager> storageManagers = new ConcurrentHashMap<>();

	public static StorageManager getManager(StorageMode mode) {
		return storageManagers.get(mode);
	}

	public static void register(StorageMode mode, StorageManager storageManager) {
		storageManagers.put(mode, storageManager);
	}

}

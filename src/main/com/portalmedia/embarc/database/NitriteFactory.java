package com.portalmedia.embarc.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.exceptions.NitriteException;

/**
 * Create new db or return existing db instance
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class NitriteFactory<T> {

	private static Nitrite nitrite;
	final Class<T> typeParameterClass;
	
	public NitriteFactory(Class<T> typeParameterClass) {
		this.typeParameterClass = typeParameterClass;
		getNitrite();
	};
	
	private static String createPath() {
		String path = File.separator + "tmp";

		try {
			final Path fullPath = Paths.get(path);
			if (Files.notExists(fullPath)) {
				Files.createDirectory(fullPath);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return path;
	}

	public static Nitrite getNitrite() {
		String path = createPath();
		String filePath = path + File.separator + "new-nitrite-database.db";

		if (nitrite == null) {
			try {
				nitrite = Nitrite.builder()
					    .compressed()
					    .filePath(filePath)
					    .openOrCreate();
			} catch(NitriteException ex) {
				System.out.println("Error in getNitrite(): " + ex);
			}
		} else {
			//System.out.println("Nitrite DB already exists at path: " + filePath);
			return nitrite;
		}
		
		System.out.println("Returning nitrite: " + nitrite);
		return nitrite;
	}
	
}

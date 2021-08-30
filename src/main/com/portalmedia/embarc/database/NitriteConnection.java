package com.portalmedia.embarc.database;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;

/**
 * Retrieve nitrite repository
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class NitriteConnection {

	public static <T> ObjectRepository<T> getRepository(Class<T> typeParameterClass) {
		final Nitrite nitrite = NitriteFactory.getNitrite();
		return nitrite.getRepository(typeParameterClass);
	}

}

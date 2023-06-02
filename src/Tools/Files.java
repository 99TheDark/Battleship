package Tools;

import java.io.InputStream;

public class Files {

	public static InputStream getResource(String location) {

		return Files.class.getResourceAsStream(location);

	}

}
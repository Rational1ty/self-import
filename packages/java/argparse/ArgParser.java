import static java.lang.System.err;
import static java.lang.System.out;

import java.lang.reflect.Field;

public final class ArgParser {
	private ArgParser() {}

	public static <T> void parse(String[] args, T template) {
		try {
			Field[] fields = template.getClass().getDeclaredFields();

			for (Field f : fields) {
				Class<?> type = f.getType();
				String name = f.getName();
				Object value = f.get(template);

				out.printf("%s %s = %s%n", type.getSimpleName(), name, value);
			}
		} catch (Exception ex) {
			err.println(ex);
			System.exit(0);
		}
	}

	public static <T> String usage(T template) {
		return "";
	}
}
import static java.lang.System.err;
import static java.lang.System.out;

import java.lang.reflect.Field;

public abstract class ArgTemplate {
	private Field[] fields;

	public ArgTemplate(String[] args) {
		try {
			fields = this.getClass().getDeclaredFields();

			for (Field f : fields) {
				Class<?> type = f.getType();
				String name = f.getName();
				Object value = f.get(this);

				out.printf("%s %s = %s%n", type.getSimpleName(), name, value);
			}
		} catch (Exception ex) {
			err.println(ex);
			System.exit(0);
		}
	}

	public void parse(String[] args) {

	}

	public String usage() {
		return "";
	}

	@Override
	public String toString() {
		return "";
	}
}

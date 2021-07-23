import java.util.Objects;

/**
 * A pair of two values. The types of the values may or may not be the same.
 * 
 * @param <F> the type of the first value
 * @param <S> the type of the second value
 */
public class Pair<F, S> {
	public final F first;
	public final S second;

	public Pair() {
		first = null;
		second = null;
	}

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> of(F first, S second) {
		return new Pair<>(first, second);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Pair<?, ?>)) return false;

		var other = (Pair<?, ?>) obj;

		return first.equals(other.first) && second.equals(other.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}
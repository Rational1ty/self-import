// Best approaches:
//   final class with static parse(String[], Class<?>) method
//   abstract class with constructor and parse(String[]) method

class ImageArgs extends ArgTemplate {
	@Parameter("The path to the input image")
	public String imagePath;
	
	@Parameter("Number of colors to quantize to")
	public int ncolors = 8;

	@Parameter("Path to output file")
	public String outputPath = "empty";

	public ImageArgs(String[] args) {
		super(args);
	}
}

public class Program {
	@Parameter("The path to the input image")
	public static String imagePath;
	
	@Parameter("Number of colors to quantize to")
	public static int ncolors = 8;

	@Parameter("Path to output file")
	public static String outputPath = "empty";

	public static void main(String[] args) {
		// ArgParser.parse(args, Program.class);
		// or
		var argv2 = new ImageArgs(args);
	}
}
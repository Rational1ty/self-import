import static java.lang.System.out;

import java.io.File;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) throws Exception {
		var img = ImageIO.read(new File("C:\\Users\\mdavi\\Downloads\\angry-laugh.png"));

		for (int i = 512; i > 1; i /= 2) {
			out.printf("Quantizing with %d colors... ", i);
			long start = System.currentTimeMillis();
	
			File result = new File(String.format("C:\\Users\\mdavi\\Downloads\\angry-laugh-%d.png", i));
			var quantized = Images.quantize(img, i);
			ImageIO.write(quantized, "png", result);
	
			long end = System.currentTimeMillis();
			out.printf("Finished in %d ms%n", end - start);
		}
	}
}
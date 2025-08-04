import static java.lang.System.out;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public final class Images {
	private static final int N_CHANNELS = 3;

	public static void main(String[] args) throws IOException {
		// TODO: make into command line utility once parse-args is done
		// TODO: update implementation to use java.nio API where possible

		long start = System.currentTimeMillis();
		
		File source = new File("./test/sunglasses.jpg");
		var original = ImageIO.read(source);

		var ascii = toASCII(original);
		var fw = new PrintWriter(new File("./test/ascii.txt"));
		for (String line : ascii) {
			fw.println(line);
		}
		fw.close();
		
		long end = System.currentTimeMillis();
		out.printf("Finished in %d ms%n", end - start);


		// var img = ImageIO.read(new File("./test/original.jpg"));
		// for (int i = 256; i > 1; i /= 2) {
		// 	long start = System.currentTimeMillis();
	
		// 	File result = new File(String.format("./test/quantized%d.png", i));
		// 	var quantized = quantize(img, i);
		// 	ImageIO.write(quantized, "png", result);
	
		// 	long end = System.currentTimeMillis();

		// 	out.printf("%3d: Finished in %d ms%n", i, end - start);
		// }


		// long start = System.currentTimeMillis();
		
		// int numColors = 64;
		// File source   = new File("./test/original.jpg");
		// String fName  = String.format("quantized%d-cr", numColors);

		// var original  = ImageIO.read(source);
		// var quantized = quantize(original, numColors);

		// File res = new File(String.format("./test/%s.png", fName));
		// ImageIO.write(quantized, "png", res);
		
		// long end = System.currentTimeMillis();

		// out.printf("%d: Finished in %d ms%n", numColors, end - start);
	}

	private Images() {}

	public static BufferedImage createBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// create a blank BufferedImage with transparency
		BufferedImage buffImg = new BufferedImage(
			img.getWidth(null),
			img.getHeight(null),
			BufferedImage.TYPE_INT_ARGB
		);

		// draw the original Image onto the new BufferedImage
		Graphics2D g2d = buffImg.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		return buffImg;
	}

	public static BufferedImage createBufferedImage(int[] pixels, int w, int h) throws IllegalArgumentException {
		if ((w * h) != pixels.length)
			throw new IllegalArgumentException("pixels array must exactly fill dimensions (w x h == # of pixels)");

		// convert from linear RGB to sRGB
		pixels = IntStream.of(pixels)
			.map(Images::gammaCompress)
			.toArray();
			
		BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] internalBuffer = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, internalBuffer, 0, pixels.length);

		return res;
	}

	public static BufferedImage createGrayscaleImage(byte[] pixels, int w, int h) {
		if ((w * h) != pixels.length)
			throw new IllegalArgumentException("pixels array must exactly fill dimensions (w x h == # of pixels)");

		// convert from linear RGB to sRGB
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = gammaCompress(pixels[i]);
		}

		BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		byte[] internalBuffer = ((DataBufferByte) res.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, internalBuffer, 0, pixels.length);

		return res;
	}

	public static BufferedImage quantize(BufferedImage img, int numColors) throws IllegalArgumentException {
		if (img == null) return null;

		int[] pixels    = getPixels(img);
		int[] palette   = getPalette(pixels, numColors);
		int[] quantized = quantize(pixels, palette);

		return createBufferedImage(quantized, img.getWidth(), img.getHeight());
	}

	private static int[] getPixels(BufferedImage img) {
		// get colors of all pixels in img
		int[] pixels = img.getRGB(
			0, 0, img.getWidth(), img.getHeight(),	// x, y, w, h
			null, 0, img.getWidth()					// target array, offset, scan size
		);

		// convert from sRGB to linear RGB
		return IntStream.of(pixels)
			.map(Images::gammaExpand)
			.toArray();
	}

	private static int[] quantize(int[] pixels, int[] palette) {
		int[] quantized = new int[pixels.length];
		
		for (int i = 0; i < pixels.length; i++) {
			int[] pix = getRGB(pixels[i]);

			int minIndex = 0;
			double minDist = Double.MAX_VALUE;

			for (int j = 0; j < palette.length; j++) {
				int[] pal = getRGB(palette[j]);
				double d = dist3d(pix, pal);

				if (d < minDist) {
					minDist = d;
					minIndex = j;
				}
			}

			quantized[i] = palette[minIndex];
		}

		return quantized;
	}

	private static double dist3d(int[] p1, int[] p2) {
		int dx = (p2[0] - p1[0]) * (p2[0] - p1[0]);
		int dy = (p2[1] - p1[1]) * (p2[1] - p1[1]);
		int dz = (p2[2] - p1[2]) * (p2[2] - p1[2]);

		return Math.sqrt(dx + dy + dz);
	}

	// uses median cut algorithm to reduce number of colors
	public static int[] getPalette(int[] pixels, int numColors) throws IllegalArgumentException {
		// check arguments
		if (pixels == null)
			throw new IllegalArgumentException("img must not be null");
		if (!isPowerOf2(numColors))
			throw new IllegalArgumentException("numColors must be a power of 2");

		return medianCut(pixels, numColors);
	}

	private static boolean isPowerOf2(int i) {
		return i > 0 && (i & (i - 1)) == 0;
	}

	// iterative median cut algorithm
	private static int[] medianCut(int[] colors, int nBuckets) {
		int mainChannel = getGreatestRangeChannel(colors);

		// get comparator to sort by main channel
		Comparator<Integer> compareByMain = switch (mainChannel) {
			case 0 -> compareByMain = (c1, c2) -> red(c1) - red(c2);
			case 1 -> compareByMain = (c1, c2) -> green(c1) - green(c2);
			case 2 -> compareByMain = (c1, c2) -> blue(c1) - blue(c2);
			default -> null;
		};

		int[] sorted = IntStream.of(colors)
			.boxed()
			.sorted(compareByMain)
			.mapToInt(i -> i)
			.toArray();

		final int chunkSize = sorted.length / nBuckets;

		// divide sorted colors into buckets
		int[][] buckets = new int[nBuckets][];

		for (int i = 0; i < buckets.length; i++) {
			int start  = i * chunkSize;
			// if on last bucket, fill all remaining colors
			int end    = i == nBuckets - 1 ? sorted.length : start + chunkSize;
			buckets[i] = Arrays.copyOfRange(sorted, start, end);
		}

		// channel averages for each bucket
		int[][] averages = new int[nBuckets][N_CHANNELS];

		// get averages for each bucket
		for (int i = 0; i < buckets.length; i++) {
			for (int color : buckets[i]) {
				int[] rgb = getRGB(color);

				averages[i][0] += rgb[0];
				averages[i][1] += rgb[1];
				averages[i][2] += rgb[2];
			}

			// same thing here
			averages[i][0] /= chunkSize;
			averages[i][1] /= chunkSize;
			averages[i][2] /= chunkSize;
		}

		return Stream.of(averages)
			.flatMapToInt(rgb -> IntStream.of(getColor(rgb)))
			.toArray();
	}

	// Returns 0 for red, 1 for green, or 2 for blue
	private static int getGreatestRangeChannel(int[] colors) {
		// color range (min -> max) for each channel
		// even indexes are minimums; odd indexes are maximums
		int[] minmax = {
			Integer.MAX_VALUE, 0,	// red
			Integer.MAX_VALUE, 0,	// green
			Integer.MAX_VALUE, 0	// blue
		};

		for (int color : colors) {
			int[] channels = getRGB(color);

			// for each channel in the current color, check if it is a new min/max
			for (int i = 0, j = 0; i < N_CHANNELS && j < minmax.length; i++, j += 2) {
				int chnl = channels[i];

				// check for new min
				if (chnl < minmax[j]) {
					minmax[j] = chnl;
				}
				// check for new max
				if (chnl > minmax[j + 1]) {
					minmax[j + 1] = chnl;
				}
			}
		}

		int[] ranges = {
			minmax[1] - minmax[0],	// red range
			minmax[3] - minmax[2],	// green range
			minmax[5] - minmax[4]	// blue range
		};

		// max range value
		int max = Math.max(ranges[0], Math.max(ranges[1], ranges[2]));
		
		// return max range index
		for (int i = 0; i < N_CHANNELS; i++) {
			if (ranges[i] == max) return i;
		}
		return 0;
	}

	public static BufferedImage grayscale(BufferedImage img) {
		if (img == null) return null;

		int[] pixels  = getPixels(img);
		byte[] values = new byte[pixels.length];

		for (int i = 0; i < values.length; i++) {
			values[i] = getGray(pixels[i]);
		}

		return createGrayscaleImage(values, img.getWidth(), img.getHeight());
	}

	private static byte getGray(int color) {
		int[] rgb = getRGB(color);
		float[] comps = new float[N_CHANNELS];

		// convert from range [0, 255] to [0, 1]
		comps[0] = rgb[0] / 255f;
		comps[1] = rgb[1] / 255f;
		comps[2] = rgb[2] / 255f;
		
		// multiply by weights
		comps[0] *= 0.2126;
		comps[1] *= 0.7152;
		comps[2] *= 0.0722;

		// calculate sum
		float y = comps[0] + comps[1] + comps[2];

		return (byte) (y * 255);
	}

	public static List<String> toASCII(BufferedImage img) {
		// TODO: make images keep aspect ratio

		// window measurements in pixels
		final int SCREEN_WIDTH    = 1920;
		final int SCREEN_HEIGHT   = 1080;
		final int SCROLLBAR_WIDTH = 24;

		// font size in pixels
		final int FONT_HEIGHT = 16;
		final int FONT_WIDTH  = FONT_HEIGHT / 2;
		
		// number of characters per line and number of lines in window
		final int LINE_LENGTH = (SCREEN_WIDTH - SCROLLBAR_WIDTH) / FONT_WIDTH;
		final int N_LINES     = SCREEN_HEIGHT / FONT_HEIGHT;

		// palette used for ASCII raytracing spheres: " .:;~=#OB8%&"
		String palette = " .,:=#$@";

		int w            = img.getWidth();
		int h            = img.getHeight();
		int sampleWidth  = w / LINE_LENGTH;
		int sampleHeight = (h / N_LINES) / 2;

		var grayscale = grayscale(quantize(img, palette.length()));
		int[] pixels = getPixels(grayscale);
		byte[][] values = new byte[h][w];

		// map pixel grayscale values to 2d array
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				values[y][x] = getGray(pixels[(y * w) + x]);
			}
		}

		var ascii = new ArrayList<String>(N_LINES);

		Point sample = new Point();
		for (int y = 0; y < N_LINES; y++) {
			var line = new StringBuilder(LINE_LENGTH);

			for (int x = 0; x < LINE_LENGTH; x++) {
				sample.y = y * sampleHeight;
				sample.x = x * sampleWidth;
				byte val = values[sample.y >= h ? h - 1 : sample.y][sample.x];
				int luminance = Byte.toUnsignedInt(val) / 32;
				line.append(palette.charAt(luminance));
			}

			ascii.add(line.toString());
		}

		return ascii;
	}

	// private static byte get2dSubarrayAverage(byte[][] original, Point from, Point to) {
	// 	byte[][] subarr = new byte[to.y - from.y][];
	// 	for (int i = from.y; i < to.y; i++) {
	// 		subarr[i - from.y] = Arrays.copyOfRange(original[i], from.x, to.x);
	// 	}

	// 	int average = 0;
	// 	for (int r = 0; r < subarr.length; r++) {
	// 		for (int c = 0; c < subarr[r].length; c++) {
	// 			average += Byte.toUnsignedInt(subarr[r][c]);
	// 		}
	// 	}

	// 	return (byte) (average / (subarr.length * subarr[0].length));
	// }

	/**
	 * Converts a color in the (nonlinear) sRGB color space to a linear representation
	 * of the color via gamma expansion
	 * 
	 * @param color a color in the sRGB color space
	 * @return the color in the linear RGB color space
	 * @see <a href="https://en.wikipedia.org/wiki/Grayscale#Converting_color_to_grayscale">
	 *   Converting color to grayscale
	 * </a>
	 */
	private static int gammaExpand(int color) {
		int[] rgb = getRGB(color);
		float[] comps = new float[N_CHANNELS];

		// convert from range [0, 255] to [0, 1]
		comps[0] = rgb[0] / 255f;
		comps[1] = rgb[1] / 255f;
		comps[2] = rgb[2] / 255f;

		for (int i = 0; i < N_CHANNELS; i++) {
			if (comps[i] <= 0.04045) {
				comps[i] /= 12.92;
			} else {
				comps[i] = (float) Math.pow((comps[i] + 0.055) / 1.055, 2.4);
			}
		}

		return getColor(new int[]{
			(int) (comps[0] * 255),
			(int) (comps[1] * 255),
			(int) (comps[2] * 255)
		});
	}

	/**
	 * Converts a color in the linear RGB color space to a nonlinear representation
	 * of the color via gamma compression
	 * 
	 * @param color a color in the linear sRGB color space
	 * @return the color in the sRGB color space
	 * @see <a href="https://en.wikipedia.org/wiki/Grayscale#Converting_color_to_grayscale">
	 *   Converting color to grayscale
	 * </a>
	 */
	private static int gammaCompress(int color) {
		int[] rgb = getRGB(color);
		float[] comps = new float[N_CHANNELS];

		// convert from range [0, 255] to [0, 1]
		comps[0] = rgb[0] / 255f;
		comps[1] = rgb[1] / 255f;
		comps[2] = rgb[2] / 255f;

		for (int i = 0; i < N_CHANNELS; i++) {
			if (comps[i] <= 0.0031308) {
				comps[i] *= 12.92;
			} else {
				comps[i] = (float) ((1.055 * Math.pow(comps[i], 1 / 2.4)) - 0.055);
			}
		}

		return getColor(new int[]{
			(int) (comps[0] * 255),
			(int) (comps[1] * 255),
			(int) (comps[2] * 255)
		});
	}

	private static byte gammaCompress(byte channel) {
		int u8 = Byte.toUnsignedInt(channel);
		float val = u8 / 255f;
		if (val <= 0.0031308) {
			val *= 12.92;
		} else {
			val = (float) ((1.055 * Math.pow(val, 1 / 2.4)) - 0.055);
		}
		return (byte) (val * 255);
	}

	private static int getColor(int[] rgb) {
		// shift bits into correct byte and mask out anything else
		int r = rgb[0] << 16 & 0xFF0000;
		int g = rgb[1] << 8 & 0x00FF00;
		int b = rgb[2] & 0x0000FF;

		// add max alpha value and OR everything together
		return 0xFF000000 | r | g | b;
	}

	private static int[] getRGB(int color) {
		// shift bits to first byte and mask out anything else
		return new int[]{
            color >> 16 & 0xFF, // red
            color >> 8 & 0xFF,  // green
            color & 0xFF        // blue
        };
	}

	private static int red(int color) {
		return color >> 16 & 0xFF;
	}

	private static int green(int color) {
		return color >> 8 & 0xFF;
	}

	private static int blue(int color) {
		return color & 0xFF;
	}
}

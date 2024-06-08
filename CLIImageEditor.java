import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class CLIImageEditor {

    private static BufferedImage image;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the image file path: ");
        String imagePath = scanner.nextLine();

        File imageFile = new File(imagePath);
        String parentDir = imageFile.getParent();
        String fileName = imageFile.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String baseName = fileName.substring(0, fileName.lastIndexOf("."));

        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("Could not read the image file.");
            return;
        }

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Rotate Image");
            System.out.println("2. Blur Image");
            System.out.println("3. Mirror Image");
            System.out.println("4. Grayscale Image");
            System.out.println("5. Invert Colors");
            System.out.println("6. Crop Image");
            System.out.println("7. Hue Shift");
            System.out.println("8. Save and Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    System.out.print("Enter angle (90, 180, 270): ");
                    int angle = scanner.nextInt();
                    scanner.nextLine(); 
                    image = rotateImage(image, angle);
                    break;
                case 2:
                    image = blurImage(image);
                    break;
                case 3:
                    image = mirrorImage(image);
                    break;
                case 4:
                    image = grayscaleImage(image);
                    break;
                case 5:
                    image = invertImage(image);
                    break;
                case 6:
                    System.out.print("Enter x, y, width, height (comma separated): ");
                    String[] cropParams = scanner.nextLine().split(",");
                    int x = Integer.parseInt(cropParams[0].trim());
                    int y = Integer.parseInt(cropParams[1].trim());
                    int width = Integer.parseInt(cropParams[2].trim());
                    int height = Integer.parseInt(cropParams[3].trim());
                    image = cropImage(image, x, y, width, height);
                    break;
                case 7:
                    System.out.print("Enter hue shift value (between -1.0 and 1.0): ");
                    float hueShift = scanner.nextFloat();
                    scanner.nextLine();
                    image = hueShiftImage(image, hueShift);
                    break;
                case 8:
                    String outputFileName = baseName + "_edited." + fileExtension;
                    String outputPath = new File(parentDir, outputFileName).getPath();
                    try {
                        ImageIO.write(image, fileExtension, new File(outputPath));
                        System.out.println("Image saved successfully to " + outputPath);
                    } catch (IOException e) {
                        System.out.println("Could not save the image.");
                    }
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static BufferedImage rotateImage(BufferedImage img, int angle) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, img.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.toRadians(angle), w / 2, h / 2);
        graphic.drawImage(img, null, 0, 0);
        graphic.dispose();
        return rotated;
    }

    private static BufferedImage blurImage(BufferedImage img) {
        float[] matrix = {
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f,
        };
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
        return op.filter(img, null);
    }

    private static BufferedImage mirrorImage(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage mirrored = new BufferedImage(w, h, img.getType());
        Graphics2D graphic = mirrored.createGraphics();
        graphic.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        graphic.dispose();
        return mirrored;
    }

    private static BufferedImage grayscaleImage(BufferedImage img) {
        BufferedImage grayscale = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    private static BufferedImage invertImage(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                img.setRGB(x, y, col.getRGB());
            }
        }
        return img;
    }

    private static BufferedImage cropImage(BufferedImage img, int x, int y, int width, int height) {
        return img.getSubimage(x, y, width, height);
    }

    private static BufferedImage hueShiftImage(BufferedImage img, float hueShift) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
                hsb[0] = (hsb[0] + hueShift) % 1.0f; 
                col = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                img.setRGB(x, y, col.getRGB());
            }
        }
        return img;
    }
}

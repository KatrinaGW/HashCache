//package com.example.hashcache.models;

//package com.example.hashcache.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageGenerator {

    public static void main(String[] args) throws IOException {
        // example 64-bit binary string
        long binaryString = 0b1101100101010110001001101111101110010100100111110101101100001000L;

        // extract the bits for different parts
        int head = (int) (binaryString & 0b11);
        int eyes = (int) ((binaryString >> 2) & 0b11);
        int body = (int) ((binaryString >> 4) & 0b11);
        int ears = (int) ((binaryString >> 6) & 0b11);

        // load the image files for different parts
        BufferedImage headImg = ImageIO.read(new File("head" + head + ".png"));
        BufferedImage eyesImg = ImageIO.read(new File("eyes" + eyes + ".png"));
        BufferedImage bodyImg = ImageIO.read(new File("body" + body + ".png"));
        BufferedImage earsImg = ImageIO.read(new File("ears" + ears + ".png"));

        // create a new image of appropriate size
        int width = Math.max(headImg.getWidth(), earsImg.getWidth());
        int height = headImg.getHeight() + eyesImg.getHeight() + bodyImg.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // draw different parts onto the final image
        int x = (width - headImg.getWidth()) / 2; // center the head image horizontally
        int y = 0;
        image.createGraphics().drawImage(headImg, x, y, null);

        x = (width - eyesImg.getWidth()) / 2; // center the eyes image horizontally
        y = headImg.getHeight() - eyesImg.getHeight() / 2;
        image.createGraphics().drawImage(eyesImg, x, y, null);

        x = (width - bodyImg.getWidth()) / 2; // center the body image horizontally
        y = headImg.getHeight() + eyesImg.getHeight();
        image.createGraphics().drawImage(bodyImg, x, y, null);

        x = 0; // align the left ear image to the left edge of the image
        y = 0; // align the left ear image to the top edge of the image
        image.createGraphics().drawImage(earsImg, x, y, null);

        x = width - earsImg.getWidth(); // align the right ear image to the right edge of the image
        y = 0; // align the right ear image to the top edge of the image
        image.createGraphics().drawImage(earsImg, x, y, null);

        // save the final image to file
        ImageIO.write(image, "png", new File("generated_image.png"));
    }

}
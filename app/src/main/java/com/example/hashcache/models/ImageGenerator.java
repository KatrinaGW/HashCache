package com.example.hashcache.models;

package com.example.hashcache.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageGenerator {

    public static void main(String[] args) throws Exception {
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
        int width = headImg.getWidth();
        int height = headImg.getHeight() + eyesImg.getHeight() + bodyImg.getHeight() + earsImg.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // combine different parts into the final image
        int y = 0;
        image.createGraphics().drawImage(headImg, 0, y, null);
        y += headImg.getHeight();
        image.createGraphics().drawImage(eyesImg, 0, y, null);
        y += eyesImg.getHeight();
        image.createGraphics().drawImage(bodyImg, 0, y, null);
        y += bodyImg.getHeight();
        image.createGraphics().drawImage(earsImg, 0, y, null);

        // save the final image to file
        ImageIO.write(image, "png", new File("generated_image.png"));
    }
}
package com.example.hashcache.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageGenerator {

    public static void main(String[] args) throws Exception {
        // example 64-bit binary string
        long binaryString = 0b1101100101010110001001101111101110010100100111110101101100001000L;

        int R = (int) ((id64 >> 32) & 0xFF);
        int G = (int) ((id64 >> 16) & 0xFF);
        int B = (int) (id64 & 0xFF);

        // calculate body, head, eyes, and ears based on bits
        int body = (int) ((id64 >> 4) & 0x3);
        int head = (int) ((id64 >> 6) & 0xF);
        int eyes = (int) ((id64 >> 10) & 0xF);
        int ears = (int) ((id64 >> 14) & 0x3);

        // load the image files
        BufferedImage bodyImg = null, headImg = null, eyesImg = null, earsImg = null;
        try {
            bodyImg = ImageIO.read(new File(String.format("images/body_%d.png", body)));
            headImg = ImageIO.read(new File(String.format("images/head_%d.png", head)));
            eyesImg = ImageIO.read(new File(String.format("images/eyes_%d.png", eyes)));
            earsImg = ImageIO.read(new File(String.format("images/ears_%d.png", ears)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a new BufferedImage object and draw the images
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(bodyImg, 0, 0, null);
        g.drawImage(headImg, 0, 0, null);
        g.drawImage(eyesImg, 0, 0, null);
        g.drawImage(earsImg, 0, 0, null);
        g.dispose();

        // save the image to a file
        try {
            File outputfile = new File(String.format("images/generated_%d.png", id64));
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
}
package org.recognition.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.recognition.config.AppConfig;
import org.recognition.config.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class Recognition {
    @Autowired
    static Environment env;
    public static String recognize(byte[] binaryFile, String language) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(binaryFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

        ITesseract _tesseract = new Tesseract();
        _tesseract.setDatapath(env.getProperty("project_dir") + "src\\main\\resources\\tessdata");
        _tesseract.setLanguage(language);

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            // Create a temp image file
            File tempFile = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bufferedImage, "png", tempFile);

            String result = _tesseract.doOCR(tempFile);
            out.append(result);

            // Delete temp file
            tempFile.delete();

        }
        return out.toString();
    }

    public static String findKeyWords(String input) {
        // минус пунктуация и нижний реестр
        input = input.replaceAll("[^a-zA-Zа-яА-Я-\\s]", "").toLowerCase();

        // делим текст на слова
        String[] words = input.split("\\s+");


        // считаем кол-во встреч каждого слова
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : words) {
            if (word.length() > 5) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        // создаем список пар слово - кол-во встреч, и сортируем его по убыванию
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCounts.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // создаем список из 7 самых частых слов
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(7, entries.size()); i++) {
            result.add(entries.get(i).getKey());
        }
        return String.join(" ", result);
    }
}

package org.recognition.utils;

import org.apache.commons.io.FilenameUtils;

public class Validation {
    public static String makeNameValid(String name) {
        String ext = FilenameUtils.getExtension(name);
        if (ext.equals(""))
            name += ".pdf";
        name = name.strip().replace(" ", "_");
        return name;
    }
}

package com.dtalks.dtalks.base.validation;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;

public class FileValidation {

    public static void imageValidation(String fileName) {
        String format = getFormat(fileName);
        if (!(format.equals(".jpg") || format.equals(".png"))) {
            throw new CustomException(ErrorCode.FILE_FORMAT_ERROR, "파일 형식이 올바르지 않습니다.");
        }
    }

    public static String getFormat(String fileName) {
        String s[] = fileName.split("[.]");
        return "." + s[s.length-1].toLowerCase();
    }
}

package io.github.erdos.stencil.test;

import io.github.erdos.stencil.impl.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZipHelperTest {


    public static void main(String... args) throws IOException {

        InputStream fis = new FileInputStream(new File("/home/erdos/sablon1.docx"));
        ZipHelper.unzipStreamIntoDirectory(fis, new File("/home/erdos/xxx"));
    }
}

package in.gov.abdm.abha.enrollmentdb.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
@UtilityClass
public class ImageUtils {

    private static final String EXCEPTION_OCCURRED_WHILE_COMPRESSING_THE_IMAGE = "Exception occured while compressing the image. ";
    private static final String ORIGINAL_UNCOMPRESSED_IMAGE_KB = "original uncompressed image {} kb";
    private static final String COMPRESSED_IMAGE_SIZE_KB = "compressed image size{} kb";
    private static final String EXCEPTION_OCCURRED_WHILE_DECOMPRESSING_THE_IMAGE = "Exception occured while decompressing the image. ";
    private static final String ORIGINAL_COMPRESSED_IMAGE_KB = "original compressed image {} kb";
    private static final String DECOMPRESSED_IMAGE_SIZE_KB = "decompressed image size {} kb";

    public static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (Exception exp) {
            log.error(EXCEPTION_OCCURRED_WHILE_COMPRESSING_THE_IMAGE, exp);
        }
        byte[] output = outputStream.toByteArray();
        log.trace(ORIGINAL_UNCOMPRESSED_IMAGE_KB, data.length / 1024);
        log.trace(COMPRESSED_IMAGE_SIZE_KB, output.length / 1024);
        return output;
    }

    @SuppressWarnings("java:S135")
    /**
     * removing breaks can fail the functionality
     */
    public static byte[] decompress(byte[] data) {

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = 0;
            try {
                count = inflater.inflate(buffer);
                if (count <= 0)
                    break;
            } catch (DataFormatException e) {
                break;
            }
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException exp) {
            log.error(EXCEPTION_OCCURRED_WHILE_DECOMPRESSING_THE_IMAGE, exp);
        }
        byte[] output = outputStream.toByteArray();
        log.trace(ORIGINAL_COMPRESSED_IMAGE_KB, data.length / 1024);
        log.trace(DECOMPRESSED_IMAGE_SIZE_KB, output.length / 1024);
        return output;
    }
}
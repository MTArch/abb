package in.gov.abdm.abha.enrollmentdbtests.utilities;

import in.gov.abdm.abha.enrollmentdb.exception.GenericExceptionMessage;
import in.gov.abdm.abha.enrollmentdb.utilities.ImageUtils;
import liquibase.pro.packaged.M;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
public class ImageUtilsTests {
    @Mock
    Inflater inflater;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        byte[] data = new byte[1232];
    }
    @AfterEach
    void tearDown() {

    }
    @Test
    public void compressTests() {
        byte[] data = new byte[1232];
        byte[] result = ImageUtils.compress(data);
        Assert.assertEquals(result.length, 18);
    }
    @org.junit.Test()
    public void decompressTests() throws DataFormatException {
        String s = "a";
        byte[] data = s.getBytes();
        String sbuffer = "nameghjkjh";
        byte[] buffer = sbuffer.getBytes();
        Mockito.when(Mockito.mock(Inflater.class).inflate(any(),anyInt(),anyInt())).thenReturn(1);
        Mockito.when(Mockito.mock(Inflater.class).inflate(buffer)).thenReturn(1);

        byte[] result = ImageUtils.decompress(data);
        Assert.assertEquals(result.length,0);
    }
}

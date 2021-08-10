package com.github.saftsau.xrel4j;

import com.github.saftsau.xrel4j.release.scene.Release;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class XrelTest {
    
    private final Xrel xrel = new Xrel();
    
    @Test
    void testRelease() {
        final Release release = xrel.getReleaseInfoId("839488661e8f92");
        Assertions.assertNotNull(release);
        Assertions.assertEquals(1576798455, release.getTime());
        Assertions.assertEquals("v+XmWHgO//104a29aaLMDhDvxQ3sOrYSrastW484hhk=", hash(release.getDirname().getBytes(StandardCharsets.UTF_8)));
    }
    
    public static String hash(byte[] data) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
}

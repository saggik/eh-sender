package com.ge.predix.cyber.csam.ehsender.config;

import java.lang.reflect.Field;

import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//import static com.ge.predix.cyber.util.Exceptions.wrapCheckedException;

/**
 * Created by taitz.
 */
@Slf4j
public class NettyConfig {

    @Getter
    private static final NettyConfig instance = new NettyConfig();
    
    private NettyConfig() {
    }

    /**
     * Call this method at the beginning of application startup to ensure that netty configuration is loaded before
     * 3rd party libraries load netty. Once netty is loaded, its configuration is immutable.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void load() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        setMaxDirectMemory();
    }

    private void setMaxDirectMemory() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String memEnv = System.getenv("MAX_DIRECT_MEMORY");
        long maxDirectMemory = memEnv == null ? -1 : Long.parseLong(memEnv);
        String nettyMaxMemPropKey = "io.netty.maxDirectMemory";
        log.info("setting {} to {}", nettyMaxMemPropKey, maxDirectMemory);
        System.setProperty(nettyMaxMemPropKey, String.valueOf(maxDirectMemory));
        verifyMaxMemorySet(maxDirectMemory);
    }

    private void verifyMaxMemorySet(long expectedMaxMem) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        long maxMem = getMaxDirectMemory();
        if (maxMem != expectedMaxMem && expectedMaxMem >= 0) {
            throw new AssertionError("Failed to set netty max memory. Expected " + expectedMaxMem + " actual " + maxMem);
        }
    }

    long getMaxDirectMemory() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String memoryLimitFieldName = "DIRECT_MEMORY_LIMIT";
        Field field = PlatformDependent.class.getDeclaredField(memoryLimitFieldName);
        field.setAccessible(true);
        long maxMem = field.getLong(null);
        log.info("{} is {}", memoryLimitFieldName, maxMem);
        return maxMem;
    }

}

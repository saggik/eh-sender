package com.ge.predix.cyber.csam.ehsender.entities;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import org.springframework.stereotype.Component;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

@Component
public class MemoryMonitor {

	private static final String DIRECT_MEMORY_FIELD = "DIRECT_MEMORY_COUNTER";
	
    private LongSupplier getDirectMemoryCounter() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = PlatformDependent.class.getDeclaredField(DIRECT_MEMORY_FIELD);
        field.setAccessible(true);
        AtomicLong counter = (AtomicLong) field.get(null);
        return counter == null ? () -> -1 : counter::get;
    }

    public long getDirectMemoryUsage() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	LongSupplier directMemory = getDirectMemoryCounter();
    	long directMemoryCount = directMemory.getAsLong();
    	return directMemoryCount;
    }
    
    public String getDirectMemoryUsageOfMax() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	String nettyMem = "io.netty.maxDirectMemory";
        long maxDirectMemory = SystemPropertyUtil.getLong(nettyMem, -1);
        
        return String.format("Used %d out of %d", getDirectMemoryUsage(), maxDirectMemory);
    }
}
package me.longnh.cryptodatastation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


@Slf4j
public class CdsBeanUtil {
    private CdsBeanUtil() {}

    public static <T> T clone (Object source, Class<T> dest) {
        T result;
        try {
            result = dest.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, result);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error cloning object", e);
            throw new RuntimeException(e);
        }
        return result;
    }
}

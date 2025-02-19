package me.modmuss50.optifabric.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.impl.lib.tinyremapper.IMappingProvider;

public class RemappingUtils {

    private static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();
    private static final String INTERMEDIARY = "intermediary";
    private static final Pattern CLASS_FINDER = Pattern.compile("Lnet\\/minecraft\\/([^;]+);");

    public static String getClassName(String className) {
        return fromIntermediaryDot(className).replaceAll("\\.", "/");
    }

    private static String fromIntermediaryDot(String className) {
        return RESOLVER.mapClassName(INTERMEDIARY, "net.minecraft." + className);
    }

    public static IMappingProvider.Member mapMethod(String owner, String name, String desc) {
        return new IMappingProvider.Member(getClassName(owner), getMethodName(owner, name, desc), mapMethodDescriptor(desc));
    }

    public static String getMethodName(String owner, String methodName, String desc) {
        return RESOLVER.mapMethodName(INTERMEDIARY, "net.minecraft." + owner, methodName, desc);
    }

    public static String mapMethodDescriptor(String desc) {
        StringBuffer buf = new StringBuffer();

        Matcher matcher = CLASS_FINDER.matcher(desc);
        while (matcher.find()) {
            matcher.appendReplacement(buf, Matcher.quoteReplacement('L' + getClassName(matcher.group(1)) + ';'));
        }

        return matcher.appendTail(buf).toString();
    }
}
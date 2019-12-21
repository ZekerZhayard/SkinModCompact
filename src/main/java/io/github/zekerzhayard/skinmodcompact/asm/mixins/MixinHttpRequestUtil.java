package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpRequestUtil;
import io.github.zekerzhayard.skinmodcompact.config.ModConfig;
import io.github.zekerzhayard.skinmodcompact.utils.MinecraftUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = HttpRequestUtil.class,
    remap = false
)
public abstract class MixinHttpRequestUtil {
    @Shadow
    public static HttpRequestUtil.HttpResponce makeHttpRequest(HttpRequestUtil.HttpRequest request, int redirectTime) {
        return null;
    }

    @ModifyArg(
        method = "Lcustomskinloader/utils/HttpRequestUtil;makeHttpRequest(Lcustomskinloader/utils/HttpRequestUtil$HttpRequest;I)Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/net/URI;<init>(Ljava/lang/String;)V"
        ),
        require = 1
    )
    private static String modifyArg$makeHttpRequest$0(String str) throws MalformedURLException, URISyntaxException {
        URL url = new URL(str);
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null).toString();
    }

    @Redirect(
        method = "Lcustomskinloader/utils/HttpRequestUtil;makeHttpRequest(Lcustomskinloader/utils/HttpRequestUtil$HttpRequest;I)Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/net/URL;openConnection()Ljava/net/URLConnection;"
        ),
        require = 1
    )
    private static URLConnection redirect$makeHttpRequest$0(URL url) throws IOException {
        return url.openConnection(MinecraftUtils.getProxy());
    }

    @Redirect(
        method = "Lcustomskinloader/utils/HttpRequestUtil;makeHttpRequest(Lcustomskinloader/utils/HttpRequestUtil$HttpRequest;I)Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;",
        at = @At(
            value = "INVOKE",
            target = "Lcustomskinloader/utils/HttpRequestUtil;loadFromCache(Lcustomskinloader/utils/HttpRequestUtil$HttpRequest;Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;)Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;",
            ordinal = 2
        ),
        require = 1
    )
    private static HttpRequestUtil.HttpResponce redirect$makeHttpRequest$1(HttpRequestUtil.HttpRequest $request, HttpRequestUtil.HttpResponce responce, HttpRequestUtil.HttpRequest request, int redirectTime) throws InterruptedException {
        CustomSkinLoader.logger.debug("[SkinModCompact] Retry to make request.");
        Thread.sleep(ModConfig.retryTime);
        return makeHttpRequest(request, redirectTime);
    }
}

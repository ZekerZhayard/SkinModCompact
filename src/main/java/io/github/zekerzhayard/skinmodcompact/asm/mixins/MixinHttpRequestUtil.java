package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import customskinloader.utils.HttpRequestUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    value = HttpRequestUtil.class,
    remap = false
)
public abstract class MixinHttpRequestUtil {
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
}

package io.github.zekerzhayard.skinmodcompact.utils;

import java.lang.reflect.Field;

import com.google.common.base.Strings;
import customskinloader.profile.UserProfile;
import org.apache.commons.lang3.StringUtils;

public class UserProfileUtils {
    public static boolean mix(UserProfile mixedProfile, UserProfile profile) {
        if (mixedProfile == null || profile == null) {
            return false;
        }

        boolean mix = false;
        if (StringUtils.isEmpty(mixedProfile.skinUrl)) {
            mixedProfile.skinUrl = profile.skinUrl;
            mixedProfile.model = profile.model;
            mix = true;
        }

        if (StringUtils.isEmpty(mixedProfile.capeUrl)) {
            mixedProfile.capeUrl = profile.capeUrl;
            mix = true;
        }

        if (StringUtils.isEmpty(mixedProfile.elytraUrl)) {
            mixedProfile.elytraUrl = profile.elytraUrl;
            mix = true;
        }
        return mix;
    }

    public static void mix(UserProfile mixedProfile, UserProfile profile, long[] indicators, int priority) {
        if (mixedProfile == null || profile == null)
            return;
        if (profile.hasSkinUrl() && indicators[1] >>> priority << priority != indicators[1]) {
            mixedProfile.skinUrl = profile.skinUrl;
            mixedProfile.model = profile.model;
            indicators[1] = indicators[1] >>> priority << priority;
        }
        if (UserProfileUtils.hasCapeUrl(profile) && indicators[2] >>> priority << priority != indicators[2]) {
            mixedProfile.capeUrl = profile.capeUrl;
            indicators[2] = indicators[2] >>> priority << priority;
        }
        if (UserProfileUtils.hasElytraUrl(profile) && indicators[3] >>> priority << priority != indicators[3]) {
            mixedProfile.elytraUrl = profile.elytraUrl;
            indicators[3] = indicators[3] >>> priority << priority;
        }
    }

    public static boolean hasCapeUrl(UserProfile userProfile) {
        return userProfile != null && StringUtils.isNotEmpty(userProfile.capeUrl);
    }

    public static boolean hasElytraUrl(UserProfile userProfile) {
        return userProfile != null && StringUtils.isNotEmpty(userProfile.elytraUrl);
    }

    public static boolean isEffective(boolean forceLoadAllTextures, long[] indicators) {
        return (forceLoadAllTextures && (indicators[0] | indicators[1]) == indicators[0] && (indicators[0] | indicators[2]) == indicators[0])
            ||(!forceLoadAllTextures && (indicators[0] | indicators[1]) == indicators[0] || (indicators[0] | indicators[2]) == indicators[0]);
    }
    
    public static UserProfile clone(UserProfile oldProfile) {
        UserProfile newProfile = new UserProfile();
        for (Field field : UserProfile.class.getFields()) {
            try {
                field.set(newProfile, field.get(oldProfile));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return newProfile;
    }
    
    public static boolean isEquals(UserProfile profile1, UserProfile profile2) {
        for (Field field : UserProfile.class.getFields()) {
            boolean isEquals = false;
            try {
                isEquals = Strings.nullToEmpty((String) field.get(profile1)).equals(Strings.nullToEmpty((String) field.get(profile2)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isEquals) {
                return false;
            }
        }
        return true;
    }
}

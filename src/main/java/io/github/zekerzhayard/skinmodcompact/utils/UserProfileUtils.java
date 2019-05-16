package io.github.zekerzhayard.skinmodcompact.utils;

import org.apache.commons.lang3.StringUtils;

import customskinloader.profile.UserProfile;

public class UserProfileUtils {
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
}

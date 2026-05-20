package chenmod.patches;

import chenmod.ChenMod;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

import java.lang.reflect.Field;

public class CustomBGMPatch {

    /** 只劫持我们 mod 的路径，避免误判原生 .ogg */
    private static boolean isCustom(String key) {
        return key != null && key.startsWith("chenmod/");
    }

    // -------------------------------------------------------------------------
    // Patch 1：劫持 TempMusic.getSong（事件、战斗临时 BGM）
    // -------------------------------------------------------------------------
    @SpirePatch(clz = TempMusic.class, method = "getSong")
    public static class PatchTempMusic {
        @SpirePrefixPatch
        public static SpireReturn<Music> Prefix(TempMusic __instance, String key) {

            if (isCustom(key)) {
                ChenMod.logger.info("[CustomBGM] TempMusic 播放自定义音乐: " + key);
                return SpireReturn.Return(MainMusic.newMusic(key));
            }

            return SpireReturn.Continue();
        }
    }

    // -------------------------------------------------------------------------
    // Patch 2：劫持 MainMusic 构造函数（主 BGM）
    // -------------------------------------------------------------------------
    @SpirePatch(clz = MainMusic.class, method = SpirePatch.CONSTRUCTOR)
    public static class PatchMainMusic {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(MainMusic __instance, String key) {

            if (isCustom(key)) {
                ChenMod.logger.info("[CustomBGM] MainMusic 播放自定义音乐: " + key);

                Music music = MainMusic.newMusic(key);

                try {
                    Field f = MainMusic.class.getDeclaredField("music");
                    f.setAccessible(true);
                    f.set(__instance, music);

                    music.setLooping(true);
                    music.setVolume(0f);
                    music.play();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }
}

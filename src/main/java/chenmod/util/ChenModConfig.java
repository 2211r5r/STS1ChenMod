package chenmod.util;

import basemod.EasyConfigPanel;
import chenmod.ChenMod;

public class ChenModConfig extends EasyConfigPanel {

    // 自动生成一个 Toggle 勾选框
    public static boolean TIPS = false;
    public static boolean ADD_EVENT_TOWERFIERCE = true;
    public static boolean ADD_FROST_NOVA = true;
    public static boolean ADD_MEPHISO_AND_FAUST = true;
    public static boolean ADD_BULDROKKASTEE = true;
    public static boolean ADD_MEPHISO_SINGER = true;
    public static boolean ADD_TALULAH = true;
    public static boolean REMOVE_ORANGEAL_BOSS = true;
    public static boolean ORANGEAL_RELIC = true;
    public static boolean DEBUG_MODE = false;

    public ChenModConfig() {
        super(ChenMod.modID, ChenMod.makeID("ChenModConfig"));
    }
}
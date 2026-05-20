package chenmod.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static chenmod.ChenMod.audioPath;

public class Sounds {

    private static final Random rng = new Random();

    public static String attackCardEffect = audioPath("/ChenCharacter/AttackCardEffect.wav");   // 普通陈普攻音效

    public static String attackEffect2 = audioPath("/ChenCharacter/AttackEffect_2.wav");
    public static String attackEffect3 = audioPath("/ChenCharacter/AttackEffect_3.wav");

    public static String shockAttackEffect = audioPath("/ChenCharacter/AttackEffect_4.wav");
    public static String doubleAttackEffect = audioPath("/ChenCharacter/AttackEffect_5.wav");

    public static String deployEffect = audioPath("/ChenCharacter/DeployEffect.wav");
    public static String deployVoice = audioPath("/ChenCharacter/DeployVoice.wav");
    public static String qiaoJiEffect = audioPath("/ChenCharacter/QiaoJiEffect.wav");
    public static String slashEffect = audioPath("/ChenCharacter/SlashEffect.wav");


    public static String jueYingEffect_1 = audioPath("/ChenCharacter/JueYingEffect_1.wav");
    public static String jueYingEffect_2 = audioPath("/ChenCharacter/JueYingEffect_2.wav");
    public static String jueYingEffect_3 = audioPath("/ChenCharacter/JueYingEffect_3.wav");

    public static String jueYingVoice_1 = audioPath("/ChenCharacter/JueYingVoice_1.wav");
    public static String jueYingVoice_2 = audioPath("/ChenCharacter/JueYingVoice_2.wav");

    public static String attackVoice_1 = audioPath("/ChenCharacter/AttackVoice_1.wav");
    public static String attackVoice_2 = audioPath("/ChenCharacter/AttackVoice_2.wav");
    public static String attackVoice_3 = audioPath("/ChenCharacter/AttackVoice_3.wav");
    public static String attackVoice_4 = audioPath("/ChenCharacter/AttackVoice_4.wav");

    public static String attackVoice_5 = audioPath("/ChenCharacter/AttackVoice_5.wav");
    public static String attackVoice_6 = audioPath("/ChenCharacter/AttackVoice_6.wav");
    public static String attackVoice_7 = audioPath("/ChenCharacter/AttackVoice_7.wav");
    public static String attackVoice_8 = audioPath("/ChenCharacter/AttackVoice_8.wav");

    public static String attackVoice_9 = audioPath("/ChenCharacter/AttackVoice_9.wav");
    public static String attackVoice_10 = audioPath("/ChenCharacter/AttackVoice_10.wav");
    public static String attackVoice_11= audioPath("/ChenCharacter/AttackVoice_11.wav");
    public static String attackVoice_12= audioPath("/ChenCharacter/AttackVoice_12.wav");

    public static String powerVoice_1 = audioPath("/ChenCharacter/PowerVoice_1.wav");
    public static String powerVoice_2 = audioPath("/ChenCharacter/PowerVoice_2.wav");
    public static String powerVoice_3 = audioPath("/ChenCharacter/PowerVoice_3.wav");

    public static String powerVoice_4 = audioPath("/ChenCharacter/PowerVoice_4.wav");
    public static String powerVoice_5 = audioPath("/ChenCharacter/PowerVoice_5.wav");
    public static String powerVoice_6 = audioPath("/ChenCharacter/PowerVoice_6.wav");

    public static String powerVoice_7 = audioPath("/ChenCharacter/PowerVoice_7.wav");
    public static String powerVoice_8 = audioPath("/ChenCharacter/PowerVoice_8.wav");
    public static String powerVoice_9 = audioPath("/ChenCharacter/PowerVoice_9.wav");

    public static String skillVoice_1 = audioPath("/ChenCharacter/SkillVoice_1.wav");
    public static String skillVoice_2 = audioPath("/ChenCharacter/SkillVoice_2.wav");
    public static String skillVoice_3 = audioPath("/ChenCharacter/SkillVoice_3.wav");

    public static String skillVoice_4 = audioPath("/ChenCharacter/SkillVoice_4.wav");
    public static String skillVoice_5 = audioPath("/ChenCharacter/SkillVoice_5.wav");
    public static String skillVoice_6 = audioPath("/ChenCharacter/SkillVoice_6.wav");

    public static String haiSiPowerEffect = audioPath("/ChenCharacter/HaiSiPowerEffect.wav");

    public static String cooperationActionVoice = audioPath("/ChenCharacter/CooperationActionVoice.wav");

    public static String tianKuiVoice = audioPath("/ChenCharacter/TianKuiVoice.wav");

    public static String towerFierceEventBGM = audioPath("Bgm/TowerFierce.ogg");
    public static String bossBuldrokkasteeBGM = audioPath("Bgm/Unshakability.ogg");
    public static String bossBuldrokkasteeBGM2 = audioPath("Bgm/Defy Death.ogg");

    public static String beforeTalulahBGM = audioPath("Bgm/Chernoberg's rapid march.ogg");

    public static String TalulahBGM_1 = audioPath("Bgm/TalulahBGM_1.ogg");
    public static String TalulahBGM_2 = audioPath("Bgm/TalulahBGM_2.ogg");
    public static String Talulah_FireRain = audioPath("Bgm/Talulah_FireRain.ogg");

    public static String FrostNovaBGM_1 = audioPath("Bgm/FrostNova1.ogg");
    public static String FrostNovaBGM_2 = audioPath("Bgm/FrostNova2.ogg");
    public static String FrostNovaBGM_3 = audioPath("Bgm/FrostNova3.ogg");

    public static String MephistoSingerBGM = audioPath("Bgm/Lullabye.ogg");
    public static String MephistoFaustBGM = audioPath("Bgm/MephistoFaust5-10.ogg");

    public static String attack_1_Effect_Buldrokkastee=audioPath("/Monsters/Buldrokkastee/Attack_1_Effect.wav");
    public static String attack_2_Effect_Buldrokkastee=audioPath("/Monsters/Buldrokkastee/Attack_2_Effect.wav");
    public static String skill_Effect_Buldrokkastee=audioPath("/Monsters/Buldrokkastee/Skill_Effect.wav");
    public static String revive_1_Effect_Buldrokkastee=audioPath("/Monsters/Buldrokkastee/revive_1_Effect.wav");
    public static String revive_3_Effect_Buldrokkastee=audioPath("/Monsters/Buldrokkastee/revive_3_Effect.wav");


    public static final List<String> attackVoicePool = new ArrayList<>();

    public static final List<String> attackVoicePool2 = new ArrayList<>();  // 假日威龙陈

    public static final List<String> attackVoicePool3 = new ArrayList<>();  // 赤刃明霄陈

    public static final List<String> skillVoicePool = new ArrayList<>();

    public static final List<String> skillVoicePool2 = new ArrayList<>();   // 假日威龙陈

    public static final List<String> skillVoicePool3 = new ArrayList<>();   // 赤刃明霄陈

    public static final List<String> powerVoicePool = new ArrayList<>();

    public static final List<String> powerVoicePool2 = new ArrayList<>();   // 假日威龙陈

    public static final List<String> powerVoicePool3 = new ArrayList<>();   // 赤刃明霄陈

    public static final List<String> jueYingVoicePool = new ArrayList<>();


    static {
        attackVoicePool.add(attackVoice_1);
        attackVoicePool.add(attackVoice_2);
        attackVoicePool.add(attackVoice_3);
        attackVoicePool.add(attackVoice_4);

        attackVoicePool2.add(attackVoice_5); // 假日威龙陈
        attackVoicePool2.add(attackVoice_6); // 假日威龙陈
        attackVoicePool2.add(attackVoice_7); // 假日威龙陈
        attackVoicePool2.add(attackVoice_8); // 假日威龙陈

        attackVoicePool3.add(attackVoice_9);    // 赤刃明霄陈
        attackVoicePool3.add(attackVoice_10);   // 赤刃明霄陈
        attackVoicePool3.add(attackVoice_11);   // 赤刃明霄陈
        attackVoicePool3.add(attackVoice_12);   // 赤刃明霄陈

        powerVoicePool.add(powerVoice_1);
        powerVoicePool.add(powerVoice_2);
        powerVoicePool.add(powerVoice_3);

        powerVoicePool2.add(powerVoice_4);   // 假日威龙陈
        powerVoicePool2.add(powerVoice_5);   // 假日威龙陈
        powerVoicePool2.add(powerVoice_6);   // 假日威龙陈

        powerVoicePool3.add(powerVoice_7);  // 赤刃明霄陈
        powerVoicePool3.add(powerVoice_8);  // 赤刃明霄陈
        powerVoicePool3.add(powerVoice_9);  // 赤刃明霄陈

        skillVoicePool.add(skillVoice_1);
        skillVoicePool.add(skillVoice_2);
        skillVoicePool.add(skillVoice_3);

        skillVoicePool2.add(skillVoice_4);   // 假日威龙陈
        skillVoicePool2.add(skillVoice_5);   // 假日威龙陈
        skillVoicePool2.add(skillVoice_6);   // 假日威龙陈

        skillVoicePool3.add(powerVoice_7);
        skillVoicePool3.add(powerVoice_8);
        skillVoicePool3.add(powerVoice_9);

        jueYingVoicePool.add(jueYingVoice_1);
        jueYingVoicePool.add(jueYingVoice_2);
    }

    public static String getRandomVoiceString(List<String> voicePool) {
        return voicePool.get(rng.nextInt(voicePool.size()));
    }
}

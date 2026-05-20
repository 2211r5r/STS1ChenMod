package chenmod;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.EasyConfigPanel;
import basemod.interfaces.*;
import chenmod.cards.BaseCard;
import chenmod.character.ChenCharacter;
import chenmod.events.TowerFierceEvent;
import chenmod.monsters.*;
import chenmod.relics.BaseRelic;
import chenmod.relics.ChiXiaoRelic;
import chenmod.util.*;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class ChenMod implements
        EditRelicsSubscriber,
        EditCardsSubscriber,
        EditCharactersSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        AddAudioSubscriber,
        PostInitializeSubscriber,
        StartGameSubscriber,
        PreStartGameSubscriber,
        PostUpdateSubscriber,
        OnStartBattleSubscriber,
        PostDungeonInitializeSubscriber
{
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static boolean firstQuestionMarkAlreadyTriggered = false;

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new ChenMod();

        ChenCharacter.Meta.registerColor();
    }

    public ChenMod() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }


    @Override
    public void receivePreStartGame() {
        logger.info("游戏启动前：已重置事件标记");
    }

    @Override
    public void receiveStartGame() {

    }

    @Override
    public void receivePostDungeonInitialize() {

        if (AbstractDungeon.id.equals(Exordium.ID) && ChenModConfig.ADD_EVENT_TOWERFIERCE) {

            // 在 第一层 填充大量 【杀戮之塔】 事件，拿取以后，获得支线任务.
            for(int i = 0; i < 100;i++){
                AbstractDungeon.eventList.add(TowerFierceEvent.ID);
            }
        }

        ChenMod.logger.info("【BossList.size】:{}; 【BossKey】:{}; 【BossList】:{}", AbstractDungeon.bossList.size(), AbstractDungeon.bossKey, AbstractDungeon.bossList);
        ChenMod.logger.info("【EventList.size】:{}; 【EventList】:{}", AbstractDungeon.eventList.size(), AbstractDungeon.eventList);

    }

    @Override
    public void receivePostInitialize() {

        //This loads the image used as an icon in the in-game mods menu.
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        //Set up the mod information displayed in the in-game mods menu.
        //The information used is taken from your pom.xml file.

        //If you want to set up a config panel, that will be done here.
        //You can find information about this on the BaseMod wiki page "Mod Config and Panel".
        EasyConfigPanel myConfig = new ChenModConfig();
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, myConfig);


        // 注册事件与怪物

        BaseMod.addEvent(TowerFierceEvent.ID, TowerFierceEvent.class, Exordium.ID);

        BaseMod.addMonster(Buldrokkastee.ID, () -> new Buldrokkastee(0.0f, 0.0f));

        BaseMod.addMonster(ShieldGuard.ID, () -> new ShieldGuard(0.0f, 0.0f));

        BaseMod.addMonster(Talulah.ID, () -> new Talulah(0.0f, 0.0f));

        BaseMod.addMonster(FrostNova.ID, () -> new FrostNova(0.0f, 0.0f));

        BaseMod.addMonster(MephistoSinger.ID, () -> new MephistoSinger(0.0f, 0.0f));

        BaseMod.addMonster("White Rabbit",() -> new MonsterGroup(new AbstractMonster[] {
                new SnowMonsterTeamIceBreaker(-250.0f, 0.0f),
                new FrostNova(-50.0f, 0.0f),
                new SnowMonsterTeamCaster(150.0f , 0.0f),
        }));


        BaseMod.addMonster("Death of a Patriot", () -> new MonsterGroup(new AbstractMonster[] {
                new Buldrokkastee(-250.0f, 0.0f),
                new ShieldGuard(-50.0f , 0.0f),
                new ShieldGuard(150.0f, 0.0f)
        }));

        BaseMod.addMonster("Scorching Sun",() -> new Talulah(0.0f, 0.0f));

        BaseMod.addMonster("Mephisto Singer",() -> new MephistoSinger(0.0f, 0.0f));

        BaseMod.addMonster("Mephisto Faust",() -> new MonsterGroup(new AbstractMonster[] {
                new ZombieScavenger(-250.0f, 0.0f),
                new Mephisto(-50.0f, 0.0f),
                new Faust(150.0f , 0.0f)
        }));


        // 注册 Boss 图标
        BaseMod.addBoss(TheBeyond.ID, "Death of a Patriot",
                "chenmod/images/ui/boss/Buldrokkastee.png",
                "chenmod/images/ui/bossoutline/Buldrokkastee.png");

        BaseMod.addBoss(TheBeyond.ID, "Mephisto Singer",
                "chenmod/images/ui/boss/MephistoSinger.png",
                "chenmod/images/ui/bossoutline/MephistoSinger.png");

        BaseMod.addBoss(TheEnding.ID, "Scorching Sun",
                "chenmod/images/ui/boss/Talulah.png",
                "chenmod/images/ui/bossoutline/Talulah.png");

        BaseMod.addBoss(TheCity.ID, "White Rabbit",
                "chenmod/images/ui/boss/FrostNova.png",
                "chenmod/images/ui/bossoutline/FrostNova.png");

        BaseMod.addBoss(TheCity.ID, "Mephisto Faust",
                "chenmod/images/ui/boss/MephistoFaust.png",
                "chenmod/images/ui/bossoutline/MephistoFaust.png");


        ChenMod.logger.info("[ChenMod] Boss 图标注册完成");

    }



    /*----------Localization----------*/

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "zhs";

    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    @Override
    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage); //no exception catching for default localization; you better have at least one that works.
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                localizationPath(lang, "MonsterStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords()
    {
        logger.info("开始加载关键词...");
        Gson gson = new Gson();
        try {
            // 读取JSON文件
            String jsonPath = localizationPath(defaultLanguage, "Keywords.json");
            logger.info("读取关键词文件路径：" + jsonPath);
            String json = Gdx.files.internal(jsonPath).readString(StandardCharsets.UTF_8.name());
            logger.info("JSON文件读取成功，内容长度：" + json.length());

            // 解析JSON
            KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
            logger.info("JSON解析完成，关键词数量：" + (keywords == null ? "null" : keywords.length));

            if (keywords != null && keywords.length > 0) {
                for (KeywordInfo keyword : keywords) {
                    try {
                        keyword.prep(); // 执行小写转换和modID替换
                        logger.info("处理关键词：" + keyword.PROPER_NAME + "，NAMES原始值：" + Arrays.toString(keyword.NAMES));
                        registerKeyword(keyword);
                    } catch (Exception e) {
                        logger.error("处理单个关键词 " + (keyword != null ? keyword.PROPER_NAME : "null") + " 失败", e);
                    }
                }
                logger.info("关键词注册完成，共处理 " + keywords.length + " 个关键词");
            } else {
                logger.warn("未解析到任何关键词，检查JSON文件是否为空或格式错误");
            }
        } catch (Exception e) {
            logger.error("加载关键词文件/解析JSON时发生致命错误", e);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                String json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(StandardCharsets.UTF_8.name());
                KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
                logger.info("加载" + getLangString() + "语言关键词，数量：" + (keywords == null ? "null" : keywords.length));
                if (keywords != null) {
                    for (KeywordInfo keyword : keywords) {
                        keyword.prep();
                        registerKeyword(keyword);
                    }
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.", e);
            }
        }
    }


    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION, info.COLOR);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    @Override
    public void receiveEditCharacters() {
        ChenCharacter.Meta.registerCharacter();
    }

    @Override
    public void receiveAddAudio() {
        loadAudio(Sounds.class);
    }

    private static final String[] AUDIO_EXTENSIONS = { ".ogg", ".wav", ".mp3" }; //There are more valid types, but not really worth checking them all here
    private void loadAudio(Class<?> cls) {
        try {
            Field[] fields = cls.getDeclaredFields();
            outer:
            for (Field f : fields) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && f.getType().equals(String.class)) {
                    String s = (String) f.get(null);
                    if (s == null) { //If no defined value, determine path using field name
                        s = audioPath(f.getName());

                        for (String ext : AUDIO_EXTENSIONS) {
                            String testPath = s + ext;
                            if (Gdx.files.internal(testPath).exists()) {
                                s = testPath;
                                BaseMod.addAudio(s, s);
                                f.set(null, s);
                                continue outer;
                            }
                        }
                        throw new Exception("Failed to find an audio file \"" + f.getName() + "\" in " + resourcesFolder + "/audio; check to ensure the capitalization and filename are correct.");
                    }
                    else { //Otherwise, load defined path
                        if (Gdx.files.internal(s).exists()) {
                            BaseMod.addAudio(s, s);
                        }
                        else {
                            throw new Exception("Failed to find audio file \"" + s + "\"; check to ensure this is the correct filepath.");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("Exception occurred in loadAudio: ", e);
        }
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String audioPath(String file) {
        return resourcesFolder + "/audio/" + file;
    }
    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = ChenMod.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);

        if (!resources.exists()) {
            throw new RuntimeException("\n\tFailed to find resources folder; expected it to be at  \"resources/" + name + "\"." +
                    " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                    "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                    "\tat the top of the " + ChenMod.class.getSimpleName() + " java file.");
        }
        if (!resources.child("images").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "images folder is in the correct location.");
        }
        if (!resources.child("localization").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "localization folder is in the correct location.");
        }

        return name;
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(ChenMod.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    @Override
    public void receiveEditCards() {
        new AutoAdd(modID) //Loads files from this mod
                .packageFilter(BaseCard.class) //In the same package as this class
                .setDefaultSeen(true) //And marks them as seen in the compendium
                .cards(); //Adds the cards
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID) //Loads files from this mod
                .packageFilter(BaseRelic.class) //In the same package as this class
                .any(BaseRelic.class, (info, relic) -> { //Run this code for any classes that extend this class
                    if (relic.pool != null)
                        BaseMod.addRelicToCustomPool(relic, relic.pool); //Register a custom character specific relic
                    else
                        BaseMod.addRelic(relic, relic.relicType); //Register a shared or base game character specific relic

                    //If the class is annotated with @AutoAdd.Seen, it will be marked as seen, making it visible in the relic library.
                    //If you want all your relics to be visible by default, just remove this if statement.
                    if (info.seen)
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                });
    }

    @Override
    public void receivePostUpdate() {
        // 游戏未进入地下城（主菜单阶段）
        if (AbstractDungeon.currMapNode == null) {
            return;
        }

        // 当前房间不存在
        if (AbstractDungeon.getCurrRoom() == null) {
            return;
        }

        // 当前房间没有怪物（例如事件房间、商店）
        if (AbstractDungeon.getCurrRoom().monsters == null) {
            return;
        }

        DistanceCache.update();

//        // 自定义奖励
//        LegacyReward.processSelection();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {

        if (abstractRoom.monsters.monsters.isEmpty()){
            return;
        }

        for(AbstractMonster monster: abstractRoom.monsters.monsters){
            if(Objects.equals(monster.id, Buldrokkastee.ID)){
                CardCrawlGame.music.playTempBGM(Sounds.bossBuldrokkasteeBGM);
                break;
            }

            if(Objects.equals(monster.id, Talulah.ID)){
                CardCrawlGame.music.playTempBGM(Sounds.TalulahBGM_1);
                break;
            }

            if(Objects.equals(monster.id, FrostNova.ID)){
                CardCrawlGame.music.playTempBGM(Sounds.FrostNovaBGM_1);
                break;
            }

            if(Objects.equals(monster.id, MephistoSinger.ID)){
                CardCrawlGame.music.playTempBGM(Sounds.MephistoSingerBGM);
                break;
            }

            if(Objects.equals(monster.id, Mephisto.ID) || Objects.equals(monster.id, Faust.ID)){
                CardCrawlGame.music.playTempBGM(Sounds.MephistoFaustBGM);
                break;
            }

        }

        DistanceCache.rebuild();
    }

}

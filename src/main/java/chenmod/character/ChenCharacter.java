package chenmod.character;

import basemod.BaseMod;
import basemod.abstracts.CustomEnergyOrb;
import basemod.abstracts.CustomPlayer;
import chenmod.ChenMod;
import chenmod.actions.NoFastWaitAction;
import chenmod.cards.AttackCard;
import chenmod.cards.AttackWeakPointCard;
import chenmod.cards.DefendCard;
import chenmod.cards.SlashCard;
import chenmod.effects.Chen3IllustrationEffect;
import chenmod.relics.ChiXiaoRelic;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.chenmod.spine38.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static chenmod.ChenMod.characterPath;
import static chenmod.ChenMod.makeID;

public class ChenCharacter extends CustomPlayer {
    //Stats
    public static final int ENERGY_PER_TURN = 3;
    public static final int MAX_HP = 75;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    public static final float SPINE38_SCALE = 1.55f;
    public static final String ATLAS_PATH_CHEN = "chenmod/images/character/animation/char_010_chen.atlas";
    public static final String JSON_PATH_CHEN = "chenmod/images/character/animation/char_010_chen.json";

    public static final String ATLAS_PATH_CHEN3 = "chenmod/images/character/animation/char_1050_chen3.atlas";
    public static final String JSON_PATH_CHEN3 = "chenmod/images/character/animation/char_1050_chen3.json";

    public boolean isChen3 = false;

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    protected com.chenmod.spine38.AnimationState state38;
    protected com.chenmod.spine38.AnimationStateData stateData38;

    // 渲染器：Spine 需要 PolygonSpriteBatch 才能绘制网格变形
    // 使用静态变量以节省性能，所有该角色实例共享一个渲染器
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();

    static {
        // 开启预乘 Alpha，防止图片边缘产生白边或黑边
        sr.setPremultipliedAlpha(true);
    }

    // Spine 资源缓存
    private static final TextureAtlas chenAtlas;
    private static final SkeletonData chenSkeletonData;

    private static final TextureAtlas chen3Atlas;
    private static final SkeletonData chen3SkeletonData;

    static {
        // 预加载 Chen
        chenAtlas = new TextureAtlas(Gdx.files.internal(ATLAS_PATH_CHEN));
        SkeletonJson jsonChen = new SkeletonJson(chenAtlas);
        jsonChen.setScale(Settings.renderScale / SPINE38_SCALE);
        chenSkeletonData = jsonChen.readSkeletonData(Gdx.files.internal(JSON_PATH_CHEN));

        // 预加载 Chen3
        chen3Atlas = new TextureAtlas(Gdx.files.internal(ATLAS_PATH_CHEN3));
        SkeletonJson jsonChen3 = new SkeletonJson(chen3Atlas);
        jsonChen3.setScale(Settings.renderScale / SPINE38_SCALE);
        chen3SkeletonData = jsonChen3.readSkeletonData(Gdx.files.internal(JSON_PATH_CHEN3));
    }

    // 角色类字段
    private Chen3IllustrationEffect chen3Effect;


    //Strings
    private static final String ID = makeID("ChenID"); //This should match whatever you have in the CharacterStrings.json file
    private static String[] getNames() { return CardCrawlGame.languagePack.getCharacterString(ID).NAMES; }
    private static String[] getText() { return CardCrawlGame.languagePack.getCharacterString(ID).TEXT; }

    //This static class is necessary to avoid certain quirks of Java classloading when registering the character.
    public static class Meta {
        //These are used to identify your character, as well as your character's card color.
        //Library color is basically the same as card color, but you need both because that's how the game was made.
        @SpireEnum
        public static PlayerClass CHEN_CHARACTER;

        // ========== 核心修改：替换为陈的朱红色常量 ==========
        @SpireEnum(name = "CHEN_SCARLET_COLOR") // 唯一标识：陈的朱红色
        public static AbstractCard.CardColor CARD_COLOR;
        @SpireEnum(name = "CHEN_SCARLET_COLOR") @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;

        //Character select images（保持不变）
        private static final String CHAR_SELECT_BUTTON = characterPath("select/button.png");
        private static final String CHAR_SELECT_PORTRAIT = characterPath("select/portrait.png");

        // ========== 核心修改：朱红色卡牌背景资源路径 ==========
        // 建议你把对应的红色卡牌背景图放在这些路径下
        private static final String BG_ATTACK = characterPath("cardback/bg_attack.png");
        private static final String BG_ATTACK_P = characterPath("cardback/bg_attack_p.png");
        private static final String BG_SKILL = characterPath("cardback/bg_skill.png");
        private static final String BG_SKILL_P = characterPath("cardback/bg_skill_p.png");
        private static final String BG_POWER = characterPath("cardback/bg_power.png");
        private static final String BG_POWER_P = characterPath("cardback/bg_power_p.png");
        private static final String ENERGY_ORB = characterPath("cardback/energy_orb.png");
        private static final String ENERGY_ORB_P = characterPath("cardback/energy_orb_p.png");
        private static final String SMALL_ORB = characterPath("cardback/small_orb.png");

        // ========== 核心修改：陈的标志性朱红色 RGB 值 ==========
        // #C8102E 明日方舟陈的主题朱红（可根据需求微调）
        private static final Color cardColor = new Color(200f/255f, 70f/255f, 70f/255f, 1f);

        //Methods that will be used in the main mod file
        public static void registerColor() {
            // 注册陈的朱红色卡牌配色体系
            BaseMod.addColor(CARD_COLOR, cardColor,
                    BG_ATTACK, BG_SKILL, BG_POWER, ENERGY_ORB,
                    BG_ATTACK_P, BG_SKILL_P, BG_POWER_P, ENERGY_ORB_P,
                    SMALL_ORB);

        }

        public static void registerCharacter() {
            BaseMod.addCharacter(new ChenCharacter(), CHAR_SELECT_BUTTON, CHAR_SELECT_PORTRAIT);
        }
    }


    //In-game images（保持不变）
    private static final String SHOULDER_1 = characterPath("shoulder.png"); //Shoulder 1 and 2 are used at rest sites.
    private static final String SHOULDER_2 = characterPath("shoulder2.png");
    private static final String CORPSE = characterPath("corpse.png"); //Corpse is when you die.

    //Textures used for the energy orb（保持不变）
    private static final String[] orbTextures = {
            characterPath("energyorb/layer1.png"), //When you have energy
            characterPath("energyorb/layer2.png"),
            characterPath("energyorb/layer3.png"),
            characterPath("energyorb/layer4.png"),
            characterPath("energyorb/layer5.png"),
            characterPath("energyorb/cover.png"), //"container"
            characterPath("energyorb/layer1d.png"), //When you don't have energy
            characterPath("energyorb/layer2d.png"),
            characterPath("energyorb/layer3d.png"),
            characterPath("energyorb/layer4d.png"),
            characterPath("energyorb/layer5d.png")
    };

    //Speeds at which each layer of the energy orb texture rotates. Negative is backwards.
    private static final float[] layerSpeeds = new float[] {
            -20.0F,
            20.0F,
            -40.0F,
            40.0F,
            360.0F
    };

    private final Map<String, Float> animSpeedMap = new HashMap<>();    // 动画速度哈希表

    //Actual character class code below this point

    public ChenCharacter() {
        super(getNames()[0], Meta.CHEN_CHARACTER,
                new CustomEnergyOrb(orbTextures, characterPath("energyorb/vfx.png"), layerSpeeds), //Energy Orb
                null, null); //Animation

        initializeClass(null, // 要使用的图片路径。方法其余部分保持不变
                SHOULDER_2,
                SHOULDER_1,
                CORPSE,
                getLoadout(),
                20.0F, -20.0F, 200.0F, 250.0F, // 角色碰撞箱：x、y 坐标，以及宽和高
                new EnergyManager(ENERGY_PER_TURN));

        //Location for text bubbles. You can adjust it as necessary later. For most characters, these values are fine.
        dialogX = (drawX + 0.0F * Settings.scale);
        dialogY = (drawY + 220.0F * Settings.scale);

        // 初始化动画速度配置（根据需要自定义）

        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle", 1.0f);          // 待机动画：原速
        animSpeedMap.put("Skill", 1.5f);    // 鞘击启动
        animSpeedMap.put("Skill_2", 1.5f);  // 赤霄拔刀蓄力
        animSpeedMap.put("Skill_3", 1.0f);  // 绝影蓄力
        animSpeedMap.put("Skill_End_3", 1.0f);   // 绝影结束
        animSpeedMap.put("Attack", 2.0f);        // 普通攻击
        animSpeedMap.put("Die", 0.5f);           // 死亡动画：原速
        // 可添加更多动画的速度配置

        loadSpine38();

        this.chen3Effect = new Chen3IllustrationEffect(null);

    }

    // --- 加载逻辑 ---
    private void loadSpine38() {
        // 使用预加载的 Chen 资源
        skeleton38 = new Skeleton(chenSkeletonData);
        skeleton38.setColor(Color.WHITE);

        stateData38 = new AnimationStateData(chenSkeletonData);
        state38 = new AnimationState(stateData38);

        this.isChen3 = false;

        state38.setAnimation(0, "Idle", true);

        stateData38.setMix("Idle", "Attack", 0.1f);
        stateData38.setMix("Attack", "Idle", 0.1f);
        stateData38.setMix("Idle", "Skill_3", 0.1f);
        stateData38.setMix("Skill_End_3", "Idle", 0.5f);
        stateData38.setMix("Idle", "Skill_2", 0.1f);
        stateData38.setMix("Skill_2", "Idle", 0.1f);
        stateData38.setMix("Idle", "Skill", 0.1f);
        stateData38.setMix("Skill", "Idle", 0.1f);
        stateData38.setMix("Idle", "Die", 0.1f);
    }


    public void changeSpine38ToChen3(Runnable afterChange) {
        if (this.isChen3) {
            if (afterChange != null) afterChange.run();
            return;
        }

        // 重置已有特效对象，绑定新的回调
        chen3Effect.reset(() -> {
            // Spine 切换逻辑
            skeleton38 = new Skeleton(chen3SkeletonData);
            skeleton38.setColor(Color.WHITE);
            skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));

            stateData38 = new AnimationStateData(chen3SkeletonData);
            stateData38.setDefaultMix(0.2f);
            state38 = new AnimationState(stateData38);

            state38.setAnimation(0, "Idle", true);
            this.isChen3 = true;

            // 设置混合
            stateData38.setMix("Idle", "Attack", 0.1f);
            stateData38.setMix("Attack", "Idle", 0.1f);
            stateData38.setMix("Idle", "Skill_3", 0.1f);
            stateData38.setMix("Skill_End_3", "Idle", 0.5f);
            stateData38.setMix("Idle", "Skill_2", 0.1f);
            stateData38.setMix("Skill_2", "Idle", 0.1f);
            stateData38.setMix("Idle", "Skill", 0.1f);
            stateData38.setMix("Skill", "Idle", 0.1f);
            stateData38.setMix("Idle", "Die", 0.1f);

            // 插画完成后执行 Action 传入的逻辑
            if (afterChange != null) afterChange.run();
        });

        AbstractDungeon.topLevelEffects.add(chen3Effect);
    }




    @Override
    public void render(SpriteBatch sb) {
        // 姿态（必画）
        this.stance.render(sb);

        // 战斗中：血条 + orb
        if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                || AbstractDungeon.getCurrRoom() instanceof MonsterRoom)
                && !this.isDead) {

            renderHealth(sb);

            if (!this.orbs.isEmpty()) {
                for (AbstractOrb o : this.orbs) {
                    o.render(sb);
                }
            }
        }

        // 非休息房
        if (!(AbstractDungeon.getCurrRoom() instanceof RestRoom)) {
            renderPlayerImage(sb);
            this.hb.render(sb);
            this.healthHb.render(sb);
        } else {
            sb.setColor(Color.WHITE);
            renderShoulderImg(sb);
        }
    }

    // --- 渲染逻辑 (Override) ---
    // 这一点至关重要：我们需要打断原有的 SpriteBatch，切换到 PolygonSpriteBatch
    @Override
    public void renderPlayerImage(SpriteBatch sb) {
        if (state38 == null || skeleton38 == null) return;

        float baseSpeed = 2.0f; // 你原有的全局基础速度
        float animSpeed = baseSpeed; // 默认使用全局速度

        if (state38.getCurrent(0) != null && state38.getCurrent(0).getAnimation() != null) {
            String currentAnimName = state38.getCurrent(0).getAnimation().getName();
            // 如果配置了该动画的速度，则使用配置值；否则用全局速度
            animSpeed = animSpeedMap.getOrDefault(currentAnimName, baseSpeed);
        }

        // 1. 更新动画时间步
        state38.update(Gdx.graphics.getDeltaTime() * animSpeed);
        state38.apply(skeleton38);
        skeleton38.updateWorldTransform();

        // 2. 同步位置 (对齐到游戏内的角色坐标)
        skeleton38.setPosition(this.drawX + this.animX, this.drawY + this.animY);
        skeleton38.setColor(this.tint.color); // 响应游戏内的变色效果(如受伤闪烁)

        // 3. 处理翻转 (例如被"混乱"状态影响时)
        skeleton38.setScaleX(this.flipHorizontal ? -Math.abs(skeleton38.getScaleX()) : Math.abs(skeleton38.getScaleX()));
        skeleton38.setScaleY(this.flipVertical ? -Math.abs(skeleton38.getScaleY()) : Math.abs(skeleton38.getScaleY()));

        // 4. 切换画笔并绘制
        sb.end(); // 暂停主画笔
        psb.begin(); // 开启多边形画笔

        sr.draw(psb, skeleton38); // 绘制骨骼

        psb.end(); // 结束多边形画笔
        sb.begin(); // 恢复主画笔，以免影响后续UI绘制
    }

    // --- 动画控制接口 ---

    @Override
    public void playDeathAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Die", false);
        }
    }

    // 当你打出一张攻击牌时调用
    @Override
    public void useFastAttackAnimation() {

    }

    public void useAttackAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Attack", false);
            state38.addAnimation(0, "Idle", true, 0f); // 攻击完自动切回 Idle
        }
    }

    public void useSkillAttackAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Skill", false);
            state38.addAnimation(0, "Idle", true, 0f); // 攻击完自动切回 Idle
        }
    }

    public void useSkill2AttackAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Skill_2", false);
            state38.addAnimation(0, "Idle", true, 0f); // 攻击完自动切回 Idle
        }
    }
    public void useSkill3BeginAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Skill_3", false);
        }
    }

    public void useSkill3EndAnimation() {
        if (state38 != null) {
            state38.setAnimation(0, "Skill_End_3",false);
            state38.addAnimation(0, "Idle", true, 1.0f); // 攻击完自动切回 Idle
        }
    }



    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        //List of IDs of cards for your starting deck.
        //If you want multiple of the same card, you have to add it multiple times.
        // 初始牌组
        retVal.add(AttackCard.ID);
        retVal.add(AttackCard.ID);
        retVal.add(AttackCard.ID);
        retVal.add(AttackCard.ID);

        retVal.add(DefendCard.ID);
        retVal.add(DefendCard.ID);
        retVal.add(DefendCard.ID);
        retVal.add(DefendCard.ID);

        retVal.add(AttackWeakPointCard.ID);
        retVal.add(SlashCard.ID);

        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        //IDs of starting relics. You can have multiple, but one is recommended.

        retVal.add(ChiXiaoRelic.ID);

//        下面是为了防止牌比较少时候的崩溃，提前整的五彩棱镜
//        retVal.add(PrismaticShard.ID);

        return retVal;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        //This card is used for the Gremlin card matching game.
        //It should be a non-strike non-defend starter card, but it doesn't have to be.
        return new AttackWeakPointCard();
    }

    /*- Below this is methods that you should *probably* adjust, but don't have to. -*/

    @Override
    public int getAscensionMaxHPLoss() {
        return 4; //Max hp reduction at ascension 14+
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        //These attack effects will be used when you attack the heart.
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.SLASH_VERTICAL,
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    @Override
    public List<CutscenePanel> getCutscenePanels() {
        List<CutscenePanel> panels = new ArrayList<>();
        panels.add(new CutscenePanel("chenmod/images/scenes/chenCharacter.jpg", Sounds.qiaoJiEffect));
        panels.add(new CutscenePanel("chenmod/images/scenes/vic1.png", Sounds.jueYingEffect_3));
        panels.add(new CutscenePanel("chenmod/images/scenes/vic2.png"));
        panels.add(new CutscenePanel("chenmod/images/scenes/vic3.png"));
        return panels;
    }

    // ========== 核心修改：同步调整卡牌视觉效果颜色为朱红 ==========
    private final Color cardRenderColor = new Color(200f/255f, 16f/255f, 46f/255f, 1f); // 卡牌渲染颜色
    private final Color cardTrailColor = new Color(220f/255f, 30f/255f, 55f/255f, 0.8f); // 卡牌拖尾颜色
    private final Color slashAttackColor = new Color(180f/255f, 10f/255f, 35f/255f, 1f); // 攻击特效颜色

    @Override
    public Color getCardRenderColor() {
        return cardRenderColor;
    }

    @Override
    public Color getCardTrailColor() {
        return cardTrailColor;
    }

    @Override
    public Color getSlashAttackColor() {
        return slashAttackColor;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        // 改为红色能量数字字体，匹配陈的朱红风格
        return FontHelper.energyNumFontRed;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        //This occurs when you click the character's button in the character select screen.
        //See SoundMaster for a full list of existing sound effects, or look at BaseMod's wiki for adding custom audio.
        CardCrawlGame.sound.playA(Sounds.jueYingEffect_3, MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        //Similar to doCharSelectScreenSelectEffect, but used for the Custom mode screen. No shaking.
        return "ATTACK_DAGGER_2";
    }

    //Don't adjust these four directly, adjust the contents of the CharacterStrings.json file.
    @Override
    public String getLocalizedCharacterName() {
        return getNames()[0];
    }
    @Override
    public String getTitle(PlayerClass playerClass) {
        return getNames()[1];
    }
    @Override
    public String getSpireHeartText() {
        return getText()[1];
    }
    @Override
    public String getVampireText() {
        return getText()[2]; //Generally, the only difference in this text is how the vampires refer to the player.
    }

    /*- You shouldn't need to edit any of the following methods. -*/

    //This is used to display the character's information on the character selection screen.
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(getNames()[0], getText()[0],
                MAX_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this,
                getStartingRelics(), getStartingDeck(), false);
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return Meta.CARD_COLOR;
    }

    @Override
    public AbstractPlayer newInstance() {
        //Makes a new instance of your character class.
        return new ChenCharacter();
    }
}
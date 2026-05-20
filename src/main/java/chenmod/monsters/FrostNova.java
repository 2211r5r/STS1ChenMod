package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.actions.NoFastWaitAction;
import chenmod.cards.FrozenCard;
import chenmod.cards.SpicySugarCard;
import chenmod.effects.FrostMistEffect;
import chenmod.effects.GoldenInvincibleAuraEffect;
import chenmod.effects.IceShockwaveEffect;
import chenmod.powers.*;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

import java.util.HashMap;
import java.util.Map;

public class FrostNova extends AbstractMonster {

    public static final String ID = ChenMod.makeID(FrostNova.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH_1 = "chenmod/images/monsters/animation/FrostNova/enemy_1505_frstar.atlas";
    private static final String JSON_PATH_1 = "chenmod/images/monsters/animation/FrostNova/enemy_1505_frstar.json";

    private static final String ATLAS_PATH_2 = "chenmod/images/monsters/animation/FrostNova/enemy_1510_frstar2.atlas";
    private static final String JSON_PATH_2 = "chenmod/images/monsters/animation/FrostNova/enemy_1510_frstar2.json";
    private static final float SCALE = 1.5F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }
    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    // 解决切换spine模型时会出现的模型闪现问题。
    private boolean skipNextRender = false;

    // --- 角色基础属性 ---
    private static final int MAX_HP = 140;
    private static final int ATTACK_DAMAGE = 9;
    private static final int STRONG_ATTACK_DAMAGE= 14;
    private static final int STRONGTH_ATTACK_COUNTER= 3;
    private static final int SKILL_COUNTER= 3;  // 每四个回合使用一次技能

    private static final int STRONGTH_SKILL_COUNTER= 2;  // 每两次技能使用一次强大技能
    private static final int REVIVE_MAX_TURN = 1;    // 复活倒计时

    private enum Posture {
        NORMAL,  // 普通姿态
        REVIVE, // 复活
        WINTER, // 冬痕姿态
    }

    private Posture posture;
    private int skillCounter = 2;
    private int strongthAttackCounter = 0;
    private int strongthSkillCounter = 0;
    private int reviveCounter = 0;
    public FrostNova (float offsetX, float offsetY){
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 320.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;

        this.flipHorizontal = true;

        this.posture = Posture.NORMAL;

        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 28);
        }else{
            this.setHp(MAX_HP);
        }

        if(AbstractDungeon.ascensionLevel >= 4){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 2));
            this.damage.add(new DamageInfo(this, STRONG_ATTACK_DAMAGE + 3));
        }else{
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE));
            this.damage.add(new DamageInfo(this, STRONG_ATTACK_DAMAGE));
        }

        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Move", 1.0f);
        animSpeedMap.put("Skill_1", 1.0f);
        animSpeedMap.put("Skill_2", 1.0f);
        animSpeedMap.put("Skill_3", 1.0f);
        animSpeedMap.put("Attack", 1.5f);
        animSpeedMap.put("Die", 1.0f);
        // 可添加更多动画的速度配置

        loadSpine();
    }

    // 普通形态
    private void loadSpine() {
        atlas38 = new TextureAtlas(Gdx.files.internal(ATLAS_PATH_1));
        SkeletonJson json = new SkeletonJson(atlas38);
        json.setScale(Settings.renderScale / SCALE);
        SkeletonData data = json.readSkeletonData(Gdx.files.internal(JSON_PATH_1));
        skeleton38 = new Skeleton(data);
        skeleton38.setColor(Color.WHITE);

        skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));

        stateData38 = new AnimationStateData(data);
        state38 = new AnimationState(stateData38);
        stateData38.setDefaultMix(0.1f);

        state38.setAnimation(0, "Idle", true);

    }

    // 普通形态死亡的时候切换 Spine 模型
    public void changeSpine() {

        this.skipNextRender = true;

        // 1. 先 dispose 旧资源（非常重要，否则内存泄漏+贴图错乱）
        if (atlas38 != null) {
            atlas38.dispose();
            atlas38 = null;
        }

        // 2. 加载新贴图集
        atlas38 = new TextureAtlas(Gdx.files.internal(ATLAS_PATH_2));

        // 3. 加载新骨骼数据
        SkeletonJson json = new SkeletonJson(atlas38);
        json.setScale(Settings.renderScale / SCALE);
        SkeletonData newSkeletonData = json.readSkeletonData(Gdx.files.internal(JSON_PATH_2));

        // 4. 重建骨骼和动画状态
        skeleton38 = new Skeleton(newSkeletonData);
        skeleton38.setColor(Color.WHITE);
        skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));
        skeleton38.setPosition(this.drawX + this.animX, this.drawY + this.animY);

        stateData38 = new AnimationStateData(newSkeletonData);
        stateData38.setDefaultMix(0.1f);
        state38 = new AnimationState(stateData38);

        state38.setAnimation(0, "Idle", true);

    }

    @Override
    public void update() {
        super.update();
        if (state38 != null) {

            float baseSpeed = 1.0f; // 你原有的全局基础速度
            float animSpeed = baseSpeed; // 默认使用全局速度

            if (state38.getCurrent(0) != null && state38.getCurrent(0).getAnimation() != null) {
                String currentAnimName = state38.getCurrent(0).getAnimation().getName();
                // 如果配置了该动画的速度，则使用配置值；否则用全局速度
                animSpeed = animSpeedMap.getOrDefault(currentAnimName, baseSpeed);
            }

            state38.update(Gdx.graphics.getDeltaTime() * animSpeed);
            state38.apply(skeleton38);
            skeleton38.updateWorldTransform();
            skeleton38.setPosition(this.drawX + this.animX, this.drawY + this.animY);
            skeleton38.setColor(this.tint.color);
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        if (skipNextRender) {
            skipNextRender = false;
            return;
        }

        // 1. 渲染 Spine 动画
        if (!this.isDead || (state38 != null && !state38.getCurrent(0).isComplete())) {
            sb.end();
            psb.begin();
            sr.draw(psb, skeleton38);
            psb.end();
            sb.begin();
        }

        // 2. 渲染碰撞箱
        this.hb.render(sb);
        this.intentHb.render(sb);
        this.healthHb.render(sb);

        // 3. 渲染血条和名字
        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderName", SpriteBatch.class).invoke(this, sb);
        }

        // 4. 渲染意图 (带完整条件判定)
        if (!this.isDying && !this.isEscaping &&
                AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.player.isDead &&
                !AbstractDungeon.player.hasRelic("Runic Dome") &&
                this.intent != Intent.NONE &&
                !Settings.hideCombatElements) {

            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxBehind", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntent", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntentVfxAfter", SpriteBatch.class).invoke(this, sb);
            ReflectionHacks.privateMethod(AbstractMonster.class, "renderDamageRange", SpriteBatch.class).invoke(this, sb);
        }
    }

    @Override
    public void takeTurn() {
        Label_NextMove:{
            switch(this.nextMove){

                case 1: // 攻击
                    this.skillCounter++;
                    this.strongthAttackCounter++;

                    if(state38 != null){
                        state38.setAnimation(0, "Attack", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                    break Label_NextMove;

                case 2: // 重击----冬痕形态
                    this.strongthAttackCounter=0;
                    this.skillCounter++;

                    if(state38 != null){
                        state38.setAnimation(0, "Skill_3", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(2.1f));

                    AbstractDungeon.actionManager.addToBottom(
                            new VFXAction(
                                    new IceShockwaveEffect(
                                            skeleton38.getX(), skeleton38.getY() + 110.0F
                                    ),
                                    0.5f // 核心修改：将默认的0.5f改为更小的值（比如0.25f），特效速度翻倍
                            )
                    );
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1),
                            AbstractGameAction.AttackEffect.BLUNT_HEAVY));


                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {

                        if (!m.isDead && !m.isDying) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 2), 2));
                        }
                    }

                    break Label_NextMove;

                case 3: // 挂debuff
                    this.skillCounter=0;
                    this.strongthSkillCounter++;
                    this.strongthAttackCounter++;

                    if (state38 != null) {
                        state38.setAnimation(0,"Skill_1", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    // 释放对玩家的 Debuff
                    if(!AbstractDungeon.player.hasPower(FrozenPower.POWER_ID)){
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrozenPower(AbstractDungeon.player), -1));
                    }

                    if(AbstractDungeon.ascensionLevel >= 19){
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.45f, Settings.HEIGHT / 2.0f));
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.6f, Settings.HEIGHT / 2.0f));
                    }else{
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new FrozenCard()));
                    }

                    break Label_NextMove;

                case 4: // 挂一个强大的debuff
                    this.strongthSkillCounter = 0;
                    this.skillCounter = 0;
                    this.strongthAttackCounter++;

                    if (state38 != null) {
                        state38.setAnimation(0,"Skill_1", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    // 释放对玩家的 Debuff
                    if(!AbstractDungeon.player.hasPower(FrozenPower.POWER_ID)){
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrozenPower(AbstractDungeon.player), -1));
                    }
//                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.3f, Settings.HEIGHT / 2.0f));
//                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.45f, Settings.HEIGHT / 2.0f));
//                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.6f, Settings.HEIGHT / 2.0f));
//                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new FrozenCard(), 1, true, false, false, Settings.WIDTH * 0.75f, Settings.HEIGHT / 2.0f));

                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new FrozenCard()));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new FrozenCard()));
                    if(AbstractDungeon.ascensionLevel >= 19){
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAndDeckAction(new FrozenCard()));
                    }

                    break Label_NextMove;

                case 5: // 复活
                    this.reviveCounter++;
                    this.strongthSkillCounter++;
                    this.strongthAttackCounter++;

                    if(this.reviveCounter >= REVIVE_MAX_TURN){
                        this.halfDead = false;
                        this.posture=Posture.WINTER;

                        AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth));
                        AbstractDungeon.actionManager.addToBottom(new CanLoseAction());

                        AbstractDungeon.effectList.add(
                                new TextAboveCreatureEffect(
                                        this.hb.cX,
                                        this.hb.cY + 50.0F,
                                        "冬痕",
                                        Color.SKY
                                )
                        );

                        if(state38 != null){
                            try{
                                state38.setAnimation(0, "Skill_3", false);
                                ChenMod.logger.info("【spine38】正常找到Skill_3动画");
                            }catch(IllegalArgumentException e){
                                state38.setAnimation(0, "Skill_2", false);
                                ChenMod.logger.error("【spine38】错误：未正常找到Skill_3动画！！！");
                            }

                            state38.addAnimation(0, "Idle", true, 0.0f);
                        }

                        AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(1.9f));

                        AbstractDungeon.actionManager.addToBottom(
                                new VFXAction(
                                        new IceShockwaveEffect(
                                                skeleton38.getX(), skeleton38.getY() + 110.0F
                                        ),
                                        0.5f // 核心修改：将默认的0.5f改为更小的值（比如0.25f），特效速度翻倍
                                )
                        );
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1),
                                AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                        // 清除 debuff
                        AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Shackled"));

                        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                            if (!m.isDead && !m.isDying) {
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 2), 2));
                            }
                        }

                    }else{
                        AbstractDungeon.effectList.add(
                                new TextAboveCreatureEffect(
                                        this.hb.cX,
                                        this.hb.cY + 50.0F,
                                        "复活阶段",
                                        Color.GREEN
                                )
                        );
                    }
                    break Label_NextMove;
            }
        }

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {

        ChenMod.logger.info("【霜星状态】this.posture:"+this.posture+"; this.reviveCounter:"+this.reviveCounter+"; this.skillCounter:"+skillCounter+"; 【this.strongthSkillCounter】:"+this.strongthSkillCounter+"; 【this.strongthAttackCounter】:"+this.strongthAttackCounter);


        switch(this.posture){
            case REVIVE:
                if(reviveCounter >= REVIVE_MAX_TURN - 1){
                    setMove((byte) 5, Intent.ATTACK_BUFF, this.damage.get(1).base);
                }else{
                    setMove((byte) 5, Intent.UNKNOWN);
                }

                break;

            case NORMAL:

                if(skillCounter >= SKILL_COUNTER){
                    setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
                }else{
                    setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(0).base);
                }

                break;

            case WINTER :

                if (this.strongthSkillCounter >= STRONGTH_SKILL_COUNTER){
                    setMove(monsterStrings.MOVES[3],(byte) 4, Intent.STRONG_DEBUFF);
                    break;
                }

                if(skillCounter>=SKILL_COUNTER){
                    setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
                }else{
                    if(this.strongthAttackCounter >= STRONGTH_ATTACK_COUNTER){
                        setMove(monsterStrings.MOVES[1],(byte) 2, Intent.ATTACK_BUFF, this.damage.get(1).base);
                    }else{
                        setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(0).base);
                    }
                }

                break;
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead && this.posture == Posture.NORMAL) {

            // 进入复活阶段
            this.halfDead = true;
            this.posture = Posture.REVIVE;
            this.reviveCounter = 0;

            AbstractDungeon.getCurrRoom().cannotLose = true;

            setMove((byte) 5, Intent.ATTACK_BUFF, this.damage.get(1).base);
            createIntent();

            CardCrawlGame.music.fadeOutTempBGM();
            CardCrawlGame.music.playTempBGM(Sounds.FrostNovaBGM_3);

            if(state38!=null){// 普通姿态的死亡后又复活的动画（Skill_2），在执行完后立刻切换为冬痕模型的IDLE

                AbstractDungeon.overlayMenu.endTurnButton.disable();
                AnimationState.AnimationStateListener changePostureAnimationListener = new AnimationState.AnimationStateListener() {
                    @Override
                    public void complete(AnimationState.TrackEntry entry) {
                        if ("Skill_2".equals(entry.getAnimation().getName())) {
                            // 切换模型
                            playFrostMist();
                            changeSpine();
                            AbstractDungeon.overlayMenu.endTurnButton.enable();
                            state38.removeListener(this);
                        }
                    }

                    @Override public void start(AnimationState.TrackEntry entry) {}
                    @Override public void interrupt(AnimationState.TrackEntry entry) {
                        // 切换模型
                        playFrostMist();
                        changeSpine();
                        state38.setAnimation(0,"Skill_3", true);
                        AbstractDungeon.overlayMenu.endTurnButton.enable();
                        state38.removeListener(this);
                    }
                    @Override public void end(AnimationState.TrackEntry entry) {}
                    @Override public void dispose(AnimationState.TrackEntry entry) {}
                    @Override public void event(AnimationState.TrackEntry entry, Event event) {}
                };

                state38.setAnimation(0,"Skill_2", false);
                state38.addListener(changePostureAnimationListener);
            }

        }
    }

    @Override
    public void die() {

        this.useFastShakeAnimation(1.0f);
        CardCrawlGame.screenShake.rumble(2.0f);

        if (this.posture != Posture.WINTER) {  // 不是冬痕姿态下的死亡，直接重置。
            return;
        }

        // 播放 Spine 死亡动画
        if (state38 != null) {

            setHalfDead(true);

            AbstractDungeon.getCurrRoom().cannotLose = true;

            AbstractDungeon.overlayMenu.endTurnButton.disable();

            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[0], 1.0F, 2.0F));

            AnimationState.AnimationStateListener dieAnimationListener = new AnimationState.AnimationStateListener() {

                boolean deathHandled = false;

                @Override public void complete(AnimationState.TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }
                @Override public void start(AnimationState.TrackEntry entry) {}
                @Override public void interrupt(AnimationState.TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }
                @Override public void end(AnimationState.TrackEntry entry) {
                }
                @Override public void dispose(AnimationState.TrackEntry entry) {}
                @Override public void event(AnimationState.TrackEntry entry, Event event) {}

                private void handleDeath() {
                    if (deathHandled) return;  // 防止重复执行
                    deathHandled = true;

                    setHalfDead(false);

                    AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(new SpicySugarCard()));
                    AbstractDungeon.getCurrRoom().cannotLose = false;

                    AbstractDungeon.overlayMenu.endTurnButton.enable();

                    superDie(); // 动画结束后再真正死亡
                    CardCrawlGame.music.fadeOutTempBGM();
                    state38.removeListener(this);
                }

            };

            state38.setAnimation(0, "Die", false);
            state38.addListener(dieAnimationListener);
        }else{
            superDie();
        }

    }

    private void superDie(){
        super.die();
    }

    private void setHalfDead(boolean halfDead) {
        this.halfDead = halfDead;
    }

    public void playFrostMist() {
        AbstractDungeon.effectsQueue.add(
                new FrostMistEffect(skeleton38.getX(), skeleton38.getY() + 110.0F, 3.5F)
        );
    }


    @Override
    public void usePreBattleAction() {

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SnowMonsterTeamPower(this)));

        setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
        createIntent();

    }

}

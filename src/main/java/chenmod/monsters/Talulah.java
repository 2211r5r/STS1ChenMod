package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.actions.NoFastWaitAction;
import chenmod.powers.FierceBurningPower;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.FlameBarrierEffect;

import java.util.HashMap;
import java.util.Map;

public class Talulah extends AbstractMonster{

    public static final String ID = ChenMod.makeID(Talulah.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH_1 = "chenmod/images/monsters/animation/Talulah/enemy_1503_talula.atlas";
    private static final String JSON_PATH_1 = "chenmod/images/monsters/animation/Talulah/enemy_1503_talula.json";

    private static final String ATLAS_PATH_2 = "chenmod/images/monsters/animation/Talulah/enemy_1515_bsnake.atlas";
    private static final String JSON_PATH_2 = "chenmod/images/monsters/animation/Talulah/enemy_1515_bsnake.json";
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
    private static final int MAX_HP = 200;
    private static final int ATTACK_DAMAGE = 12;
    private static final int DEFEND_ATTACK_DAMAGE= 10;
    private static final int DEFEND_ATTACK_BLOCK= 12;
    private static final int STRONG_ATTACK_DAMAGE= 14;
    private static final int FIRE_RAIN_DAMAGE= 15;

    private static final int DEFEND_ATTACK_COUNTER= 3;  // 没三个回合使用一次火焰屏障

    private static final int SKILL_COUNTER= 3;  // 每三个回合使用一次技能

    private static final int STRONGTH_SKILL_COUNTER= 2;  // 每两次技能使用一次强大技能

    private static final int REVIVE_MAX_TURN = 1;    // 复活倒计时，两个回合复活

    private static final int DYING_MAX_TURN = 3; // 火焰AOE持续三个回合。

    private static final int MAX_FLAME_BARRIER_POWER_AMOUNT= 5; // 反伤的最大数值。

    private enum Posture {
        NORMAL,  // 普通姿态
        REVIVE, // 复活
        BLACK_SNAKE_1, // 黑蛇姿态_1
        BLACK_SNAKE_2,    // 黑蛇姿态_2
        DYING   // 第三次被击败后，全场火焰。角色扛不住就要遭殃（输掉）
    }

    private Posture posture;

    private int skillCounter = 3;

    private int defendAttackCounter = 2;

    private int strongthSkillCounter = 0;

    private int reviveCounter = 0;

    private boolean isFirstRevive = true;

    private int dyingCounter = 0;

    private int blockAmt = 0;

    private int strengthAmt = 0;

    private int flameBarrierAmt = 0;

    private int fierceBurningAmt = 0;

    private int maxFlameBarrierAmount = 0;

    public Talulah (float offsetX, float offsetY){
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;

        this.flipHorizontal = true;

        this.posture = Posture.NORMAL;

        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 60);
            this.blockAmt = DEFEND_ATTACK_BLOCK + 4;
        }else{
            this.setHp(MAX_HP);
            this.blockAmt = DEFEND_ATTACK_BLOCK;
        }

        if(AbstractDungeon.ascensionLevel >= 4){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 2));
            this.damage.add(new DamageInfo(this, DEFEND_ATTACK_DAMAGE + 2));
            this.damage.add(new DamageInfo(this, STRONG_ATTACK_DAMAGE + 3));
            this.damage.add(new DamageInfo(this, FIRE_RAIN_DAMAGE + 5));

        }else{
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE));
            this.damage.add(new DamageInfo(this, DEFEND_ATTACK_DAMAGE));
            this.damage.add(new DamageInfo(this, STRONG_ATTACK_DAMAGE));
            this.damage.add(new DamageInfo(this, FIRE_RAIN_DAMAGE));
        }

        if(AbstractDungeon.ascensionLevel >= 19){
            this.fierceBurningAmt = 3;
            this.flameBarrierAmt = 3;
            this.maxFlameBarrierAmount = 5;
            this.strengthAmt = 3;
        }else{
            this.fierceBurningAmt = 2;
            this.flameBarrierAmt = 2;
            this.maxFlameBarrierAmount = 5;
            this.strengthAmt = 2;
        }


        // 示例配置：可根据你的动画名称修改
        // 塔露拉-普通形态
        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Move", 1.0f);
        animSpeedMap.put("Skill_1", 1.0f);
        animSpeedMap.put("Skill_2", 2.0f);
        animSpeedMap.put("Attack_1", 1.5f);
        animSpeedMap.put("Attack_2", 1.5f);
        animSpeedMap.put("Die", 1.0f);

        // 塔露拉-黑蛇形态( A--BLACK_SNAKE1， B--BLACK_SNAKE2 )
        animSpeedMap.put("Idle_A", 1.0f);
        animSpeedMap.put("Idle_B", 1.0f);
        animSpeedMap.put("Move_A", 1.0f);
        animSpeedMap.put("Move_B", 1.0f);
        animSpeedMap.put("Skill_1_A", 1.5f);
        animSpeedMap.put("Skill_1_B", 1.5f);
        animSpeedMap.put("Skill_2_A", 1.5f);
        animSpeedMap.put("Skill_2_B", 1.5f);
        animSpeedMap.put("Skill_3", 1.0f);
        animSpeedMap.put("Attack_A", 1.5f);
        animSpeedMap.put("Attack_B", 1.5f);

        animSpeedMap.put("Reborn_1_Start", 1.0f);
        animSpeedMap.put("Reborn_1_Idle", 1.0f);
        animSpeedMap.put("Reborn_1_End", 1.0f);
        animSpeedMap.put("Reborn_2_Start", 1.0f);
        animSpeedMap.put("Reborn_2_Idle", 1.0f);
        animSpeedMap.put("Reborn_2_End", 1.0f);
        // 可添加更多动画的速度配置

        loadSpine();
    }

    // 普通形态塔露拉
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

    // 黑蛇形态塔露拉，记得在普通塔露拉死亡的时候切换 Spine 模型
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

        stateData38 = new AnimationStateData(newSkeletonData);
        stateData38.setDefaultMix(0.1f);
        state38 = new AnimationState(stateData38);
        skeleton38.setPosition(this.drawX + this.animX, this.drawY + this.animY);

        stateData38.setMix("Reborn_1_Idle", "Idle_A", 0.7f);
        stateData38.setMix("Reborn_1_Idle", "Idle", 0.7f);

        try{
            // 这里设置初始动画为一个形式上是个空的，因为要等到一个回合的复活计时后才会设置新的动画
            state38.setAnimation(0, "Reborn_1_Idle", true);
            ChenMod.logger.info("【spine38】动画 Reborn_1_Idle 已播放！");
        } catch (IllegalArgumentException e) {
            // 捕获动画不存在的异常，防止游戏崩溃
            ChenMod.logger.error("【spine38】警告：动画 Reborn_1_Idle 不存在，已跳过播放！");
        }

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
            switch (this.nextMove){

                case 1: // 攻击
                    this.skillCounter++;
                    this.defendAttackCounter++;

                    ChenMod.logger.info("【塔露拉(黑蛇)】正在"+this.posture+"，执行一次普通攻击(type) 1.");

                    switch (this.posture){
                        case NORMAL:

                            ChenMod.logger.info("【塔露拉(黑蛇)】正在普通姿态，执行普通攻击");

                            if (state38 != null) {
                                if(skillCounter % 2 == 0){
                                    state38.setAnimation(0,"Attack_1", false);
                                }else{
                                    AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(0.2f));
                                    state38.setAnimation(0,"Attack_2", false);
                                }
                                state38.addAnimation(0, "Idle", true, 0.0f);
                            }

                            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0),
                                        (this.skillCounter % 2==0)? AbstractGameAction.AttackEffect.BLUNT_LIGHT : AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                            break Label_NextMove;

                        case BLACK_SNAKE_1:
                            if(state38 != null){
                                state38.setAnimation(0, "Attack_A", false);
                                state38.addAnimation(0, "Idle_A", true, 0.0f);
                            }
                            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2),
                                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                            break Label_NextMove;

                        case BLACK_SNAKE_2:
                            if(state38 != null){
                                state38.setAnimation(0, "Attack_B", false);
                                state38.addAnimation(0, "Idle_B", true, 0.0f);
                            }
                            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2),
                                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

                            break Label_NextMove;

                        case REVIVE:

                        case DYING:
                            ChenMod.logger.info("【塔露拉(黑蛇)】正在复活/死亡阶段，为什么会攻击(type) 1?");
                            break Label_NextMove;

                    }

                    break ;

                case 2: // 防御攻击
                    this.skillCounter++;
                    this.defendAttackCounter = 0;
                    ChenMod.logger.info("【塔露拉(黑蛇)】正在"+this.posture+"，执行一次防御攻击(type) 2.");

                    FlameBarrierPower flameBarrier = (FlameBarrierPower) this.getPower(FlameBarrierPower.POWER_ID);

                    float effectDuration = Settings.FAST_MODE ? 0.1f : 0.5f;
                    this.addToBot(new VFXAction(this, new FlameBarrierEffect(this.hb.cX, this.hb.cY), effectDuration));

                    switch (this.posture){
                        case NORMAL:

                            if (state38 != null) {
                                state38.setAnimation(0,"Skill_2", false);
                                state38.addAnimation(0, "Idle", true, 0.0f);
                            }


                            if (flameBarrier == null || flameBarrier.amount < this.maxFlameBarrierAmount) {
                                AbstractDungeon.actionManager.addToBottom(
                                        new ApplyPowerAction(this, this, new FlameBarrierPower(this, this.flameBarrierAmt), this.flameBarrierAmt)
                                );
                            }

                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmt));
                            AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(0.2f));
                            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1),
                                    AbstractGameAction.AttackEffect.SLASH_HEAVY));

                            break Label_NextMove;

                        case BLACK_SNAKE_1:

                            if (state38 != null) {
                                if(this.strongthSkillCounter % 2 == 0){
                                    state38.setAnimation(0,"Skill_1_A", false);
                                }else{
                                    state38.setAnimation(0,"Skill_2_A", false);
                                }

                                state38.addAnimation(0, "Idle_A", true, 0.0f);

                            }

                            if (flameBarrier == null || flameBarrier.amount < this.maxFlameBarrierAmount) {
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FlameBarrierPower(this, this.flameBarrierAmt), this.flameBarrierAmt));
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt), this.strengthAmt));

                            }else{
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt + this.flameBarrierAmt), this.strengthAmt + this.flameBarrierAmt));
                            }

                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmt));

                            break Label_NextMove;

                        case BLACK_SNAKE_2:

                            if (state38 != null) {
                                if(this.strongthSkillCounter % 2 == 0){
                                    state38.setAnimation(0,"Skill_1_B", false);
                                }else{
                                    state38.setAnimation(0,"Skill_2_B", false);
                                }
                                state38.addAnimation(0, "Idle_B", true, 0.0f);
                            }

                            if (flameBarrier == null || flameBarrier.amount <  this. maxFlameBarrierAmount) {
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FlameBarrierPower(this, this.flameBarrierAmt), this.flameBarrierAmt));
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt), this.strengthAmt));

                            }else{
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt + this.flameBarrierAmt), this.strengthAmt + this.flameBarrierAmt));
                            }

                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.blockAmt));

                            break Label_NextMove;

                        case REVIVE:

                        case DYING:
                            ChenMod.logger.info("【塔露拉(黑蛇)】正在黑色姿态/复活/死亡阶段，为什么会防御攻击(type) 2?");
                            break Label_NextMove;

                    }

                    break ;


                case 3: // Skill_1 普通技能

                    this.skillCounter = 0;
                    this.strongthSkillCounter++;
                    ChenMod.logger.info("【塔露拉(黑蛇)】正在"+this.posture+"，执行一次技能");


                    switch (this.posture){
                        case NORMAL:

                            if (state38 != null) {
                                state38.setAnimation(0,"Skill_1", false);
                                state38.addAnimation(0, "Idle", true, 0.0f);
                            }

                            // 释放对玩家的 Debuff

                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FierceBurningPower(AbstractDungeon.player, this.fierceBurningAmt), this.fierceBurningAmt));

                            break Label_NextMove;

                        case BLACK_SNAKE_1:

                            if (state38 != null) {
                                if(this.strongthSkillCounter % 2 == 0){
                                    state38.setAnimation(0,"Skill_1_A", false);
                                }else{
                                    state38.setAnimation(0,"Skill_2_A", false);
                                }

                                state38.addAnimation(0, "Idle_A", true, 0.0f);
                            }

                            // 释放对玩家的 Debuff
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FierceBurningPower(AbstractDungeon.player, this.fierceBurningAmt), this.fierceBurningAmt));

                            break Label_NextMove;

                        case BLACK_SNAKE_2:

                            if (state38 != null) {
                                if(this.strongthSkillCounter % 2 == 0){
                                    state38.setAnimation(0,"Skill_1_B", false);
                                }else{
                                    state38.setAnimation(0,"Skill_2_B", false);
                                }
                                state38.addAnimation(0, "Idle_B", true, 0.0f);
                            }

                            // 释放对玩家的 Debuff
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FierceBurningPower(AbstractDungeon.player, this.fierceBurningAmt), this.fierceBurningAmt));

                            break Label_NextMove;

                        case REVIVE:

                        case DYING:
                            ChenMod.logger.info("【塔露拉(黑蛇)】正在复活/死亡阶段，为什么会使用普通技能(type) 3?");
                            break Label_NextMove;

                    }

                    break ;


                case 4: // Skill_2 强大技能
                    this.skillCounter = 0;
                    this.strongthSkillCounter= 0;

                    switch (this.posture){
                        case BLACK_SNAKE_2:
                            ChenMod.logger.info("【塔露拉(黑蛇)】正在普通姿态，执行一次强大技能");

                            if (state38 != null) {
                                state38.setAnimation(0,"Skill_3", false);
                                state38.addAnimation(0, "Idle_B", true, 0.0f);
                            }

                            // 强大 DeBuff
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FierceBurningPower(AbstractDungeon.player, 99), 99));

                            break Label_NextMove;

                        case NORMAL:

                        case BLACK_SNAKE_1:

                        case REVIVE:

                        case DYING:
                            ChenMod.logger.info("【塔露拉(黑蛇)】正在普通姿态/黑蛇姿态一阶段/复活/死亡阶段，为什么会使用强大技能(type) 4?");
                            break Label_NextMove;

                    }

                    break ;

                case 5: // Revive复活
                    this.reviveCounter++;
                    this.strongthSkillCounter++;

                    if(this.reviveCounter >= REVIVE_MAX_TURN){

                        this.halfDead =false;

                        AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth));
                        AbstractDungeon.actionManager.addToBottom(new CanLoseAction());

                        if(isFirstRevive){
                            if(state38!=null){
                                state38.setAnimation(0,"Idle", true);
                            }
                            this.posture = Posture.BLACK_SNAKE_1;
                            isFirstRevive = false;
                        }else{
                            if(state38!=null){
                                state38.setAnimation(0,"Reborn_1_End", true);
                                state38.addAnimation(0, "Idle_B", true,0.0f);
                            }
                            this.strongthSkillCounter = STRONGTH_SKILL_COUNTER;
                            this.posture = Posture.BLACK_SNAKE_2;
                        }

                        if (Settings.isEndless && AbstractDungeon.player.hasBlight("ToughEnemies")) {
                            final float mod = AbstractDungeon.player.getBlight("ToughEnemies").effectFloat();
                            this.maxHealth *= (int)mod;
                        }

                        CardCrawlGame.screenShake.rumble(2.0f);
                        CardCrawlGame.sound.playV(Sounds.revive_3_Effect_Buldrokkastee, 1.5f);

                        AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5f));
                        AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3),
                                AbstractGameAction.AttackEffect.FIRE));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FierceBurningPower(AbstractDungeon.player, this.fierceBurningAmt), this.fierceBurningAmt));
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.strengthAmt), this.strengthAmt));

                        AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Shackled"));

                    }

                    break ;

                case 6: // Dying死亡过程
                    this.dyingCounter++;
                    // 临时解除 halfDead,
                    this.halfDead = false;

                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new FireballEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5f));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3),
                            AbstractGameAction.AttackEffect.FIRE));

                    if(dyingCounter >= DYING_MAX_TURN){
                        die();
                    }

                    break ;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        switch(this.posture){
            case REVIVE:
                setMove(monsterStrings.MOVES[4], (byte) 5, Intent.ATTACK_BUFF,  this.damage.get(3).base);
                break;

            case DYING:
                setMove(monsterStrings.MOVES[5],(byte) 6, Intent.ATTACK_BUFF, this.damage.get(3).base);
                break;

            case BLACK_SNAKE_2:

                if(!AbstractDungeon.player.hasPower(FierceBurningPower.POWER_ID) && skillCounter >= SKILL_COUNTER){
                    if(strongthSkillCounter >= STRONGTH_SKILL_COUNTER){
                        setMove(monsterStrings.MOVES[3],(byte) 4, Intent.STRONG_DEBUFF);
                    }else{
                        setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
                    }
                }else{
                    if(this.defendAttackCounter >= DEFEND_ATTACK_COUNTER - 1){
                        setMove(monsterStrings.MOVES[1],(byte) 2, Intent.DEFEND_BUFF);
                    }else{
                        setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(2).base);
                    }
                }


                break;

            case BLACK_SNAKE_1:
                if(!AbstractDungeon.player.hasPower(FierceBurningPower.POWER_ID) &&  skillCounter >= SKILL_COUNTER){
                    setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
                }else{
                    if(this.defendAttackCounter >= DEFEND_ATTACK_COUNTER - 1){
                        setMove(monsterStrings.MOVES[1],(byte) 2, Intent.DEFEND_BUFF);
                    }else{
                        setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(2).base);
                    }
                }
                break;

            case NORMAL:
                if(!AbstractDungeon.player.hasPower(FierceBurningPower.POWER_ID) &&  skillCounter>=SKILL_COUNTER){
                    setMove(monsterStrings.MOVES[2],(byte) 3, Intent.DEBUFF);
                }else{
                    if(this.defendAttackCounter >= DEFEND_ATTACK_COUNTER){
                        setMove(monsterStrings.MOVES[1],(byte) 2, Intent.ATTACK_DEFEND, this.damage.get(1).base);
                    }else{
                        setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(0).base);
                    }
                }

                break;
        }
    }

    @Override
    public void damage(DamageInfo info) {

        if(this.posture==Posture.DYING){
            this.halfDead = true;
            return;
        }

        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {

            this.reviveCounter = 0;

            switch(this.posture){
                case NORMAL:
                    this.posture = Posture.REVIVE;
                    this.halfDead = true;

                    setMove((byte) 5, Intent.ATTACK_BUFF, this.damage.get(3).base);
                    createIntent();

                    CardCrawlGame.music.fadeOutTempBGM();
                    CardCrawlGame.music.playTempBGM(Sounds.TalulahBGM_2);

                    if(state38!=null){// 普通塔露拉的死亡动画

                        AbstractDungeon.overlayMenu.endTurnButton.disable();

                        AnimationState.AnimationStateListener changePostureAnimationListener = new AnimationState.AnimationStateListener() {
                            @Override public void complete(AnimationState.TrackEntry entry) {
                                if ("Die".equals(entry.getAnimation().getName())) {
                                    // 切换模型
                                    changeSpine();
                                    AbstractDungeon.overlayMenu.endTurnButton.enable();
                                    state38.removeListener(this);
                                }
                            }
                            @Override public void start(AnimationState.TrackEntry entry) {}
                            @Override public void interrupt(AnimationState.TrackEntry entry) {
                                if ("Die".equals(entry.getAnimation().getName())) {
                                    // 切换模型
                                    changeSpine();
                                    state38.addAnimation(0,"Idle", true, 0.8f);
                                    AbstractDungeon.overlayMenu.endTurnButton.enable();
                                    state38.removeListener(this);
                                }
                            }
                            @Override public void end(AnimationState.TrackEntry entry) {

                            }
                            @Override public void dispose(AnimationState.TrackEntry entry) {}
                            @Override public void event(AnimationState.TrackEntry entry, Event event) {}
                        };

                        state38.setAnimation(0,"Die", false);
                        state38.addListener(changePostureAnimationListener);
                    }

                    break;

                case BLACK_SNAKE_1:
                    this.posture = Posture.REVIVE;
                    this.halfDead = true;

                    setMove((byte) 5, Intent.ATTACK_BUFF, this.damage.get(3).base);
                    createIntent();

                    if(state38!=null){
                        state38.setAnimation(0,"Reborn_1_Start", false);
                        state38.addAnimation(0, "Reborn_1_Idle", true, 0.0f);
                    }

                    break;
                case BLACK_SNAKE_2:
                    this.posture = Posture.DYING;
                    //  这个halfDead不能设置为 true，会导致 DYING状态的火焰攻击代码无法触发。
                    this.halfDead = true;

                    setMove((byte) 6, Intent.ATTACK_BUFF, this.damage.get(3).base);
                    createIntent();

                    CardCrawlGame.music.fadeOutTempBGM();
                    CardCrawlGame.music.playTempBGM(Sounds.Talulah_FireRain);

                    if(state38!=null){
                        state38.setAnimation(0,"Reborn_2_Start", false);
                        state38.addAnimation(0, "Reborn_2_Idle", true, 0.0f);
                    }

                    break;
            }

        }

    }

    @Override
    public void die() {

        this.useFastShakeAnimation(1.0f);
        CardCrawlGame.screenShake.rumble(2.0f);

        if (this.posture != Posture.DYING) {
            return;
        }

        // 播放 Spine 死亡动画
        if (state38 != null) {

//            setHalfDead(true);

            // 对于 BOSS 级（独立）单位，必须设置一下暂时不要结算房间，不然结算太快，角色模型会卡
            // 对于小怪(盾卫),没有复活机制的, 可是简单设置一下 halfDead = true.否则绝影会一直选中.
            AbstractDungeon.getCurrRoom().cannotLose = true;

            AbstractDungeon.overlayMenu.endTurnButton.disable();

            AnimationState.AnimationStateListener dieAnimationListener = new AnimationState.AnimationStateListener() {

                private boolean deathHandled = false;

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
                @Override public void end(AnimationState.TrackEntry entry) {}
                @Override public void dispose(AnimationState.TrackEntry entry) {}
                @Override public void event(AnimationState.TrackEntry entry, Event event) {}

                private void handleDeath() {
                    if (deathHandled) return;  // 防止重复执行
                    deathHandled = true;

                    setHalfDead(false);

                    AbstractDungeon.overlayMenu.endTurnButton.enable();

                    AbstractDungeon.getCurrRoom().cannotLose = false;

                    superDie(); // 动画结束后再真正死亡
                    CardCrawlGame.music.fadeOutTempBGM();
                    state38.removeListener(this);
                }

            };

            state38.setAnimation(0, "Reborn_2_End", false);
            state38.addAnimation(0, "Die", false, 0.0f);
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
}

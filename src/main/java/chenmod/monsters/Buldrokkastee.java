package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.actions.NoFastWaitAction;
import chenmod.powers.BreakBlockPower_monster;
import chenmod.powers.EmbattlePower;
import chenmod.powers.LastMarchPower;
import chenmod.util.DistanceCache;
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
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;

import java.util.HashMap;
import java.util.Map;

import static com.chenmod.spine38.AnimationState.AnimationStateListener;
import static com.chenmod.spine38.AnimationState.TrackEntry;

public class Buldrokkastee extends AbstractMonster {

    public static final String ID = ChenMod.makeID(Buldrokkastee.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/Buldrokkastee/enemy_1506_patrt.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/Buldrokkastee/enemy_1506_patrt.json";
    private static final float SCALE = 1.30F; // 根据需要调整大小，通常是 1.0F 到 1.5F


    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }


    // *** Spine38 结束

    private static final int MAX_HP = 300;
    private static final int STR_AMT = 2;
    private static final int MARCH_BLOCK = 15;

    private static final int ATTACK_DAMAGE = 3;

    private static final int ATTACK_HIT_TIMES = 4;

    private static final int ATTACK_DAMAGE_2 = 17;

    private static final int SKILL_DAMAGE = 15;

    private static final int REVIVE_MAX_TURN= 3;

    private int reviveCounter = 0;  //  复活回合计数，三个回合后复活

    private int marchCounter = 4;  //  行军回合计数，五个回合后向前移动一点

    private int skillCounter = 0;

    private static final float MARCH_SPEED = 0.7f;

    private static final float MARCH_DISTANCE = 100.0f;

    private boolean firstTalk = true;

    private enum Posture {
        MARCH,  // 行军姿态
        REVIVE, // 复活
        DESTROY // 毁灭姿态
    }

    private Posture posture;

    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    public Buldrokkastee(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 450.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;

        this.posture = Posture.MARCH;

        this.marchCounter = 4;
        this.skillCounter = 0;
        this.reviveCounter = 0;

        this.flipHorizontal = true;


        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 40);
        }else{
            this.setHp(MAX_HP);
        }

        float difficulty = 1.0f;
        if(AbstractDungeon.ascensionLevel >= 4){
            difficulty *= 1.25f;
        }

        this.damage.add(new DamageInfo(this, (int)(ATTACK_DAMAGE * difficulty)));
        this.damage.add(new DamageInfo(this, (int)(ATTACK_DAMAGE_2 * difficulty)));
        this.damage.add(new DamageInfo(this, (int)(SKILL_DAMAGE * difficulty)));


        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle_1", 1.0f);
        animSpeedMap.put("Idle_2", 1.0f);
        animSpeedMap.put("Move_1", 1.0f);
        animSpeedMap.put("Move_2", 1.0f);
        animSpeedMap.put("Skill", 1.5f);
        animSpeedMap.put("Attack_1", 1.5f);
        animSpeedMap.put("Attack_2", 1.5f);

        animSpeedMap.put("revive_1", 1.0f);
        animSpeedMap.put("revive_2", 1.0f);
        animSpeedMap.put("revive_3", 1.0f);
        animSpeedMap.put("Die", 1.0f);
        // 可添加更多动画的速度配置

        loadSpine();

    }

    private void loadSpine() {
        atlas38 = new TextureAtlas(Gdx.files.internal(ATLAS_PATH));
        SkeletonJson json = new SkeletonJson(atlas38);
        json.setScale(Settings.renderScale / SCALE);
        SkeletonData data = json.readSkeletonData(Gdx.files.internal(JSON_PATH));
        skeleton38 = new Skeleton(data);
        skeleton38.setColor(Color.WHITE);

        skeleton38.setScaleX(-Math.abs(skeleton38.getScaleX()));

        stateData38 = new AnimationStateData(data);
        state38 = new AnimationState(stateData38);
        stateData38.setDefaultMix(0.2f);

        state38.setAnimation(0, "Idle_1", true);

        stateData38.setMix("Idle_1", "Attack_1", 0.1f);
        stateData38.setMix("Attack_1", "Idle_1", 0.1f);

        stateData38.setMix("Move_1", "Attack_1", 0.1f);
        stateData38.setMix("Attack_1", "Move_1", 0.1f);

        stateData38.setMix("Idle_2", "Attack_2", 0.1f);
        stateData38.setMix("Attack_2", "Idle_2", 0.1f);

        stateData38.setMix("Idle_2", "Skill", 0.1f);
        stateData38.setMix("Skill", "Idle_2", 0.1f);

        stateData38.setMix("Idle_1", "revive_1", 0.1f);
        stateData38.setMix("revive_3", "Idle_2", 0.1f);
    }

    @Override
    public void update() {
        super.update();
        if (state38 != null) {

            float baseSpeed = 2.0f; // 你原有的全局基础速度
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
            switch (this.nextMove) {
                case 1: // 攻击

                    switch(this.posture){
                        case REVIVE:
                            ChenMod.logger.info("【爱国者】正在复活阶段，为什么会攻击(type) 1?");
                            break Label_NextMove;

                        case MARCH:
                            ChenMod.logger.info("【爱国者】正在行军姿态，执行四连击");

                            AbstractDungeon.effectList.add(
                                    new TextAboveCreatureEffect(
                                            this.hb.cX,
                                            this.hb.cY + 50.0F,
                                            "四连击",
                                            Color.GOLD
                                    )
                            );

                            if (state38 != null) {
                                state38.setAnimation(0,"Attack_1", false);
                                state38.addAnimation(0, "Idle_1", true, 0.0f);
                            }
//                            CardCrawlGame.sound.playV(Sounds.attack_1_Effect_Buldrokkastee, 1.5f);

                            for (int i = 0; i < ATTACK_HIT_TIMES; i++) {
                                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0),
                                        (i % 2==0)? AbstractGameAction.AttackEffect.SLASH_VERTICAL : AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                            }

                            this.marchCounter++;

                            break Label_NextMove;

                        case DESTROY:
                            ChenMod.logger.info("【爱国者】正在毁灭姿态，执行一次重击");

                            AbstractDungeon.effectList.add(
                                    new TextAboveCreatureEffect(
                                            this.hb.cX,
                                            this.hb.cY + 50.0F,
                                            "重击",
                                            Color.GOLD
                                    )
                            );

                            if (state38!=null){
                                state38.setAnimation(0,"Attack_2", false);
                                state38.addAnimation(0, "Idle_2", true, 0.0f);
                            }
                            CardCrawlGame.sound.playV(Sounds.attack_2_Effect_Buldrokkastee, 1.5f);
                            this.skillCounter++;
                            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HEAVY));

                            break Label_NextMove;
                    }

                    break;

                case 2: // 技能攻击-投戟
                    if(this.posture !=Posture.DESTROY){
                        ChenMod.logger.info("【爱国者】不在毁灭姿态，为什么会投戟技能攻击(type) 2?");
                        break;
                    }

                    ChenMod.logger.info("【爱国者】正在毁灭姿态，执行一次投戟攻击，接下来准备new一个Action");

                    if (state38!=null){
                        state38.setAnimation(0,"Skill", false);
                        state38.addAnimation(0, "Idle_2", true, 0.0f);
                    }

                    CardCrawlGame.sound.playV(Sounds.skill_Effect_Buldrokkastee, 1.5f);

                    AbstractDungeon.effectList.add(
                            new TextAboveCreatureEffect(
                                    this.hb.cX,
                                    this.hb.cY + 50.0F,
                                    "掷戟",
                                    Color.GOLD
                            )

                    );

                    AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(1.0f));
                    AbstractDungeon.actionManager.addToBottom(
                            new VFXAction(
                                    new WeightyImpactEffect(
                                            AbstractDungeon.player.hb.cX,
                                            AbstractDungeon.player.hb.cY,
                                            new Color(0.1f, 1.0f, 0.1f, 0.0f)
                                    ),
                                    0.5f // 核心修改：将默认的0.5f改为更小的值（比如0.25f），特效速度翻倍
                            )
                    );

                    AbstractDungeon.actionManager.addToBottom(
                            new DamageAction(
                                    AbstractDungeon.player,
                                    this.damage.get(2), // 原 skillBaseDamageInfo
                                    AbstractGameAction.AttackEffect.NONE
                            )
                    );

                    AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, BreakBlockPower_monster.POWER_ID));

                    this.skillCounter = 0;

                    break;

                case 4: // 行军姿态技能，全场＋格挡

                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[0], 1.0F, 2.0F));

                    AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {

                        private boolean moveInitiated = false;
                        // 存储每个敌人的初始核心坐标drawX（不再用animX）
                        private final Map<AbstractMonster, Float> enemyInitialDrawXMap = new HashMap<>();

                        final float limitX = AbstractDungeon.player.drawX + 150.0f;

                        @Override
                        public void update() {
                            // 初始化：记录所有敌人的初始核心坐标 + 播放移动动画（只执行一次）
                            // 配置：向左移动的像素数（核心参数，你可以自由调整）
                            // 向左移动100像素，可改50/80/120等
                            float moveDistance = MARCH_DISTANCE;
                            if (!moveInitiated) {
                                moveInitiated = true;
                                ChenMod.logger.info("【全场行军】-开始：所有敌人向左移动 " + moveDistance + " 像素");

                                // 遍历当前战斗中的所有敌人
                                for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                                    // 跳过已死亡/逃脱的敌人
                                    if (monster.isDead || monster.isEscaping) {
                                        continue;
                                    }

                                    // 1. 记录该敌人的初始核心坐标drawX（统一用drawX，不再区分怪物类型）
                                    float initialDrawX = monster.drawX;

                                    if(initialDrawX <= limitX){
                                        continue;
                                    }

                                    enemyInitialDrawXMap.put(monster, initialDrawX);
                                    ChenMod.logger.info("【全场行军】-敌人[" + monster.name + "]初始核心位置：" + initialDrawX);

                                    // 2. 播放 Move 动画（原有动画逻辑完全保留）
                                    if (monster instanceof Buldrokkastee) {
                                        Buldrokkastee boss = (Buldrokkastee) monster;
                                        if (boss.state38 != null) {
                                            boss.state38.setAnimation(0, "Move_1", true);
                                        }
                                    } else if (monster instanceof ShieldGuard) {
                                        ShieldGuard enemy = (ShieldGuard) monster;
                                        if (enemy.state38 != null) {
                                            enemy.state38.setAnimation(0, "Move_Begin", false);
                                            enemy.state38.addAnimation(0, "Move_Loop", true, 0.0f);
                                        }
                                    }
                                }
                            }

                            // 核心逻辑：遍历所有敌人，基于drawX执行同步移动
                            if (!this.isDone && !enemyInitialDrawXMap.isEmpty()) {
                                boolean allEnemiesReachedTarget = true; // 标记是否所有敌人都到达目标

                                for (Map.Entry<AbstractMonster, Float> entry : enemyInitialDrawXMap.entrySet()) {
                                    AbstractMonster monster = entry.getKey();
                                    float initialDrawX = entry.getValue();

                                    // 跳过已死亡/逃脱的敌人
                                    if (monster.isDead || monster.isEscaping) {
                                        continue;
                                    }

                                    // 计算该敌人的目标核心位置（初始drawX - 移动距离）
                                    float targetDrawX = initialDrawX - moveDistance;
                                    // 当前核心位置（统一取drawX，不再依赖animX）
                                    float currentDrawX = monster.drawX;

                                    // 检查是否到达目标位置（带误差范围避免抖动）
                                    if (currentDrawX <= targetDrawX + 1.0f) {
                                        // 修正核心位置到目标值（永久修改，不会被重置）
                                        monster.drawX = targetDrawX;
                                        // 重置animX为0，避免偏移叠加（可选但建议保留）
                                        monster.animX = 0.0f;
                                        ChenMod.logger.info("【全场行军】-敌人[" + monster.name + "]已到达目标核心位置：" + targetDrawX);
                                    } else {
                                        // 未到达：持续向左移动核心坐标（修改drawX而非animX）
                                        // 恒定移动速度（像素/帧，建议1-3之间）
                                        monster.drawX -= MARCH_SPEED;
                                        // 保持animX为0，避免双重偏移
                                        monster.animX = 0.0f;
                                        allEnemiesReachedTarget = false; // 有敌人未到达，标记为false
                                        ChenMod.logger.info("【全场行军】-敌人[" + monster.name + "]移动中：当前核心位置=" + currentDrawX);
                                    }
                                }

                                // 所有敌人都到达目标后，结束整个Action
                                if (allEnemiesReachedTarget) {
                                    ChenMod.logger.info("【全场行军】-完成：所有敌人已移动到位");

                                    // 遍历所有敌人，切换回攻击/待机动画
                                    for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                                        if (monster instanceof Buldrokkastee && !monster.isDead) {
                                            Buldrokkastee boss = (Buldrokkastee) monster;
                                            if (boss.state38 != null) {
                                                boss.state38.setAnimation(0, "Attack_1", true);
                                                boss.state38.addAnimation(0, "Idle_1", true, 0.0f);
                                            }
                                            // 执行该Boss的伤害逻辑
                                            boss.doMarchDamageAndBlock(); // 假设doMarchDamageAndBlock是Boss类的方法
                                        } else if (monster instanceof ShieldGuard && !monster.isDead) {
                                            ShieldGuard enemy = (ShieldGuard) monster;
                                            if (enemy.state38 != null) {
                                                // 优化：盾卫先播放Move_End结束动画，再切Idle，动画更完整
                                                enemy.state38.setAnimation(0, "Move_End", false);
                                                enemy.state38.addAnimation(0, "Idle", true, 0.0f);
                                            }
                                        } else {
                                            ChenMod.logger.info("场上存在着不是爱国者和盾卫的怪物");
                                        }
                                    }

                                    ChenMod.logger.info("【全场行军】-即将重新绘制DistanceCache缓存");
                                    DistanceCache.rebuild();
                                    this.isDone = true;
                                    ChenMod.logger.info("【全场行军】-Action结束");
                                }
                            }
                        }
                    });

                    this.marchCounter = 0;
                    break;

                case 5: // 复活阶段
                    this.reviveCounter++;
                    this.skillCounter++;

                    if(this.reviveCounter >= REVIVE_MAX_TURN){

                        AbstractDungeon.effectList.add(
                                new TextAboveCreatureEffect(
                                        this.hb.cX,
                                        this.hb.cY + 50.0F,
                                        "毁灭姿态",
                                        Color.FIREBRICK
                                )
                        );

                        ChenMod.logger.info("【爱国者】复活，进入毁灭姿态");

                        CardCrawlGame.music.fadeOutTempBGM();
                        CardCrawlGame.music.playTempBGM(Sounds.bossBuldrokkasteeBGM2);

                        CardCrawlGame.screenShake.rumble(2.0f);
                        CardCrawlGame.sound.playV(Sounds.revive_3_Effect_Buldrokkastee, 1.5f);

                        if(state38!=null){
                            state38.setAnimation(0,"revive_3", false);
                            state38.addAnimation(0, "Idle_2", true,0.0f);
                        }

                        this.halfDead =false;
                        this.posture = Posture.DESTROY;

                        if (Settings.isEndless && AbstractDungeon.player.hasBlight("ToughEnemies")) {
                            final float mod = AbstractDungeon.player.getBlight("ToughEnemies").effectFloat();
                            this.maxHealth *= (int)mod;
                        }

                        // 清除 debuff
                        AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Shackled"));

                        AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth));
                        AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
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

                    break;

                case 6:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new EmbattlePower(this, 2)));
                    break ;

            }
        }

        StrengthPower strengthPower = (StrengthPower) this.getPower(StrengthPower.POWER_ID);

        if(strengthPower!=null && strengthPower.type== AbstractPower.PowerType.DEBUFF){
            if(this.firstTalk){
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[1], 1.0F, 2.0F));
                this.firstTalk = false;
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Strength"));
        }

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {

        ChenMod.logger.info("【爱国者状态】this.posture:"+this.posture+"; this.reviveCounter:"+this.reviveCounter+"; this.skillCounter:"+skillCounter+"; 【this.marchCounter】:"+marchCounter);

        switch(this.posture){
            case REVIVE:

                if(reviveCounter >= REVIVE_MAX_TURN - 1){
                    setMove((byte) 5, Intent.BUFF);
                }else{
                    setMove((byte) 5, Intent.UNKNOWN);
                }

                break;

            case MARCH:

                if(!this.hasPower(EmbattlePower.POWER_ID)){
                    setMove((byte) 6, Intent.BUFF);
                    return;
                }

                if(marchCounter >= 4){
                    setMove((byte) 4, Intent.ATTACK_DEFEND, this.damage.get(0).base, ATTACK_HIT_TIMES, true);
                }else{
                    setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base, ATTACK_HIT_TIMES, true);
                }
                break;

            case DESTROY:
                if(skillCounter >= 2){
                    setMove((byte) 2, Intent.ATTACK_BUFF, this.damage.get(2).base);
                    if (AbstractDungeon.ascensionLevel >= 19) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BreakBlockPower_monster(this)));
                    }else{
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BreakBlockPower_monster(this, 60)));
                    }
                }else{
                    setMove((byte) 1, Intent.ATTACK, this.damage.get(1).base);
                }
                break;
        }

    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead && this.posture == Posture.MARCH) {

            // 进入复活阶段
            this.halfDead = true;
            this.posture = Posture.REVIVE;
            this.reviveCounter = 0;

            AbstractDungeon.getCurrRoom().cannotLose = true;

            if(AbstractDungeon.ascensionLevel >= 19){
                reviveCounter = REVIVE_MAX_TURN - 2 ;
            }
            setMove((byte) 5, Intent.UNKNOWN);
            createIntent();

            if(state38!=null){
                state38.setAnimation(0,"revive_1", false);
                state38.addAnimation(0, "revive_2", true, 0.0f);
            }

        }
    }

    @Override
    public void die() {

        this.useFastShakeAnimation(1.0f);
        CardCrawlGame.screenShake.rumble(2.0f);

        CardCrawlGame.sound.playV(Sounds.revive_1_Effect_Buldrokkastee, 1.5f);

        if (this.posture != Posture.DESTROY) {  // 不是毁灭姿态下的死亡，直接重置。
            return;
        }

        // 播放 Spine 死亡动画
        if (state38 != null) {
            setHalfDead(true);

            AbstractDungeon.getCurrRoom().cannotLose = true;    // 当前房间不能结算
            AbstractDungeon.overlayMenu.endTurnButton.disable();

            AnimationStateListener dieAnimationListener = new AnimationStateListener() {

                boolean deathHandled = false;

                @Override
                public void complete(TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }

                @Override
                public void interrupt(TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }
                @Override public void start(TrackEntry entry) {}
                @Override public void end(TrackEntry entry) {}
                @Override public void dispose(TrackEntry entry) {}
                @Override public void event(TrackEntry entry, Event event) {}

                private void handleDeath() {
                    if (deathHandled) return;  // 防止重复执行
                    deathHandled = true;

                    setHalfDead(false);
                    AbstractDungeon.overlayMenu.endTurnButton.enable();
                    AbstractDungeon.getCurrRoom().cannotLose = false;

                    superDie();
                    CardCrawlGame.music.fadeOutTempBGM();
                    CardCrawlGame.music.playTempBGM(Sounds.beforeTalulahBGM);

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
        // 爱国者死后，向全场盾卫添加 “最后的行军 ”
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDead && !m.isDying && !m.id.equals(this.id)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new LastMarchPower(m)));
            }
        }

    }

    private void setHalfDead(boolean halfDead) {
        this.halfDead = halfDead;
    }

    private void doMarchDamageAndBlock(){

        AbstractDungeon.actionManager.addToBottom(new NoFastWaitAction(0.15f));

        for (int i = 0; i < ATTACK_HIT_TIMES; i++) {
            AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0),
                    (i % 2==0)? AbstractGameAction.AttackEffect.SLASH_VERTICAL : AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDying && !m.isDead && !m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, MARCH_BLOCK));
            }
        }

    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, monsterStrings.DIALOG[0], 1.0F, 2.0F));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new EmbattlePower(this, 2)));

        if(AbstractDungeon.ascensionLevel >= 19){
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, 20)));
        }else{
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, 15)));
        }

        setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base, ATTACK_HIT_TIMES, true);
        createIntent();

    }

}

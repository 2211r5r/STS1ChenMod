package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.powers.BreakBlockPower_monster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

import java.util.*;

public class Faust extends AbstractMonster {

    public static final String ID = ChenMod.makeID(Faust.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/Faust/enemy_1508_faust.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/Faust/enemy_1508_faust.json";
    private static final float SCALE = 1.50F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }

    private static final int MAX_HP = 150;

    private static final int ATTACK_DAMAGE = 11;

    private static final int ATTACK_DAMAGE_2 = 5;

    private int reviveCounter;

    private int breakAttackCounter = 4;

    private enum Posture {
        NORMAL,  // 普通姿态
        REVIVE // 复活
    }

    private Posture posture;

    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    private final List<Byte> possibleMoves = new ArrayList<>();

    public Faust(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;

        this.posture = Posture.REVIVE;
        this.halfDead = true;
        this.reviveCounter = 0;

        this.flipHorizontal = true;

        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 21);
        }else{
            this.setHp(MAX_HP);
        }

        if(AbstractDungeon.ascensionLevel >= 4){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 2));
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE_2 + 1));
        }else{
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE));
            this.damage.add(new DamageInfo(this,ATTACK_DAMAGE_2));
        }

        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Attack", 2.0f);
        animSpeedMap.put("Skill_1", 2.0f);
        animSpeedMap.put("Skill_2", 1.5f);
        animSpeedMap.put("Move", 1.0f);
        animSpeedMap.put("Die", 1.0f);

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
        stateData38.setDefaultMix(0.1f);

        state38.setAnimation(0, "Idle", true);

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
            if(this.posture == Posture.NORMAL){
                this.renderHealth(sb);
            }
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
        this.reviveCounter++;
        this.halfDead = false; // 必须先将halfDead临时设置为false,否则浮士德不会攻击

        boolean isTimeToNormal = AbstractDungeon.ascensionLevel >= 19
                ? this.reviveCounter > 4
                : this.reviveCounter > 3;

        if (isTimeToNormal) {
            if(this.posture == Posture.REVIVE){
                AbstractDungeon.effectList.add(
                        new TextAboveCreatureEffect(
                                this.hb.cX,
                                this.hb.cY + 50.0F,
                                "隐匿结束",
                                Color.WHITE
                        )
                );
            }
            this.posture = Posture.NORMAL;
        }else{
            this.posture = Posture.REVIVE;
        }

        switch (this.nextMove) {
            case 1: {   // 普通攻击

                this.breakAttackCounter++;

                if(state38 != null){
                    state38.setAnimation(0,"Attack", false);
                    state38.addAnimation(0, "Idle", true,0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            }
            case 2: {   // 双重攻击

                this.breakAttackCounter++;

                if(state38 != null){
                    state38.setAnimation(0,"Attack", false);
                    state38.addAnimation(0, "Idle", true,0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                break;
            }
            case 3: {   // 特殊攻击(破甲)
                if(state38 != null){
                    state38.setAnimation(0,"Skill_1", false);
                    state38.addAnimation(0, "Idle", true,0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

                AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, BreakBlockPower_monster.POWER_ID));

                this.breakAttackCounter = 0;
                break;
            }
            case 4: {   // 施法(易伤)
                if(state38 != null){
                    state38.setAnimation(0,"Skill_2", false);
                    state38.addAnimation(0, "Idle", true,0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 1, true), 1));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {

        boolean isTimeToNormal = AbstractDungeon.ascensionLevel >= 19
                ? this.reviveCounter > 4
                : this.reviveCounter > 3;

        if (isTimeToNormal) {
            if(this.posture == Posture.REVIVE){
                AbstractDungeon.effectList.add(
                        new TextAboveCreatureEffect(
                                this.hb.cX,
                                this.hb.cY + 50.0F,
                                "隐匿结束",
                                Color.WHITE
                        )
                );
            }
            this.posture = Posture.NORMAL;
        }else{
            this.posture = Posture.REVIVE;
            this.halfDead = true; // 必须先将halfDead临时设置为false,否则浮士德不会攻击
            AbstractDungeon.effectList.add(
                    new TextAboveCreatureEffect(
                            this.hb.cX,
                            this.hb.cY + 50.0F,
                            "隐匿",
                            Color.GOLD
                    )
            );
        }

        // 记录上一次行为
        byte lastMove = this.nextMove;
        ChenMod.logger.info("【浮士德】lastMove:"+lastMove);

        possibleMoves.clear();

        possibleMoves.add((byte)1);
        possibleMoves.add((byte)2);

        // case 3 回复：血量越少概率越高，但不超过50%，且冷却中不加入
        if (this.posture==Posture.NORMAL && this.breakAttackCounter > 4) {
            for(int i = 3; i < this.breakAttackCounter; ++i) {
                possibleMoves.add((byte)3);
            }
        }

        if(!AbstractDungeon.player.hasPower(VulnerablePower.POWER_ID)){
            possibleMoves.add((byte)4);
        }

        // 如果上一次行为在候选列表里，移除它，避免连续重复
        possibleMoves.remove(Byte.valueOf(lastMove));

        // 如果候选为空（比如刚好被移除），至少保证攻击行为存在
        if (possibleMoves.isEmpty()) {
            possibleMoves.add((byte)1);
        }

        ChenMod.logger.info("【浮士德】possibleMoveList:"+possibleMoves);
        ChenMod.logger.info("【浮士德】随机数 num:"+num);
        // 随机选择一个行为
        int index = num % possibleMoves.size();
        byte move = possibleMoves.get(index);

        ChenMod.logger.info("【浮士德】nowMove:"+move);

        // 设置怪物意图
        switch (move) {
            case 1:
                setMove(monsterStrings.MOVES[0],(byte)1, Intent.ATTACK, this.damage.get(0).base);
                break;
            case 2:
                setMove(monsterStrings.MOVES[1],(byte)2, Intent.ATTACK, this.damage.get(1).base, 2, true);
                break;
            case 3:
                setMove(monsterStrings.MOVES[2],(byte)3, Intent.ATTACK, this.damage.get(1).base);
                if (AbstractDungeon.ascensionLevel >= 19) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BreakBlockPower_monster(this)));
                }else{
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BreakBlockPower_monster(this, 60)));
                }
                break;
            case 4:
                setMove(monsterStrings.MOVES[3],(byte)4, Intent.DEBUFF);
                break;
        }
    }

    @Override
    public void damage(DamageInfo info) {
        if (this.posture == Posture.REVIVE) {
            this.halfDead = true;
            return;
        }
        super.damage(info);
    }

    @Override
    public void die() {

        this.useFastShakeAnimation(1.0f);
        CardCrawlGame.screenShake.rumble(2.0f);

        // 播放 Spine 死亡动画
        if (state38 != null) {
            setHalfDead(true);

            AbstractDungeon.getCurrRoom().cannotLose = true;    // 当前房间不能结算
            AbstractDungeon.overlayMenu.endTurnButton.disable();

            AnimationState.AnimationStateListener dieAnimationListener = new AnimationState.AnimationStateListener() {

                boolean deathHandled = false;

                @Override
                public void complete(AnimationState.TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }

                @Override
                public void interrupt(AnimationState.TrackEntry entry) {
                    if ("Die".equals(entry.getAnimation().getName())) {
                        handleDeath();
                    }
                }
                @Override public void start(AnimationState.TrackEntry entry) {}
                @Override public void end(AnimationState.TrackEntry entry) {}
                @Override public void dispose(AnimationState.TrackEntry entry) {}
                @Override public void event(AnimationState.TrackEntry entry, Event event) {}

                private void handleDeath() {
                    if (deathHandled) return;  // 防止重复执行
                    deathHandled = true;

                    setHalfDead(false);
                    AbstractDungeon.overlayMenu.endTurnButton.enable();
                    AbstractDungeon.getCurrRoom().cannotLose = false;

                    superDie();

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

}

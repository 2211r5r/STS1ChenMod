package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.powers.BreakBlockPower_monster;
import chenmod.util.Sounds;
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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mephisto extends AbstractMonster {

    public static final String ID = ChenMod.makeID(Mephisto.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/Mephisto/enemy_1507_mephi.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/Mephisto/enemy_1507_mephi.json";
    private static final float SCALE = 1.50F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }

    private static final int MAX_HP = 160;

    private static final int ATTACK_DAMAGE = 4;

    private static final int RECOVER_HP = 15;

    private static final int BLOCK = 9;

    private int recoverCounter = 0;

    private int strengthAmt = 1;

    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    private final List<Byte> possibleMoves = new ArrayList<>();

    public Mephisto(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;

        this.flipHorizontal = true;

        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 33);
        }else{
            this.setHp(MAX_HP);
        }

        if(AbstractDungeon.ascensionLevel >= 4){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 3, DamageInfo.DamageType.NORMAL));
        }else {
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE, DamageInfo.DamageType.NORMAL));
        }

        if(AbstractDungeon.ascensionLevel >= 19){
            this.strengthAmt = 2;
        }else {
            this.strengthAmt = 1;
        }

        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Attack", 2.0f);
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
            if(!this.halfDead){
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

        this.recoverCounter++;

        if(state38 != null){
            state38.setAnimation(0, "Attack", false);
            state38.addAnimation(0,"Idle", true, 0.0f);
        }

        switch (this.nextMove) {
            case 1: { // 攻击
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                break;
            }
            case 2: {   // 群体回复
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new HealAction(m, this, RECOVER_HP));
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this,  BLOCK));
                    }
                }
                this.recoverCounter = 0;
                break;
            }
            case 3: {   // 群体力量
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.strengthAmt), this.strengthAmt));
                    }
                }
                break;
            }
            case 4: {   // 弱化玩家
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 1, true), 1));
                break;
            }

            case 5: {   // 群体格挡
                for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDying && !m.isEscaping) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this,  BLOCK));
                    }
                }
               break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int needToHeal = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDying && !m.isEscaping) {
                needToHeal += m.maxHealth - m.currentHealth;
            }
        }

        // 记录上一次行为
        byte lastMove = this.nextMove;
        ChenMod.logger.info("【梅菲斯特】lastMove:"+lastMove);

        possibleMoves.clear();

        possibleMoves.add((byte)3);
        possibleMoves.add((byte)5);

        if(needToHeal > 3 * RECOVER_HP && this.recoverCounter > 4){
            int counter = (int) Math.ceil(needToHeal * 0.1d / RECOVER_HP);
            for(int i =0; i< counter ; i++){
                possibleMoves.add((byte)2);
            }
        }

        if(!AbstractDungeon.player.hasPower(FrailPower.POWER_ID)){
            possibleMoves.add((byte)4);
        }

        int aliveCount = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDying && !m.isEscaping) {
                ++aliveCount;
            }
        }

        if(aliveCount <= 1){
            possibleMoves.add((byte)1);
            possibleMoves.add((byte)1);
            possibleMoves.add((byte)1);
            possibleMoves.add((byte)1);
            possibleMoves.add((byte)1);
        }

        // 如果上一次行为在候选列表里，移除它，避免连续重复
        possibleMoves.remove(Byte.valueOf(lastMove));

        // 如果候选为空（比如刚好被移除）
        if (possibleMoves.isEmpty()) {
            possibleMoves.add((byte)5);
            possibleMoves.add((byte)1);
        }

        ChenMod.logger.info("【梅菲斯特】possibleMoveList:"+possibleMoves);
        ChenMod.logger.info("【梅菲斯特】随机数 num:"+num);
        // 随机选择一个行为
        int index = num % possibleMoves.size();
        byte move = possibleMoves.get(index);

        ChenMod.logger.info("【梅菲斯特】nowMove:"+move);

        // 设置怪物意图
        switch (move) {
            case 1:
                setMove(monsterStrings.MOVES[0],(byte)1, Intent.ATTACK, this.damage.get(0).base);
                break;
            case 2:
                setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND_BUFF);
                break;
            case 3:
                setMove(monsterStrings.MOVES[2],(byte)3, Intent.BUFF);
                break;
            case 4:
                setMove(monsterStrings.MOVES[3],(byte)4, Intent.DEBUFF);
                break;
            case 5:
                setMove(monsterStrings.MOVES[4],(byte)5, Intent.DEFEND);
                break;
        }

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

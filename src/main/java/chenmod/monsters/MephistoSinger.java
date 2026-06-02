package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.cards.DustCard;
import chenmod.powers.DustPower;
import chenmod.powers.EmbattlePower;
import chenmod.powers.FierceBurningPower;
import chenmod.powers.MephistoSingerPower;
import chenmod.util.Sounds;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MephistoSinger extends AbstractMonster {

    public static final String ID = ChenMod.makeID(MephistoSinger.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/MephistoSinger/enemy_1514_smephi.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/MephistoSinger/enemy_1514_smephi.json";
    private static final float SCALE = 1.30F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }

    private static final int MAX_HP = 400;

    private static final int ATTACK_DAMAGE = 21;

    private static final int ATTACK_DAMAGE_2 = 9;

    private static final int REVIVE_MAX_TURN= 3;

    private int reviveCounter = 0;

    private int healCooldown = 0;

    private final int recoverPerTurn;

    private enum Posture {
        NORMAL,  // 普通姿态
        REVIVE // 复活
    }

    private Posture posture;

    public final List<Float> hpPhase = new ArrayList<>();

    public int currentHpPhase;

    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    public MephistoSinger(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = EnemyType.BOSS;
        this.posture = Posture.NORMAL;
        this.flipHorizontal = true;

        if(AbstractDungeon.ascensionLevel >= 9){
            this.setHp(MAX_HP + 100);
            this.recoverPerTurn = 15;
        }else{
            this.setHp(MAX_HP);
            this.recoverPerTurn = 12;
        }

        float difficulty = 1.0f;
        if(AbstractDungeon.ascensionLevel >= 4){
            difficulty *= 1.2f;
        }

        this.damage.add(new DamageInfo(this, (int)(ATTACK_DAMAGE * difficulty)));
        this.damage.add(new DamageInfo(this, (int)(ATTACK_DAMAGE_2 * difficulty)));

        if(AbstractDungeon.ascensionLevel >= 19){
            this.hpPhase.add(0.8f);
            this.hpPhase.add(0.6f);
            this.hpPhase.add(0.4f);
            this.hpPhase.add(0.2f);
        }else{
            this.hpPhase.add(0.75f);
            this.hpPhase.add(0.5f);
            this.hpPhase.add(0.25f);
        }

        this.currentHpPhase = 0;

        animSpeedMap.put("Idle", 1.0f);

        animSpeedMap.put("Skill", 1.0f);
        animSpeedMap.put("Skill_2", 1.0f);

        animSpeedMap.put("Idle_2_begin", 1.0f);
        animSpeedMap.put("Idle_2_loop", 1.0f);
        animSpeedMap.put("Idle_2_end", 1.0f);
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

        this.healCooldown--;

        Label_NextMove:{
            switch (this.nextMove) {
                case 1: // 攻击

                    AbstractDungeon.effectList.add(
                            new TextAboveCreatureEffect(
                                    this.hb.cX,
                                    this.hb.cY + 50.0F,
                                    "打击",
                                    Color.GOLD
                            )
                    );

                    if (state38!=null){
                        state38.setAnimation(0,"Attack", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                    break;

                case 2: // 攻击

                    AbstractDungeon.effectList.add(
                            new TextAboveCreatureEffect(
                                    this.hb.cX,
                                    this.hb.cY + 50.0F,
                                    "打击",
                                    Color.GOLD
                            )
                    );

                    if (state38!=null){
                        state38.setAnimation(0,"Attack", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

                    break;

                case 3: // 技能攻击

                    if (state38!=null){
                        state38.setAnimation(0,"Skill_2", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DustPower(AbstractDungeon.player, 2+this.currentHpPhase), 2+this.currentHpPhase));

                    break;

                case 4: // 回复

                    if (state38!=null){
                        state38.setAnimation(0,"Skill", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }
                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!m.isDead && !m.isDying) {
                            AbstractDungeon.actionManager.addToBottom(new HealAction(m, m, (int) ((m.maxHealth - m.currentHealth) * 0.15f )));
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 3), 3));
                        }
                    }

                    this.healCooldown = 4;

                    break;

                case 5:

                    if (state38!=null){
                        state38.setAnimation(0,"Skill_2", false);
                        state38.addAnimation(0, "Idle", true, 0.0f);
                    }

                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new DustCard(), 1, true, false, false, Settings.WIDTH * 0.3f, Settings.HEIGHT / 2.0f));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new DustCard(), 1, true, false, false, Settings.WIDTH * 0.45f, Settings.HEIGHT / 2.0f));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new DustCard(), 1, true, false, false, Settings.WIDTH * 0.6f, Settings.HEIGHT / 2.0f));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new DustCard(), 1, true, false, false, Settings.WIDTH * 0.75f, Settings.HEIGHT / 2.0f));

                    break ;

                case 6: // 复活阶段

                    this.reviveCounter++;
                    this.halfDead =false;

                    if(this.reviveCounter >= REVIVE_MAX_TURN){

                        this.reviveCounter = 0;

                        AbstractDungeon.effectList.add(
                                new TextAboveCreatureEffect(
                                        this.hb.cX,
                                        this.hb.cY + 50.0F,
                                        "休眠结束",
                                        Color.GOLD
                                )
                        );

                        ChenMod.logger.info("【梅菲斯特·歌者】休眠姿态结束");

                        if(state38!=null){
                            state38.setAnimation(0,"Idle_2_end", false);
                            state38.addAnimation(0, "Idle", true,0.0f);
                        }

                        this.posture =Posture.NORMAL;

                        AbstractDungeon.actionManager.addToBottom(new RemoveDebuffsAction(this));
                        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this, this, "Shackled"));

                    }else{
                        AbstractDungeon.actionManager.addToBottom(
                                new HealAction(this, this, this.recoverPerTurn)
                        );
                        AbstractDungeon.actionManager.addToBottom(
                                new ApplyPowerAction(this, this, new StrengthPower(this, 1), 1)
                        );
                        AbstractDungeon.effectList.add(
                                new TextAboveCreatureEffect(
                                        this.hb.cX,
                                        this.hb.cY + 50.0F,
                                        "休眠阶段",
                                        Color.GREEN
                                )
                        );
                    }
                    break;

                case 7:
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MephistoSingerPower(this)));
                    break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {

        if(!this.hasPower(MephistoSingerPower.POWER_ID)){
            setMove((byte) 7, Intent.BUFF);
            return;
        }

        if(this.posture == Posture.REVIVE){
            if(this.reviveCounter >= REVIVE_MAX_TURN - 1){
                setMove((byte) 6, Intent.BUFF);
            }else{
                setMove((byte) 6, Intent.UNKNOWN);
            }

            return;
        }

        // 记录上一次行为
        byte lastMove = this.nextMove;
        List<Byte> possibleMoves = new ArrayList<>();

        // case 1 和 case 2 永远可选
        possibleMoves.add((byte)1);
        possibleMoves.add((byte)2);

        if(!AbstractDungeon.player.hasPower(DustPower.POWER_ID)){
            possibleMoves.add((byte)3);
        }

        if (this.healCooldown <= 0) {
            possibleMoves.add((byte)4);
        }

        // case 4 Debuff：只有玩家有 DustPower 时才可能出现
        if (AbstractDungeon.player.hasPower(DustPower.POWER_ID) && AbstractDungeon.player.currentHealth > 1) {
            possibleMoves.add((byte)5);
        }

        // 如果上一次行为在候选列表里，移除它，避免连续重复
        possibleMoves.remove(Byte.valueOf(lastMove));

        // 如果候选为空（比如刚好被移除），至少保证攻击行为存在
        if (possibleMoves.isEmpty()) {
            possibleMoves.add((byte)1);
        }

        // 随机选择一个行为
        int index = num % possibleMoves.size();
        byte move = possibleMoves.get(index);

        // 设置怪物意图
        switch (move) {
            case 1:
                setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
                break;
            case 2:
                setMove((byte)2, Intent.ATTACK, this.damage.get(1).base, 2, true);
                break;
            case 3:
                setMove((byte)3, Intent.DEBUFF);
                break;
            case 4:
                setMove((byte)4, Intent.BUFF);
                break;
            case 5:
                setMove((byte)5, Intent.DEBUFF);
                break;
        }
    }


    @Override
    public void damage(DamageInfo info) {
        ChenMod.logger.info("【梅菲斯特·歌者】收到了伤害当前血量阶段"+this.currentHpPhase+",总阶段为："+this.hpPhase.size());

        super.damage(info);

        if(this.currentHpPhase >= this.hpPhase.size()){
            ChenMod.logger.info("【梅菲斯特·歌者】当前血量阶段已达上限:当前血量阶段"+this.currentHpPhase+",总阶段为："+this.hpPhase.size());
            return;
        }

        if (this.currentHealth <= (int) (this.maxHealth * this.hpPhase.get(currentHpPhase)) && !this.halfDead) {

            AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, (int) (this.maxHealth * this.hpPhase.get(currentHpPhase) - this.currentHealth)));
            // 添加对玩家的debuff
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DustPower(AbstractDungeon.player, 2+this.currentHpPhase), 2+this.currentHpPhase));
            ChenMod.logger.info("【梅菲斯特·歌者】的血量阶段为："+(this.currentHpPhase)+" 释放技能debuff"+",总阶段为："+this.hpPhase.size());

            // 进入复活阶段
            this.halfDead = true;
            this.posture = Posture.REVIVE;

            if (this.currentHpPhase < this.hpPhase.size()) {
                this.currentHpPhase++;
            }

            setMove((byte) 6, Intent.UNKNOWN);
            createIntent();

            if (state38 != null) {
                AnimationState.TrackEntry current = state38.getCurrent(0); // 获取第0轨道的当前动画
                String currentAnim = (current != null) ? current.getAnimation().getName() : null;

                if (!"Idle_2_loop".equals(currentAnim)) {
                    state38.setAnimation(0, "Idle_2_begin", false);
                    state38.addAnimation(0, "Idle_2_loop", true, 0.0f);
                }
            }

        }
    }

    @Override
    public void die() {

        this.useFastShakeAnimation(1.0f);
        CardCrawlGame.screenShake.rumble(2.0f);

        CardCrawlGame.sound.playV(Sounds.revive_1_Effect_Buldrokkastee, 1.5f);

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
                    AbstractDungeon.actionManager.addToBottom(new HealAction(AbstractDungeon.player, AbstractDungeon.player, (int)(AbstractDungeon.player.maxHealth * 0.5f)));
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

    @Override
    public void usePreBattleAction() {

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MephistoSingerPower(this)));
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));

    }
}

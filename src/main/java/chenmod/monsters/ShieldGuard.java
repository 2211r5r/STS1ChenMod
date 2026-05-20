package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.actions.ShieldGuardMarchAction;
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
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

import java.util.HashMap;
import java.util.Map;

public class ShieldGuard extends AbstractMonster {

    public static final String ID = ChenMod.makeID(ShieldGuard.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/ShieldGuard/enemy_1081_sotisd.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/ShieldGuard/enemy_1081_sotisd.json";
    private static final float SCALE = 1.35F; // 根据需要调整大小，通常是 1.0F 到 1.5F

    // --- Spine 3.8 核心变量 ---
    protected TextureAtlas atlas38;
    protected Skeleton skeleton38;
    public AnimationState state38;
    protected AnimationStateData stateData38;
    protected static final PolygonSpriteBatch psb = new PolygonSpriteBatch();
    protected static final SkeletonRenderer sr = new SkeletonRenderer();
    static { sr.setPremultipliedAlpha(true); }
    private final Map<String, Float> animSpeedMap = new HashMap<>(); // 动画速度哈希表

    // --- 角色基础属性 ---
    private static final int MAX_HP = 180;
    private static final int BLOCK = 15;
    private static final int ARMOR_BLOCK = 12;
    private static final int ATTACK_DAMAGE = 7;
    private static final int ATTACK_DAMAGE_MARCH = 10;

    private static final int DEFEND_TURN_COUNTER = 3;

    private int defendCounter = 0;

    public ShieldGuard(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 350.0F, null, offsetX, offsetY);

        this.type = EnemyType.ELITE;

        if(AbstractDungeon.ascensionLevel >= 8){
            this.setHp(MAX_HP + 20);
        }else{
            this.setHp(MAX_HP);
        }

        float difficulty = 1.0f;
        if(AbstractDungeon.ascensionLevel >= 3){
            difficulty *= 1.25f;
        }

        this.damage.add(new DamageInfo(this, (int) (ATTACK_DAMAGE * difficulty)));
        this.damage.add(new DamageInfo(this, (int) (ATTACK_DAMAGE_MARCH * difficulty)));

        this.flipHorizontal = true;

        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Move_Begin", 1.0f);
        animSpeedMap.put("Move_Loop", 1.0f);
        animSpeedMap.put("Move_End", 1.0f);
        animSpeedMap.put("Attack", 1.5f);
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

        state38.setAnimation(0, "Idle", true);

        stateData38.setMix("Idle", "Attack", 0.1f);
        stateData38.setMix("Attack", "Idle", 0.1f);

        stateData38.setMix("Idle", "Move_Begin", 0.1f);
        stateData38.setMix("Move_End", "Idle", 0.1f);

        stateData38.setMix("Move_End", "Attack", 0.1f);

        stateData38.setMix("Idle", "Die", 0.1f);
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
        switch (this.nextMove){
            case 1: //  攻击
                this.defendCounter++;
                if (state38 != null) {
                    state38.setAnimation(0,"Attack", false);
                    state38.addAnimation(0, "Idle", true, 0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break ;

            case 2: // 防御

                int maxArmorBlock = AbstractDungeon.ascensionLevel >=18? ARMOR_BLOCK : ARMOR_BLOCK + 3;

                if(this.hasPower(PlatedArmorPower.POWER_ID)){

                    int gap = maxArmorBlock - this.getPower(PlatedArmorPower.POWER_ID).amount;

                    if(gap > 0){
                        this.addToBot(new ApplyPowerAction(this, this, new PlatedArmorPower(this, gap), gap));
                    }

                }else{
                    this.addToBot(new ApplyPowerAction(this, this, new PlatedArmorPower(this, maxArmorBlock), maxArmorBlock));
                }

                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, BLOCK));
                this.defendCounter = 0;
                break;

            case 3: // 施法-嘲讽

                this.defendCounter++;
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new EmbattlePower(this, 1)));

                break;

            case 4: // 行军！！

                AbstractDungeon.actionManager.addToBottom(
                        new ShieldGuardMarchAction(
                                this,
                                -80.0f,   // 向左移动 80 像素
                                0.0f,     // Y 不变
                                this.damage.get(0).base,       // 行军后造成伤害
                                BLOCK         // 行军后获得的格挡
                        )
                );
                DistanceCache.rebuild();

                // 日后写 “行军”移动的时候，记得要调用，也要记得重建距离缓存
                if(this.hasPower(EmbattlePower.POWER_ID)){
                    this.getPower(EmbattlePower.POWER_ID).updateDescription();
                }

                break;
        }

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));

    }

    @Override
    protected void getMove(int i) {

        if(!this.hasPower(EmbattlePower.POWER_ID)){
            setMove(monsterStrings.MOVES[2],(byte) 3, Intent.BUFF);
            return;
        }

        if(this.hasPower(LastMarchPower.POWER_ID)){
            setMove(monsterStrings.MOVES[3],(byte) 4, Intent.ATTACK_DEFEND, this.damage.get(0).base);
            return;
        }

        if (AbstractDungeon.ascensionLevel >= 18) {
            if (!this.lastMove((byte)2) && !this.lastMoveBefore((byte)2)) {
                this.setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND_BUFF);
                return;
            }
        }
        else {
            if (i < 50) {
                this.setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND_BUFF);
                return;
            }
        }
        if (this.lastTwoMoves((byte)1)) {
            this.setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND_BUFF);
        }
        else {
            this.setMove(monsterStrings.MOVES[0],(byte)1, Intent.ATTACK, this.damage.get(0).base);
        }

    }

    @Override
    public void die(){

        if(state38 != null){
            state38.setAnimation(0,"Die", false);
        }

        super.die();
    }

    @Override
    public void usePreBattleAction() {
        // 战斗开始时，为自己添加 “ 列阵！！！” 的 buff
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new EmbattlePower(this, 1)));
        setMove((byte) 1, Intent.ATTACK, this.damage.get(0).base);
        createIntent();
    }

}

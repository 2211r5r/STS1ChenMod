package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.powers.SnowMonsterTeamPower;
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
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.HashMap;
import java.util.Map;

public class MercenarySarkazSoldier extends AbstractMonster{

    public static final String ID = ChenMod.makeID(MercenarySarkazSoldier.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/MercenarySarkazSoldier/enemy_1084_sotidm_3.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/MercenarySarkazSoldier/enemy_1084_sotidm_3.json";
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

    // --- 角色基础属性 ---
    private static final int MAX_HP = 119;
    private static final int BLOCK = 15;
    private static final int ATTACK_DAMAGE = 15;
    private static final int ATTACK_DAMAGE_2 = 4;
    private static final int DEFEND_TURN_COUNTER = 3;

    private int hitTimes= 2;

    private int defendCounter = 0;

    public MercenarySarkazSoldier(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = AbstractMonster.EnemyType.ELITE;

        if(AbstractDungeon.ascensionLevel >= 8){
            this.setHp(MAX_HP + 16);
        }else{
            this.setHp(MAX_HP);
        }

        if(AbstractDungeon.ascensionLevel >= 3){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 4));
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE_2 + 1 ));
        }else{
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE ));
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE_2 ));
        }

        this.flipHorizontal = true;

        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle_2", 1.0f);
        animSpeedMap.put("Move_2", 1.0f);
        animSpeedMap.put("Attack_2", 1.5f);
        animSpeedMap.put("Die_2", 1.0f);
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
        stateData38.setDefaultMix(0.1f);

        state38.setAnimation(0, "Idle_2", true);

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
                this.intent != AbstractMonster.Intent.NONE &&
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
                    state38.setAnimation(0,"Attack_2", false);
                    state38.addAnimation(0, "Idle_2", true, 0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                break ;

            case 2: //  攻击
                this.defendCounter++;
                if (state38 != null) {
                    state38.setAnimation(0,"Attack_2", false);
                    state38.addAnimation(0, "Idle_2", true, 0.0f);
                }

                for(int i=0; i < this.hitTimes; i++){
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }

                break ;

            case 3: // 防御
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(this, this, BLOCK, AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, BLOCK));

                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 1, true),1));


                if(hitTimes < 2){
                    hitTimes = 2;
                }

                hitTimes++;

                this.defendCounter = 0;
                break;

        }

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));

    }

    @Override
    protected void getMove(int i) {

        if(defendCounter >= DEFEND_TURN_COUNTER - 1){
            setMove(monsterStrings.MOVES[2], (byte) 3, Intent.DEFEND_DEBUFF);
            return;
        }

        if (AbstractDungeon.ascensionLevel >= 18) {
            if (this.lastMove((byte)3)) {
                this.setMove(monsterStrings.MOVES[1], (byte)2, Intent.ATTACK, this.damage.get(1).base, this.hitTimes, true);
            }else{
                this.setMove(monsterStrings.MOVES[0],(byte)1, Intent.ATTACK, this.damage.get(0).base);
            }
            return;
        }
        else {
            if (i < 33) {
                this.setMove(monsterStrings.MOVES[1], (byte)2, Intent.ATTACK, this.damage.get(1).base, this.hitTimes, true);
                return;
            }
        }
        if (this.lastTwoMoves((byte)1)) {
            this.setMove(monsterStrings.MOVES[1], (byte)2, Intent.ATTACK, this.damage.get(1).base, this.hitTimes, true);
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

        if (AbstractDungeon.ascensionLevel >= 18) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, 2),2));
        }

        this.setMove(monsterStrings.MOVES[1], (byte)2, Intent.ATTACK, this.damage.get(1).base, this.hitTimes, true);
        createIntent();
    }

}

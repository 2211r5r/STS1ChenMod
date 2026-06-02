package chenmod.monsters;

import basemod.ReflectionHacks;
import chenmod.ChenMod;
import chenmod.cards.DustCard;
import chenmod.powers.DustPower;
import chenmod.powers.LastMarchPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.chenmod.spine38.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuerrillaHerald extends AbstractMonster {

    public static final String ID = ChenMod.makeID(GuerrillaHerald.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final String ATLAS_PATH = "chenmod/images/monsters/animation/GuerrillaHerald/enemy_1080_sotidp.atlas";
    private static final String JSON_PATH = "chenmod/images/monsters/animation/GuerrillaHerald/enemy_1080_sotidp.json";
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
    private static final int MAX_HP = 75;
    private static final int ATTACK_DAMAGE = 15;
    private static final int ATTACK_DAMAGE_2 = 8;

    private int blockAmt = 1;
    private int improvedStrengthAmt = 0;

    private int coolDown = 0;

    public GuerrillaHerald(float offsetX, float offsetY) {
        super(monsterStrings.NAME, ID, MAX_HP, 0.0F, 0.0F, 220.0F, 300.0F, null, offsetX, offsetY);

        this.type = AbstractMonster.EnemyType.ELITE;

        if(AbstractDungeon.ascensionLevel >= 8){
            this.setHp(MAX_HP + 10);
        }else{
            this.setHp(MAX_HP);
        }

        if(AbstractDungeon.ascensionLevel >= 3){
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE + 2));
        }else{
            this.damage.add(new DamageInfo(this, ATTACK_DAMAGE));
        }

        this.flipHorizontal = true;

        // 示例配置：可根据你的动画名称修改
        animSpeedMap.put("Idle", 1.0f);
        animSpeedMap.put("Move", 1.0f);
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

        this.coolDown--;

        switch (this.nextMove){

            case 1: //  打击
                if (state38 != null) {
                    state38.setAnimation(0,"Attack", false);
                    state38.addAnimation(0, "Idle", true, 0.0f);
                }
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                break ;

            case 2: // 传令

                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDead && !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new PlatedArmorPower(m, this.blockAmt), this.blockAmt));
                    }
                }

                break ;

            case 3: // 士气大涨

                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDead && !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 1), 1));
                    }
                }
                this.improvedStrengthAmt++;
                break;

        }

        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));

    }

    @Override
    protected void getMove(int i) {

        int aliveCount = 0;
        for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDying && !m.isEscaping) {
                ++aliveCount;
            }
        }
        if(aliveCount <= 1){
            setMove(monsterStrings.MOVES[0],(byte) 1, Intent.ATTACK, this.damage.get(0).base);
            return;
        }

        byte lastMove = this.nextMove;
        List<Byte> possibleMoves = new ArrayList<>();

        // case 2 和 case 3 永远可选
        possibleMoves.add((byte)2);

        if(this.coolDown <= 0){
            possibleMoves.add((byte)3);
        }

        // 如果上一次行为在候选列表里，移除它，避免连续重复
        possibleMoves.remove(Byte.valueOf(lastMove));

        // 如果候选为空（比如刚好被移除），至少保证攻击行为存在
        if (possibleMoves.isEmpty()) {
            possibleMoves.add((byte)2);
        }

        // 随机选择一个行为
        int index = i % possibleMoves.size();
        byte move = possibleMoves.get(index);

        // 设置怪物意图
        switch (move) {
            case 1://  打击
                setMove(monsterStrings.MOVES[0], (byte)1, Intent.ATTACK, this.damage.get(0).base);
                break;
            case 2://  传令
                setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND);
                break;
            case 3:// 士气大涨
                setMove(monsterStrings.MOVES[2],(byte) 3, Intent.BUFF);
                break;

        }

    }

    @Override
    public void die(){

        if(state38 != null){
            state38.setAnimation(0,"Die", false);
        }

        if(this.improvedStrengthAmt > 0){
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDead && !m.isDying && !m.id.equals(this.id)) {
                    AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(m, m, StrengthPower.POWER_ID, this.improvedStrengthAmt));
                }
            }
        }

        super.die();
    }

    @Override
    public void usePreBattleAction() {
        if(AbstractDungeon.ascensionLevel >= 18){

            AbstractDungeon.effectList.add(
                    new TextAboveCreatureEffect(
                            this.hb.cX,
                            this.hb.cY + 50.0F,
                            monsterStrings.MOVES[2],
                            Color.FIREBRICK
                    )
            );

            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDead && !m.isDying) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 2), 2));
                }
            }
        }

        setMove(monsterStrings.MOVES[1],(byte)2, Intent.DEFEND);
        createIntent();
    }
}
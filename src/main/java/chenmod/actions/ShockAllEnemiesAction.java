package chenmod.actions;

import chenmod.util.DistanceCache;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class ShockAllEnemiesAction extends AbstractGameAction {

    public int[] damage;
    private int baseDamage;
    private boolean firstFrame = true;
    private boolean utilizeBaseDamage = false;
    private final boolean upgraded;   // 是否升级（决定倍率是否封顶）

    private final static float TIMES_LIMIT = 1.5f;

    public ShockAllEnemiesAction(AbstractCreature source, int[] amount, DamageInfo.DamageType type,
                                 AttackEffect effect, boolean upgraded) {
        this.source = source;
        this.damage = amount;
        this.damageType = type;
        this.attackEffect = effect;
        this.upgraded = upgraded;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public ShockAllEnemiesAction(AbstractPlayer player, int baseDamage, DamageInfo.DamageType type,
                                 AttackEffect effect, boolean upgraded) {
        this(player, null, type, effect, upgraded);
        this.baseDamage = baseDamage;
        this.utilizeBaseDamage = true;
    }

    @Override
    public void update() {

        if (firstFrame) {

            // 1. 如果使用 baseDamage，则创建矩阵
            if (utilizeBaseDamage) {
                this.damage = DamageInfo.createDamageMatrix(this.baseDamage, true);
            }

            // 2. 应用距离倍率（使用最新 DistanceCache）
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

                if (!m.isDeadOrEscaped()) {
                    float times = upgraded
                            ? DistanceCache.getTimesFromMax(m)
                            : Math.min(DistanceCache.getTimesFromMax(m), TIMES_LIMIT);
                    this.damage[i] = (int) Math.ceil(this.damage[i] * times);
                }
            }

            // 3. 播放攻击特效（与原版一致）
            boolean playedMusic = false;
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    if (playedMusic) {
                        AbstractDungeon.effectList.add(
                                new FlashAtkImgEffect(m.hb.cX, m.hb.cY, this.attackEffect, true)
                        );
                    } else {
                        playedMusic = true;
                        AbstractDungeon.effectList.add(
                                new FlashAtkImgEffect(m.hb.cX, m.hb.cY, this.attackEffect)
                        );
                    }
                }
            }

            firstFrame = false;
        }

        this.tickDuration();

        if (this.isDone) {

            // 4. 通知玩家的 Power（如怒火、火焰屏障等）
            for (AbstractPower p : AbstractDungeon.player.powers) {
                p.onDamageAllEnemies(this.damage);
            }

            // 5. 逐个怪物造成伤害
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

                if (!m.isDeadOrEscaped()) {

                    // 特效颜色（与原版一致）
                    if (this.attackEffect == AttackEffect.POISON) {
                        m.tint.color.set(Color.CHARTREUSE);
                        m.tint.changeColor(Color.WHITE.cpy());
                    } else if (this.attackEffect == AttackEffect.FIRE) {
                        m.tint.color.set(Color.RED);
                        m.tint.changeColor(Color.WHITE.cpy());
                    }

                    m.damage(new DamageInfo(this.source, this.damage[i], this.damageType));
                }
            }

            // 6. 清理战斗
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }

            if (!Settings.FAST_MODE) {
                this.addToTop(new WaitAction(0.1F));
            }
        }
    }
}

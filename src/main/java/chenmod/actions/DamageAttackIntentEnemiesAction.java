package chenmod.actions;

import chenmod.character.ChenCharacter;
import chenmod.util.Sounds;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;


public class DamageAttackIntentEnemiesAction extends AbstractGameAction {

    public int[] damage;
    private int baseDamage;
    private boolean firstFrame;
    private boolean utilizeBaseDamage;

    public DamageAttackIntentEnemiesAction(final AbstractCreature source, final int[] amount, final DamageInfo.DamageType type, final AttackEffect effect, final boolean isFast) {
        this.firstFrame = true;
        this.utilizeBaseDamage = false;
        this.source = source;
        this.damage = amount;
        this.actionType = ActionType.DAMAGE;
        this.damageType = type;
        this.attackEffect = effect;
        if (isFast) {
            this.duration = Settings.ACTION_DUR_XFAST;
        }
        else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
    }

    public DamageAttackIntentEnemiesAction(final AbstractCreature source, final int[] amount, final DamageInfo.DamageType type, final AttackEffect effect) {
        this(source, amount, type, effect, false);
    }

    public DamageAttackIntentEnemiesAction(final AbstractPlayer player, final int baseDamage, final DamageInfo.DamageType type, final AttackEffect effect) {
        this(player, null, type, effect, false);
        this.baseDamage = baseDamage;
        this.utilizeBaseDamage = true;
    }

    @Override
    public void update() {
        if (this.firstFrame) {

            // ① 检查是否存在攻击意图的怪物
            boolean hasAttackIntent = false;
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped() && mo.getIntentBaseDmg() >= 0) {
                    hasAttackIntent = true;
                    break;
                }
            }

            // ② 如果没有攻击意图 → 直接结束，不播放动画、不造成伤害
            if (!hasAttackIntent) {
                this.isDone = true;
                return;
            }

            // ③ 存在攻击意图 → 播放动画
            boolean playedMusic = false;
            if (this.utilizeBaseDamage) {
                this.damage = DamageInfo.createDamageMatrix(this.baseDamage);
            }

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped() && mo.getIntentBaseDmg() >= 0) {
                    if (playedMusic) {
                        AbstractDungeon.effectList.add(
                                new FlashAtkImgEffect(mo.hb.cX, mo.hb.cY, this.attackEffect, true)
                        );
                    } else {
                        playedMusic = true;
                        AbstractDungeon.effectList.add(
                                new FlashAtkImgEffect(mo.hb.cX, mo.hb.cY, this.attackEffect)
                        );
                    }
                }
            }

            // ④ 陈角色的攻击动画
            if (this.source instanceof ChenCharacter) {
                ((ChenCharacter) this.source).useSkillAttackAnimation();
            }

            CardCrawlGame.sound.play(Sounds.qiaoJiEffect);

            this.firstFrame = false;
        }

        // ⑤ 等待动画结束
        this.tickDuration();

        if (this.isDone) {

            // ⑥ 触发玩家的 onDamageAllEnemies
            for (AbstractPower p : AbstractDungeon.player.powers) {
                p.onDamageAllEnemies(this.damage);
            }

            // ⑦ 对所有攻击意图怪物造成伤害
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster mo = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);

                if (!mo.isDeadOrEscaped() && mo.getIntentBaseDmg() >= 0) {

                    if (this.attackEffect == AttackEffect.POISON) {
                        mo.tint.color.set(Color.CHARTREUSE);
                        mo.tint.changeColor(Color.WHITE.cpy());
                    } else if (this.attackEffect == AttackEffect.FIRE) {
                        mo.tint.color.set(Color.RED);
                        mo.tint.changeColor(Color.WHITE.cpy());
                    }

                    mo.damage(new DamageInfo(this.source, this.damage[i], this.damageType));
                }
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }

            if (!Settings.FAST_MODE) {
                this.addToTop(new WaitAction(0.1f));
            }
        }
    }

}

package chenmod.actions;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.effects.Chen3IllustrationEffect;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class TianKuiAction extends AbstractGameAction {

    private final float percent; // 6%
    private final int baseMinDamage; // 卡牌面板上的最少伤害（未加力量）

    private boolean isFirstEffect = true;

    private final AbstractCreature source;

    private final AbstractCreature target;

    public TianKuiAction(AbstractCreature target, int baseMinDamage, int percent) {
        this.source = player;
        this.target = target;
        this.actionType = ActionType.DAMAGE;

        this.duration = AbstractGameAction.DEFAULT_DURATION;

        this.baseMinDamage = baseMinDamage;

        this.percent = percent * 0.01f;

    }

    @Override
    public void update() {

        if(isFirstEffect){
            if (player instanceof ChenCharacter) {
                // 第二步：安全强转（100%不会报错）
                CardCrawlGame.sound.play(Sounds.tianKuiVoice);

                ChenCharacter p = (ChenCharacter) player;

                p.changeSpine38ToChen3(()->{
                    if (target == null || target.isDeadOrEscaped()) {
                        this.isDone = true;
                        return;
                    }

                    p.useSkillAttackAnimation();

                    int base = baseMinDamage;

                    DamageInfo newInfo;

                    int percentBase = (int)(target.maxHealth * percent);

                    if(percentBase >= baseMinDamage){
                        base = percentBase;
                        newInfo = new DamageInfo(target, base, DamageInfo.DamageType.NORMAL);
                        newInfo.applyPowers(source, target);
                    }else{
                        newInfo = new DamageInfo(source, base, DamageInfo.DamageType.NORMAL);
                    }

                    AbstractDungeon.actionManager.addToTop(
                            new DamageAction(target, newInfo, AttackEffect.SLASH_HEAVY)
                    );

                    this.isDone = true;
                });

            }else{
                if (target == null || target.isDeadOrEscaped()) {
                    this.isDone = true;
                    return;
                }

                int base = baseMinDamage;

                DamageInfo newInfo;

                int percentBase = (int)(target.maxHealth * percent);

                if(percentBase >= baseMinDamage){
                    base = percentBase;
                    newInfo = new DamageInfo(target, base, DamageInfo.DamageType.NORMAL);
                    newInfo.applyPowers(source, target);
                }else{
                    newInfo = new DamageInfo(source, base, DamageInfo.DamageType.NORMAL);
                }

                AbstractDungeon.actionManager.addToTop(
                        new DamageAction(target, newInfo, AttackEffect.SLASH_HEAVY)
                );

                this.isDone = true;
            }

            isFirstEffect = false;
        }
    }
}

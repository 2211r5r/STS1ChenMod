package chenmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class ExhaustRandomAttackCardAndBuffAction extends AbstractGameAction {

    private final AbstractPlayer p;
    private final AbstractCard sourceCard; // 用来 +1 magicNumber 的那张牌

    public ExhaustRandomAttackCardAndBuffAction(AbstractCard sourceCard) {
        this.p = AbstractDungeon.player;
        this.sourceCard = sourceCard;
        this.actionType = ActionType.EXHAUST;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        // 第一次进入 update()
        if (this.duration == this.startDuration) {

            // 1. 收集所有攻击牌
            ArrayList<AbstractCard> attackCards = new ArrayList<>();
            for (AbstractCard c : p.hand.group) {
                if (c.type == AbstractCard.CardType.ATTACK && c != sourceCard) {
                    attackCards.add(c);
                }
            }

            // 没有攻击牌 → 直接结束
            if (attackCards.isEmpty()) {
                this.isDone = true;
                return;
            }

            // 2. 随机选择一张攻击牌
            AbstractCard toExhaust = attackCards.get(
                    AbstractDungeon.cardRandomRng.random(attackCards.size() - 1)
            );

            // 3. 消耗该牌（原生 ExhaustAction 也是直接 moveToExhaustPile）
            p.hand.moveToExhaustPile(toExhaust);

            // 4. 本牌的次数 +1
            sourceCard.baseMagicNumber = sourceCard.upgraded
                    ? sourceCard.baseMagicNumber + 1
                    : sourceCard.baseMagicNumber;

            sourceCard.magicNumber = sourceCard.baseMagicNumber;

            sourceCard.baseDamage += toExhaust.baseDamage;
            sourceCard.damage = sourceCard.baseDamage;

            sourceCard.isMagicNumberModified = true;
            sourceCard.isDamageModified = true;

            sourceCard.superFlash();
            sourceCard.applyPowers();
            sourceCard.initializeDescription();

            // 5. 结束
            this.isDone = true;
            return;
        }

        this.tickDuration();
    }
}
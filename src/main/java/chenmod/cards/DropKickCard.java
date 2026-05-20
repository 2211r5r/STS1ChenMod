package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DropKickCard extends BaseCard {

    public static final String ID = makeID(DropKickCard.class.getSimpleName());

    private static final CardType TYPE = CardType.ATTACK;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;
    private static final int COST = 1;

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 4;

    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, TYPE, RARITY, TARGET, COST
    );

    public DropKickCard() {
        super(ID, info);

        if (ChenModConfig.DEBUG_MODE) {
            setDamage(99, 1);
        } else {
            setDamage(DAMAGE, UPG_DAMAGE);
        }

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成 !D! 点伤害
        addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT
        ));

        // 2. 若目标有负面状态，则摸 1 张牌并获得 1 能量
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {

                boolean hasDebuff = false;

                // 检查目标是否有负面状态（DEBUFF）
                for (AbstractPower pow : m.powers) {
                    if (pow.type == AbstractPower.PowerType.DEBUFF) {
                        hasDebuff = true;
                        break;
                    }
                }

                if (hasDebuff) {
                    // 摸 1 张牌
                    addToTop(new DrawCardAction(p, 1));
                    // 获得 1 点能量
                    addToTop(new GainEnergyAction(1));
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            this.upgraded = true;
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeDamage(UPG_DAMAGE); // 应用伤害升级
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DropKickCard();
    }
}
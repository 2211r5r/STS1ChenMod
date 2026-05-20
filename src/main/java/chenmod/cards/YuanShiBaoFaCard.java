package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YuanShiBaoFaCard extends BaseCard {
    public static final String ID = makeID(YuanShiBaoFaCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.SPECIAL, // 稀有度
            CardTarget.ALL_ENEMY,
            -2 // 基础费用
    );

    private static final int BASE_MAGIC = 8; // 扣血量
    private static final int UPG_MAGIC = 2;
    private static final int BASE_DAMAGE = 45;   // 基础伤害
    private static final int UPG_DAMAGE = 10;    // 升级增加伤害

    public YuanShiBaoFaCard() {
        super(ID, info);
        setMagic(BASE_MAGIC, UPG_MAGIC);
        setDamage(BASE_DAMAGE, UPG_DAMAGE);
        this.exhaust = true;
    }

    public YuanShiBaoFaCard(boolean isUpgrade) {
        super(ID, info);
        setMagic(BASE_MAGIC, UPG_MAGIC);
        setDamage(BASE_DAMAGE, UPG_DAMAGE);
        this.upgraded = false;
        if(isUpgrade){
            this.upgrade();
        }
        this.exhaust = true;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return this.dontTriggerOnUseCard;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToBot(new LoseHPAction(p, p, this.magicNumber));
            this.addToBot(new DamageAllEnemiesAction(
                    p,
                    DamageInfo.createDamageMatrix(this.damage, true),
                    DamageInfo.DamageType.NORMAL,
                    AbstractGameAction.AttackEffect.FIRE
            ));
            this.dontTriggerOnUseCard = false;
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }


    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPG_DAMAGE);
            upgradeMagicNumber(UPG_MAGIC);
            upgraded = true;
        }
    }

    // ========== 卡牌复制 ==========
    @Override
    public YuanShiBaoFaCard makeCopy() {
        return new YuanShiBaoFaCard();
    }

}
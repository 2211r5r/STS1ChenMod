package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.TowerFiercePower;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TowerFierceCard extends BaseCard{
    public static final String ID = makeID(TowerFierceCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, // 卡牌颜色
            CardType.STATUS, // 卡牌类型
            CardRarity.SPECIAL, // 稀有度
            CardTarget.NONE, // 目标
            -2 // 基础费用
    );

    private static final int BASE_MAGIC = 5;

    private static final int UPG_MAGIC = 5;

    public TowerFierceCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC, UPG_MAGIC);

        this.isInnate = true;  // 固有(游戏开始时的首轮抽牌直接拿到)

        this.selfRetain = true; // 保留

    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m){
        return false;
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void triggerWhenDrawn() {
        for (final AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if(!mo.hasPower(TowerFiercePower.POWER_ID)){
                this.addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new TowerFiercePower(mo, this.magicNumber), this.magicNumber, true));
            }
        }
    }

    public void triggerWhenCopied() {
        for (final AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if(!mo.hasPower(TowerFiercePower.POWER_ID)){
                this.addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new TowerFiercePower(mo, this.magicNumber), this.magicNumber, true));
            }
        }
    }

    @Override
    public void triggerOnExhaust(){
        for (final AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if(mo.hasPower(TowerFiercePower.POWER_ID)){
                this.addToBot(new ReducePowerAction(mo, AbstractDungeon.player, TowerFiercePower.POWER_ID, this.magicNumber));
            }
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）

            upgradeMagicNumber(UPG_MAGIC);
            this.isInnate = true;
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TowerFierceCard();
    }
}

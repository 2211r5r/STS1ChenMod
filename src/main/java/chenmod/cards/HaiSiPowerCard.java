package chenmod.cards;

import chenmod.ChenMod;
import chenmod.actions.HaiSiAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.HaiSiPower;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class HaiSiPowerCard extends BaseCard{
    public static final String ID = makeID(HaiSiPowerCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            CardColor.COLORLESS, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            -1 // 基础费用
    );
    private static final int BASE_MAGIC = 12;
    private static final int UPG_MAGIC = 8;

    public HaiSiPowerCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC, UPG_MAGIC);

        this.isEthereal = true;

    }

    @Override
    public void applyPowers(){
        super.applyPowers();

        AbstractPlayer p = AbstractDungeon.player;

        if(p!=null){

            int effect = EnergyPanel.getCurrentEnergy();

            if (p.hasRelic("Chemical X")) {
                effect += 2;
            }

            int addMaxHp = this.magicNumber * effect;

            this.rawDescription = "虚无 . NL 增加 !M! X ("+(addMaxHp)+"点) 最大生命值 NL (可保留至多3点增量). NL 受到伤害而失去体力时改为失去等额最大生命值.";
            initializeDescription();

        }

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot(new HaiSiAction(p, this.magicNumber, this.freeToPlayOnce, this.energyOnUse));

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）

            upgradeMagicNumber(UPG_MAGIC);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HaiSiPowerCard();
    }
}

package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.powers.LieZhenPower;
import chenmod.powers.LieZhenPower_temp;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LieZhenCard extends BaseCard{
    public static final String ID = makeID(LieZhenCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            2 // 基础费用
    );

    private static final int BASE_REDUCE_DAMAGE = 2;


    public LieZhenCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_REDUCE_DAMAGE);

        tags.add(CustomTags.DEFEND);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));
        this.addToBot(new ApplyPowerAction(
                    p, p,
                    new LieZhenPower(p, this.magicNumber)
            ));

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeBaseCost(1);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LieZhenCard();
    }
}

package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.DoubleSwordsPower;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DoubleSwordsCard extends BaseCard{
    public static final String ID = makeID(DoubleSwordsCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            1 // 基础费用
    );

    private static final int BASE_MAGIC = 3;
    private static final int UPG_MAGIC = 3;
    public DoubleSwordsCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC,UPG_MAGIC);
        tags.add(CustomTags.CHIXIAO);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));

        this.addToBot(new ApplyPowerAction(
                p, p,
                new DoubleSwordsPower(p, this.magicNumber),
                this.magicNumber
        ));

    }

    @Override
    public void triggerWhenCopied(){

        AbstractPower power = AbstractDungeon.player.getPower(DoubleSwordsPower.POWER_ID);

        if(power != null && power.amount > 0) {
            this.addToBot(
                    new DoubleSwordsAction(
                            new DamageInfo(AbstractDungeon.player, power.amount, DamageInfo.DamageType.NORMAL)
                    ));
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPG_MAGIC);
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        DoubleSwordsCard copy = new DoubleSwordsCard();
        if (this.upgraded) {
            copy.upgrade();
        }
        return copy;
    }
}

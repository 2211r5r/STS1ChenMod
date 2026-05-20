package chenmod.cards;

import chenmod.actions.DoubleSwordsAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.BenYePower;
import chenmod.powers.DoubleSwordsPower;
import chenmod.powers.MultiHitPower;
import chenmod.powers.MultiHitPower_temp;
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

public class BenYeCard extends BaseCard {
    public static final String ID = makeID(BenYeCard.class.getSimpleName());

    // 卡牌基础属性：2费、技能、稀有度不常见、目标自己
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            0
    );

//    // 核心数值：初始打2次，升级后打3次
//    private static final int BASE_HIT_TIMES = 2;
//    private static final int UPG_HIT_TIMES = 1;

    private static final int BASE_MAGIC = 3;

    public BenYeCard() {
        super(ID, info);
        // 魔法数值：对应本地化的!M!占位符（显示打出次数）
        setMagic(BASE_MAGIC);

        this.exhaust = true;

        tags.add(CustomTags.CHIXIAO);
        tags.add(CustomTags.DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.skillVoicePool));

        this.addToBot(new ApplyPowerAction(p, p, new BenYePower(p, this.magicNumber), this.magicNumber));

//        if(upgraded){
//            this.addToBot(new ApplyPowerAction(
//                    p, p,
//                    new MultiHitPower(p, this.magicNumber), // 可跨回合的多重之力
//                    this.magicNumber
//            ));
//        }else{
//            this.addToBot(new ApplyPowerAction(
//                    p, p,
//                    new MultiHitPower_temp(p, this.magicNumber), // 当前回合的多重之力
//                    this.magicNumber
//            ));
//        }


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

    // 升级逻辑：打出次数+1，更新描述
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgraded = true;
            this.exhaust = false;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BenYeCard();
    }
}
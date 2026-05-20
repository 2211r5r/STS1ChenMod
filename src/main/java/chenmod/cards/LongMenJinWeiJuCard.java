package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

public class LongMenJinWeiJuCard extends BaseCard{
    public static final String ID = makeID(LongMenJinWeiJuCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.POWER, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            2 // 基础费用
    );

    // 核心数值：未升级/升级后的各项属性加成（严格对应你的描述）
    // 力量：未升级1，升级后2
    private static final int BASE_STRENGTH = 1;
    private static final int UPG_STRENGTH = 1; // 升级+1，1+1=2
    // 敏捷：未升级1，升级后2（和力量逻辑一致）
    private static final int BASE_DEXTERITY = 1;
    private static final int UPG_DEXTERITY = 1;
    // 荆棘：未升级3，升级后6
    private static final int BASE_THORNS = 3;
    private static final int UPG_THORNS = 3; // 升级+3，3+3=6
    // 金币：未升级25，升级后45
    private static final int BASE_GOLD = 25;
    private static final int UPG_GOLD = 10; // 升级+20，25+20=45


    public LongMenJinWeiJuCard() {
        super(ID, info); // 调用父类构造方法

        tags.add(CustomTags.DEFEND);

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.powerVoicePool));

        // 1. 施加力量：根据是否升级取对应数值（统一逻辑）
        int strengthValue = this.upgraded ? (BASE_STRENGTH + UPG_STRENGTH) : BASE_STRENGTH;
        this.addToBot(new ApplyPowerAction(
                p, p,
                new StrengthPower(p, strengthValue),
                strengthValue
        ));

        // 2. 施加敏捷：和力量逻辑完全统一
        int dexterityValue = this.upgraded ? (BASE_DEXTERITY + UPG_DEXTERITY) : BASE_DEXTERITY;
        this.addToBot(new ApplyPowerAction(
                p, p,
                new DexterityPower(p, dexterityValue),
                dexterityValue
        ));

        // 3. 施加荆棘：统一逻辑，和其他属性保持一致
        int thornsValue = this.upgraded ? (BASE_THORNS + UPG_THORNS) : BASE_THORNS;
        this.addToBot(new ApplyPowerAction(
                p, p,
                new ThornsPower(p, thornsValue),
                thornsValue
        ));

        // 4. 获得金币：统一逻辑，无例外
        int goldValue = this.upgraded ? (BASE_GOLD + UPG_GOLD) : BASE_GOLD;
        CardCrawlGame.sound.play("GOLD_JINGLE");
        this.addToBot(new GainGoldAction(goldValue));
        AbstractDungeon.effectList.add(new RainingGoldEffect(goldValue));
    }

    // 卡牌升级逻辑（严格对应你提供的升级效果，逻辑统一）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            // 无需升级魔法值（取消绑定），仅更新描述即可
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LongMenJinWeiJuCard();
    }
}
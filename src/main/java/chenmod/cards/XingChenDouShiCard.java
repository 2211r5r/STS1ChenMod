package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class XingChenDouShiCard extends BaseCard{
    public static final String ID = makeID(XingChenDouShiCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.ATTACK, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF_AND_ENEMY, // 目标
            2 // 基础费用
    );

    private static final int BASE_ARM = 7;

    private static final int UPG_ARM = 2;

    private static final int BASE_DAMAGE = 3;

    private static final int UPG_DAMAGE = 1;

    private static final int DAMAGE_COUNT = 3;

    public XingChenDouShiCard() {
        super(ID, info); // 调用父类构造方法

        setDamage(BASE_DAMAGE, UPG_DAMAGE);

        setCustomVar("BuffCount", BASE_ARM, UPG_ARM);

        setMagic(DAMAGE_COUNT);

        tags.add(CustomTags.MULTIPLE_ATTACKS);

    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));

        if(p.hasPower(PlatedArmorPower.POWER_ID)){

            int gap = this.customVar("BuffCount") - p.getPower(PlatedArmorPower.POWER_ID).amount;

            if(gap > 0){
                this.addToBot(new ApplyPowerAction(p, p, new PlatedArmorPower(p, gap), gap));
            }

        }else{
            this.addToBot(new ApplyPowerAction(p, p, new PlatedArmorPower(p, this.customVar("BuffCount")), this.customVar("BuffCount")));
        }

        for(int i = 0; i < this.magicNumber; i++){
            if(m.isDeadOrEscaped())
                return;

            addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT
            ));
        }

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）

            upgradeDamage(UPG_DAMAGE);
            upgradeCustomVar("BuffCount",UPG_ARM);

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new XingChenDouShiCard();
    }
}

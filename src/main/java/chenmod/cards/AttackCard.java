package chenmod.cards;

import chenmod.character.ChenCharacter;
import chenmod.util.CardStats;
import chenmod.util.ChenModConfig;
import chenmod.util.CustomTags;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AttackCard extends BaseCard {
    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(AttackCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色（铁clad）
            AbstractCard.CardType.ATTACK, // 卡牌类型（攻击）
            AbstractCard.CardRarity.BASIC, // 稀有度（初始牌）
            AbstractCard.CardTarget.ENEMY, // 目标（单个敌人）
            1 // 基础费用
    );

    // 攻击伤害
    private static final int DAMAGE = 3;
    // 升级后伤害
    private static final int UPG_DAMAGE = 1;

    private static final int BASE_MAGIC = 2;

    public AttackCard() {
        super(ID, info); // 调用父类构造方法
        // 设置基础伤害，升级后增加伤害
        if(ChenModConfig.DEBUG_MODE){
            setDamage(99, 1);
        }else{
            setDamage(DAMAGE, UPG_DAMAGE);
        }

        setMagic(BASE_MAGIC);

        tags.add(CardTags.STARTER_STRIKE);
        tags.add(CardTags.STRIKE);
        tags.add(CustomTags.MULTIPLE_ATTACKS);
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        CardCrawlGame.sound.play(Sounds.attackCardEffect);
        CardCrawlGame.sound.play(Sounds.getRandomVoiceString(Sounds.attackVoicePool));

        if (p instanceof ChenCharacter) {
            // 第二步：安全强转（100%不会报错）
            ChenCharacter player = (ChenCharacter) p;
            player.useAttackAnimation();
        }

        for(int i = 0; i < this.magicNumber; ++i){
            addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, damage, DamageInfo.DamageType.NORMAL),
                    i % 2 ==0 ? AbstractGameAction.AttackEffect.SLASH_HORIZONTAL: AbstractGameAction.AttackEffect.SLASH_VERTICAL // 横批
            ));
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeDamage(UPG_DAMAGE); // 应用伤害升级
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new AttackCard();
    }
}
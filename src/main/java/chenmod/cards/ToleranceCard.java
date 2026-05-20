package chenmod.cards;


import chenmod.character.ChenCharacter;
import chenmod.powers.BreakBlockPower_monster;
import chenmod.powers.BreakBlockPower_player;
import chenmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ToleranceCard extends BaseCard {

    // 卡牌ID（必须唯一，格式：modID:卡牌名）
    public static final String ID = makeID(ToleranceCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色（铁clad）
            CardType.SKILL, // 卡牌类型（攻击）
            CardRarity.UNCOMMON, // 稀有度（初始牌）
            CardTarget.SELF, // 目标（单个敌人）
            1 // 基础费用
    );

    // 攻击伤害
    private static final int BASE_MAGIC = 10;
    // 升级后伤害
    private static final int UPG_MAGIC = 5;

    public ToleranceCard() {
        super(ID, info); // 调用父类构造方法
        setMagic(BASE_MAGIC,UPG_MAGIC);
        this.exhaust=true;
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        final int count = AbstractDungeon.player.hand.size();

        this.addToBot(new ApplyPowerAction(p, p, new BreakBlockPower_player(p, count * this.magicNumber)));

        for (int i = 0; i < count; ++i) {
            if (Settings.FAST_MODE) {
                this.addToTop(new ExhaustAction(1, true, true, false, Settings.ACTION_DUR_XFAST));
            }
            else {
                this.addToTop(new ExhaustAction(1, true, true));
            }
        }

        this.addToBot(new PressEndTurnButtonAction());

    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）
            upgradeMagicNumber(UPG_MAGIC); // 应用伤害升级
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ToleranceCard();
    }

}

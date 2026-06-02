package chenmod.powers;

import chenmod.ChenMod;
import chenmod.character.ChenCharacter;
import chenmod.util.CustomTags;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class BridgePower extends BasePower {

    public static final String POWER_ID = ChenMod.makeID(BridgePower.class.getSimpleName());

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 能力类型：BUFF / DEBUFF / NEUTRAL
    private static final PowerType TYPE = PowerType.BUFF;
    // 是否回合结束移除
    private static final boolean TURN_BASED = false;

    private final ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();

    public BridgePower(AbstractCreature owner, final int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, Math.max(1, amount));
        initChiXiaoCardList();
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    // 你可以在这里添加 onInflictDamage / onAttacked / atEndOfTurn 等逻辑
    @Override
    public void atStartOfTurn() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flash();
            for (int i = 0; i < this.amount; ++i) {
                this.addToBot(new MakeTempCardInHandAction(getChiXiaoCard().makeCopy(), 1, false));
            }
        }
    }

    public void initChiXiaoCardList(){
        for (AbstractCard c : CardLibrary.getAllCards()) {
            // 过滤：只要这个颜色的卡
            if (c.color == ChenCharacter.Meta.CARD_COLOR && !c.hasTag(AbstractCard.CardTags.HEALING) && c.hasTag(CustomTags.CHIXIAO)) {
                list.add(c);
            }
        }
    }

    public AbstractCard getChiXiaoCard() {
        return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }
}
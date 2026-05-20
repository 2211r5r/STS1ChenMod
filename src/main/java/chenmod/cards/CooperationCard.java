package chenmod.cards;

import chenmod.ChenMod;
import chenmod.actions.CooperationAction;
import chenmod.character.ChenCharacter;
import chenmod.powers.LiberationPower;
import chenmod.util.CardStats;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseDexterityPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class CooperationCard extends BaseCard{
    public static final String ID = makeID(CooperationCard.class.getSimpleName());
    // 卡牌基础属性配置
    private static final CardStats info = new CardStats(
            ChenCharacter.Meta.CARD_COLOR, // 卡牌颜色
            CardType.SKILL, // 卡牌类型
            CardRarity.UNCOMMON, // 稀有度
            CardTarget.SELF, // 目标
            -1 // 基础费用
    );

    private static final int BASE_MAGIC = 2;

    private static final int UPG_MAGIC = 1;

    private boolean isUsed;

    public CooperationCard() {
        super(ID, info); // 调用父类构造方法

        setMagic(BASE_MAGIC,UPG_MAGIC);

        this.exhaust = true;

        this.isUsed = false;

    }

    @Override
    public void triggerWhenDrawn() {

        AbstractPlayer p = AbstractDungeon.player;

        this.isUsed = false;

        ChenMod.logger.info("Drawn Cooperation Card, this.freeToPlayOnce ="+this.freeToPlayOnce);

        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber),this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.magicNumber), this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber),this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new LoseDexterityPower(p, this.magicNumber), this.magicNumber));
        }
    }

    public void triggerWhenCopied() {
        AbstractPlayer p = AbstractDungeon.player;

        this.isUsed = false;

        ChenMod.logger.info("Copied Cooperation Card, this.freeToPlayOnce ="+this.freeToPlayOnce);

        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber),this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.magicNumber), this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, this.magicNumber),this.magicNumber));
            this.addToBot(new ApplyPowerAction(p, p, new LoseDexterityPower(p, this.magicNumber), this.magicNumber));
        }
    }

    // 卡牌触发效果（核心逻辑）
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.isUsed = true;
        ChenMod.logger.info("Using Cooperation Card, this.isUsed ="+ this.isUsed);
    }

    @Override
    public void triggerOnExhaust() {

        ChenMod.logger.info("Exhaust Cooperation Card, this.freeToPlayOnce ="+this.freeToPlayOnce);

        AbstractPlayer p = AbstractDungeon.player;

        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {

            int effect = EnergyPanel.totalCount;

            if (this.isUsed && this.energyOnUse != -1) {
                effect = this.energyOnUse;
            }

            if (p.hasRelic("Chemical X")) {
                effect += 2;
                p.getRelic("Chemical X").flash();
            }

            if (upgraded){
                effect ++;
            }

            ChenMod.logger.info("Cooperation Action, this.freeToPlayOnce ="+this.freeToPlayOnce);
            ChenMod.logger.info("Cooperation Action,  EnergyPanel.totalCount ="+ EnergyPanel.totalCount);
            ChenMod.logger.info("Cooperation Action, this.effect ="+effect);

            if (effect > 0) {

                CardCrawlGame.sound.playV(Sounds.cooperationActionVoice, 1.2f);
                this.addToBot(new ReducePowerAction(p,p, LoseDexterityPower.POWER_ID,effect));
                this.addToBot(new ReducePowerAction(p,p, LoseStrengthPower.POWER_ID,effect));

                if (this.isUsed && !this.freeToPlayOnce ) {
                    ChenMod.logger.info("不是被使用且不是免费使用，要扣能量");
                    p.energy.use(EnergyPanel.totalCount);
                }else{
                    ChenMod.logger.info("不用扣能量");
                }
            }
        }
    }

    // 卡牌升级逻辑（可选，若需自定义升级行为）
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName(); // 升级卡牌名称（自动添加+号）

            upgradeMagicNumber(UPG_MAGIC);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();

            upgraded = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CooperationCard();
    }
}

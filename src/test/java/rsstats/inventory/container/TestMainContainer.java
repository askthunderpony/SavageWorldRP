package rsstats.inventory.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rsstats.data.ExtendedPlayer;
import rsstats.inventory.SkillsInventory;
import rsstats.inventory.StatsInventory;
import rsstats.inventory.WearableInventory;
import rsstats.inventory.tabs_inventory.TabHostInventory;
import rsstats.inventory.tabs_inventory.TabInventory;
import rsstats.items.ExpItem;
import rsstats.items.SkillItem;
import rsstats.items.StatItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Minecraft.class, Entity.class, ExtendedPlayer.class/*, Utils.class*/})
@PowerMockIgnore("javax.management.*")
public class TestMainContainer {
    EntityPlayer player;
    StatsInventory statsInventory;
    SkillsInventory skillsInventory;
    InventoryPlayer inventoryPlayer;
    MainContainer mainContainer;

    @Before
    public void setup() {
        player = PowerMockito.mock(EntityPlayer.class); // Мокаем игрока
        when(player.getEntityData()).thenReturn(new NBTTagCompound()); // Пустой компаунд даты игрока
        inventoryPlayer = new InventoryPlayer(player); // Используем мок
        player.inventory = inventoryPlayer;

        ExtendedPlayer extendedPlayer = PowerMockito.mock(ExtendedPlayer.class);
        PowerMockito.when(ExtendedPlayer.get(player)).thenReturn(extendedPlayer);

        Minecraft mc = PowerMockito.mock(Minecraft.class); // Мокаем класс игры
        PowerMockito.mockStatic(Minecraft.class);
        PowerMockito.when(Minecraft.getMinecraft()).thenReturn(mc);

        initStatsAndSkills();

        TabInventory tabInventory = new TabInventory("TabInventory", 27, player);
        TabHostInventory tabHostInventory = new TabHostInventory("TabHostInventor", 9, tabInventory);
        mainContainer = new MainContainer(
                player,
                inventoryPlayer,
                statsInventory,
                skillsInventory,
                new WearableInventory(ExtendedPlayer.get(player)),
                tabHostInventory,
                tabInventory
        );
    }

    @Test
    public void TestStatUpAndDown() {

        // Прокачиваем стату от нуля до максимума
        mainContainer.statUp(statsInventory.getStackInSlot(0));
        mainContainer.statUp(statsInventory.getStackInSlot(0));
        mainContainer.statUp(statsInventory.getStackInSlot(0));
        mainContainer.statUp(statsInventory.getStackInSlot(0));
        mainContainer.statUp(statsInventory.getStackInSlot(0));
        assertEquals(4, mainContainer.getSlot(9).getStack().getItemDamage());
        // И немного сверх нормы
        for (int i = 0; i < 100; i++) {
            mainContainer.statUp(statsInventory.getStackInSlot(0));
        }
        assertEquals(4, mainContainer.getSlot(9).getStack().getItemDamage());

        // Прокачиваем стату с максимума до нуля
        mainContainer.statDown(statsInventory.getStackInSlot(0));
        mainContainer.statDown(statsInventory.getStackInSlot(0));
        mainContainer.statDown(statsInventory.getStackInSlot(0));
        mainContainer.statDown(statsInventory.getStackInSlot(0));
        mainContainer.statDown(statsInventory.getStackInSlot(0));
        assertEquals(0, mainContainer.getSlot(9).getStack().getItemDamage());
        // И немного сверх нормы
        for (int i = 0; i < 100; i++) {
            mainContainer.statDown(statsInventory.getStackInSlot(0));
        }
        assertEquals(0, mainContainer.getSlot(9).getStack().getItemDamage());
    }

    @Test
    public void TestSlotClick() {
        // Тестируем клик на слот с двумя очками прокачки в инвентаре. В итоге ...
        inventoryPlayer.addItemStackToInventory(new ItemStack(new ExpItem("ExpItem"), 2));
        int upgradePrice = mainContainer.getUpgradePrice(statsInventory.getStackInSlot(0));
        mainContainer.slotClick(9, 1, 0, player);
        assertEquals(2, upgradePrice); // ... вычисляется цена прокачки
        assertEquals(1, mainContainer.getSlot(9).getStack().getItemDamage()); // ... стата прокачивается
        for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryPlayer.getStackInSlot(i);
            if (itemStack != null) {
                fail(); // ... в инвентаре игрока не остается очков прокачки
            }
        }
    }

    /**
     * Выставляет стандартный начальный билд
     */
    public void initStatsAndSkills() {
        statsInventory = new StatsInventory(player);
        skillsInventory = new SkillsInventory(player);

        int i = 0;
        statsInventory.setInventorySlotContents(i++, new ItemStack(new StatItem(null, "StrengthStatItem", "rsstats:strenght", "item.StrengthStatItem")));
        statsInventory.setInventorySlotContents(i++, new ItemStack(new StatItem(null, "AgilityStatItem", "rsstats:agility", "item.AgilityStatItem")));
        statsInventory.setInventorySlotContents(i++, new ItemStack(new StatItem(null, "IntelligenceStatItem", "rsstats:intelligence", "item.IntelligenceStatItem")));
        statsInventory.setInventorySlotContents(i++, new ItemStack(new StatItem(null, "EnduranceStatItem", "rsstats:endurance", "item.EnduranceStatItem")));
        statsInventory.setInventorySlotContents(i++, new ItemStack(new StatItem(null, "CharacterStatItem", "rsstats:character", "item.CharacterStatItem")));

        i = 0;
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "EquitationSkillItem", "rsstats:skills/Equitation", "item.EquitationSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "LockpickingSkillItem", "rsstats:skills/Lockpicking", "item.LockpickingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "DrivingSkillItem", "rsstats:skills/Driving", "item.DrivingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "FightingSkillItem", "rsstats:skills/Fighting", "item.FightingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "DisguiseSkillItem", "rsstats:skills/Disguise", "item.DisguiseSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "ThrowingSkillItem", "rsstats:skills/Throwing", "item.ThrowingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "PilotingSkillItem", "rsstats:skills/Piloting", "item.PilotingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "SwimmingSkillItem", "rsstats:skills/Swimming", "item.SwimmingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "ShootingSkillItem", "rsstats:skills/Shooting", "item.ShootingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "ShippingSkillItem", "rsstats:skills/Shipping", "item.ShippingSkillItem", (StatItem) statsInventory.getStackInSlot(1).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "GamblingSkillItem", "rsstats:skills/Gambling", "item.GamblingSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "PerceptionSkillItem", "rsstats:skills/Perception", "item.PerceptionSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "SurvivalSkillItem", "rsstats:skills/Survival", "item.SurvivalSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "TrackingSkillItem", "rsstats:skills/Tracking", "item.TrackingSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "MedicineSkillItem", "rsstats:skills/Medicine", "item.MedicineSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "ProvocationSkillItem", "rsstats:skills/Provocation", "item.ProvocationSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "InvestigationSkillItem", "rsstats:skills/Investigation", "item.InvestigationSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "RepearSkillItem", "rsstats:skills/Repear", "item.RepearSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "StreetFlairSkillItem", "rsstats:skills/StreetFlair", "item.StreetFlairSkillItem", (StatItem) statsInventory.getStackInSlot(2).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "IntimidationSkillItem", "rsstats:skills/Intimidation", "item.IntimidationSkillItem", (StatItem) statsInventory.getStackInSlot(4).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "DiplomacySkillItem", "rsstats:skills/Diplomacy", "item.DiplomacySkillItem", (StatItem) statsInventory.getStackInSlot(4).getItem())));
        skillsInventory.setInventorySlotContents(i++, new ItemStack(new SkillItem(null, "ClimbingSkillItem", "rsstats:skills/Climbing", "item.ClimbingSkillItem", (StatItem) statsInventory.getStackInSlot(0).getItem())));
        skillsInventory.setSkillsFor("item.StrengthStatItem");
    }
}

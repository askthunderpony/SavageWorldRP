package rsstats.client.gui;

import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import rsstats.inventory.container.MainContainer;

/**
 * GUI для основного окна мода, содержащее информацию о персонаже (имя, уровень, здоровье, защита, харизма,
 * стойкость), панель предметов и панели статов, навыков и перков.
 * @author RareScrap
 */
public class MainMenuGUI extends InventoryEffectRenderer {
    /** Расположение фона GUI */
    public static final ResourceLocation background =
            new ResourceLocation("rsstats","textures/gui/StatsAndInvTab.png");

    /** Длина GUI в пикселях. Defined as  float, passed as int. */
    private float xSizeFloat;
    /** Ширина GUI в пикселях. Defined as  float, passed as int. */
    private float ySizeFloat;

    public MainMenuGUI(EntityPlayer player, InventoryPlayer inventoryPlayer) {
        super(new MainContainer(player, inventoryPlayer));
        this.allowUserInput = true;
    }

    public MainMenuGUI() {
        super(new MainContainer());
        this.allowUserInput = true;
    }

    /**
     * Draws the screen and all the components in it
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.xSizeFloat = (float) mouseX;
        this.ySizeFloat = (float) mouseY;
    }

    /**
     * Свой аналог {@link #drawTexturedModalRect(int, int, int, int, int, int)}, способный работать с текстурами
     * разрешением более чем 256x256.
     * @param x Координата начала отрисовки относительно левого-верхнего угла экрана игрока
     * @param y Координата начала отрисовки относительно левого-верхнего угла экрана игрока
     * @param u Координата начала текстуры по оси X относительно левого-верхнего угла текстуры
     * @param v Координата начала текстуры по оси Y относительно левого-верхнего угла текстуры
     * @param width Ширина текстуры, которую нужно отрисовать
     * @param height Высота текстуры, которую нужно отрисовать
     * @param textureWidth Общая ширина текстуры (кол-во пикселей в файле)
     * @param textureHeight Общая высота текстуры (кол-во пикселей в файле)
     */
    // Взято отсюда: http://www.minecraftforge.net/forum/topic/20177-172-gui-cant-more-than-256256/
    private void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x), (double)(y + height), 0, (double)((float)(u) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), 0, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y), 0, (double)((float)(u + width) * f), (double)((float)(v) * f1));
        tessellator.addVertexWithUV((double)(x), (double)(y), 0, (double)((float)(u) * f), (double)((float)(v) * f1));
        tessellator.draw();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     * @param partialTicks
     * @param mouseX
     * @param mouseY
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GL11.glScalef(2.0F, 2.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);

        this.xSize = 339;///2;
        this.ySize = 211;///2;
        //339 211
        int k = this.guiLeft/1 - xSize/4;
        int l = this.guiTop/1 - ySize/4;

        drawTexturedRect(k, l, 0, 0, xSize, ySize, 512, 512);

        // Орисовываем превью игрока
        drawPlayerModel(k+30, l+90, /*17*/ 40, (float)(k + 51) - this.xSizeFloat, (float)(l + 75 - 50) - this.ySizeFloat, this.mc.thePlayer);

        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            /*if (slot.getHasStack() && slot.getSlotStackLimit()==1)
            {*/
            	this.drawTexturedModalRect(k+slot.xDisplayPosition, l+slot.yDisplayPosition, 200, 0, 16, 16);
            //}
        }
    }

    /**
     * Отрисовывает превью игрока
     * @param x TODO
     * @param y TODO
     * @param scale Маштаб модели
     * @param yaw TODO
     * @param pitch TODO
     * @param playerdrawn TODO
     */
    public static void drawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase playerdrawn) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 50.0F);
        GL11.glScalef((float)(-scale), (float)scale, (float)scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = playerdrawn.renderYawOffset;
        float f3 = playerdrawn.rotationYaw;
        float f4 = playerdrawn.rotationPitch;
        float f5 = playerdrawn.prevRotationYawHead;
        float f6 = playerdrawn.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        playerdrawn.renderYawOffset = (float)Math.atan((double)(yaw / 40.0F)) * 20.0F;
        playerdrawn.rotationYaw = (float)Math.atan((double)(yaw / 40.0F)) * 40.0F;
        playerdrawn.rotationPitch = -((float)Math.atan((double)(pitch / 40.0F))) * 20.0F;
        playerdrawn.rotationYawHead = playerdrawn.rotationYaw;
        playerdrawn.prevRotationYawHead = playerdrawn.rotationYaw;
        GL11.glTranslatef(0.0F, playerdrawn.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(playerdrawn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        playerdrawn.renderYawOffset = f2;
        playerdrawn.rotationYaw = f3;
        playerdrawn.rotationPitch = f4;
        playerdrawn.prevRotationYawHead = f5;
        playerdrawn.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}

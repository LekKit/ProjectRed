package mrtjp.projectred.transportation

import codechicken.lib.data.MCDataInput
import codechicken.lib.packet.PacketCustom
import codechicken.lib.render.FontUtils
import cpw.mods.fml.relauncher.{Side, SideOnly}
import mrtjp.core.color.Colors_old
import mrtjp.core.gui._
import mrtjp.core.vec.{Point, Size}
import mrtjp.projectred.core.libmc.PRResources
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumChatFormatting

class GuiChipUpgrade(container:ChipUpgradeContainer) extends NodeGui(container, 176, 200)
{
    override def onAddedToParent_Impl()
    {
        val inst = new NodeButtonMC
        inst.position = Point(size.width/2-20, 56)
        inst.size = Size(40, 15)
        inst.clickDelegate = {() => sendMessage("inst")}
        addChild(inst)

        val linfo = NodeDotSelect.centered(67, 45)
        linfo.tooltipBuilder = { list =>
            if (container.chachedChip != null)
            {
                val b = container.chachedChip.upgradeBus
                if (b.maxL > 0)
                {
                    list += "L slot"
                    list += (EnumChatFormatting.GRAY.toString+"Latency: "+b.LLatency)
                    list += ""
                    list += (EnumChatFormatting.GRAY.toString+b.Linfo)
                    list += (EnumChatFormatting.YELLOW.toString+b.Lformula)
                }
                else list += "not upgradable"
            }
            else list += "not upgradable"
        }
        addChild(linfo)

        val rinfo = NodeDotSelect.centered(110, 45)
        rinfo.tooltipBuilder = { list =>
            if (container.chachedChip != null)
            {
                val b = container.chachedChip.upgradeBus
                if (b.maxR > 0)
                {
                    list += "R slot"
                    list += (EnumChatFormatting.GRAY+"Latency: "+b.RLatency)
                    list += ""
                    list += (EnumChatFormatting.GRAY+b.Rinfo)
                    list += (EnumChatFormatting.YELLOW+b.Rformula)
                }
                else list += "not upgradable"
            }
            else list += "not upgradable"
        }
        addChild(rinfo)
    }

    def sendMessage(message:String)
    {
        new PacketCustom(TransportationSPH.channel, TransportationSPH.gui_RouterUtil_action).writeString(message).sendToServer()
    }

    override def drawBack_Impl(mouse:Point, frame:Float)
    {
        PRResources.guiChipUpgrade.bind()
        drawTexturedModalRect(0, 0, 0, 0, xSize, ySize)
        if (container.chachedChip != null)
        {
            val b = container.chachedChip.upgradeBus
            fontRenderer.drawString(String.valueOf(if (b.LXLatency > 0) b.LXLatency else "-"), 29, 23, Colors_old.GREY.rgb)
            fontRenderer.drawString(String.valueOf(if (b.LYLatency > 0) b.LYLatency else "-"), 29, 43, Colors_old.GREY.rgb)
            fontRenderer.drawString(String.valueOf(if (b.LZLatency > 0) b.LZLatency else "-"), 29, 63, Colors_old.GREY.rgb)
            FontUtils.drawRightString(String.valueOf(if (b.RXLatency > 0) b.RXLatency else "-"), 148, 23, Colors_old.GREY.rgb)
            FontUtils.drawRightString(String.valueOf(if (b.RYLatency > 0) b.RYLatency else "-"), 148, 43, Colors_old.GREY.rgb)
            FontUtils.drawRightString(String.valueOf(if (b.RZLatency > 0) b.RZLatency else "-"), 148, 63, Colors_old.GREY.rgb)
        }
        var s = 0
        for ((x, y) <- GuiLib.createSlotGrid(8, 18, 1, 3, 2, 2))
        {
            val color = getColorForSlot(s)
            s+=1
            drawGradientRect(x-2, y+4, x, y+12, color, color)
        }
        for ((x, y) <- GuiLib.createSlotGrid(152, 18, 1, 3, 2, 2))
        {
            val color = getColorForSlot(s)
            s+=1
            drawGradientRect(x+16, y+4, x+18, y+12, color, color)
        }
    }

    private def getColorForSlot(slot:Int):Int =
    {
        if (container.chachedChip != null)
        {
            val b = container.chachedChip.upgradeBus
            var hasChip = false
            var canInstall = false
            var canHandle = false
            if (slot < 3)
            {
                hasChip = b.Lset(slot)
                canInstall = b.installL(slot, false)
                canHandle = b.maxL > slot
            }
            else if (slot < 6)
            {
                hasChip = b.Rset(slot - 3)
                canInstall = b.installR(slot - 3, false)
                canHandle = b.maxR > slot - 3
            }
            var color = 0
            if (hasChip) color = 0xff6BF100
            else if (canInstall) color = 0xffBFBF00
            else if (canHandle) color = 0xffA20F06
            else color = 0xff535353
            return color
        }
        0
    }
}

object GuiChipUpgrade extends TGuiBuilder
{
    override def getID = TransportationProxy.guiIDChipUpgrade

    @SideOnly(Side.CLIENT)
    override def buildGui(player:EntityPlayer, data:MCDataInput) =
        new GuiChipUpgrade(new ChipUpgradeContainer(player))
}
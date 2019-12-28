package club.bastonbolado.bookdupe;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mod(modid = BookDupe.MODID, version = BookDupe.VERSION)
public class BookDupe {
    public static final String MODID = "bookdupe";
    public static final String VERSION = "1.0";


    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static IntStream fill() {
        return IntStream.generate(() -> 0x10ffff);
    }

    private static IntStream random(Random rand) {
        return rand.ints(0x80, 0x10ffff - 0x800).map(i -> i < 0xd800 ? i : i + 0x800);
    }

    private static IntStream ascii(Random rand) {
        return rand.ints(0x20, 0x7f);
    }

    @SubscribeEvent
    public void clicar(PlayerInteractEvent event) {

        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;

        ItemStack itemStack = entityPlayer.getHeldItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getItem().equals(Items.writable_book)) {
            System.out.println("Tentando Criar livro para dupar...");
            String joinedPages = random(new Random()).limit(100 * 210).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

            NBTTagList pages = new NBTTagList();

            for (int page = 0; page < 50; page++) {
                pages.appendTag(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
            }

            itemStack.setTagInfo("pages", pages);
            itemStack.setTagInfo("author", new NBTTagString(entityPlayer.getName()));
            itemStack.setTagInfo("title", new NBTTagString("Livro Sagrado..."));
            itemStack.setItem(Items.written_book);

            PacketBuffer buff = new PacketBuffer(Unpooled.buffer());
            buff.writeItemStackToBuffer(itemStack);
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("MC|BSign", buff));
        }


    }


}

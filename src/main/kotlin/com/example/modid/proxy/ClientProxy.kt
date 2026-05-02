package com.example.modid.proxy

import com.example.modid.ExampleMod
import com.example.modid.Tags
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ClientProxy : IProxy {
    override fun preInit(event: FMLPreInitializationEvent) {
        ExampleMod.LOGGER.info("Hello From {}!", Tags.MOD_NAME)
        ExampleMod.LOGGER.info("Proxy is {}", ExampleMod.proxy)
        ExampleMod.LOGGER.info("Language: {}", Minecraft.getMinecraft().languageManager.currentLanguage)
    }
}

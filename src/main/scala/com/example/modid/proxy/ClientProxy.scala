package com.example.modid.proxy

import com.example.modid.{ExampleMod, Tags}
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ClientProxy extends IProxy {

  override def preInit(event: FMLPreInitializationEvent): Unit = {
    ExampleMod.LOGGER.info("Hello From {}!", Tags.MOD_NAME)
    ExampleMod.LOGGER.info("Proxy is {}", ExampleMod.proxy)
    ExampleMod.LOGGER.info("Language: {}", Minecraft.getMinecraft.getLanguageManager.getCurrentLanguage)
  }
}

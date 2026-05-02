package com.example.modid

import scala.compiletime.uninitialized

import com.example.modid.proxy.IProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

import net.minecraftforge.fml.common.{Mod, SidedProxy}
import org.apache.logging.log4j.{LogManager, Logger}

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.MOD_VERSION, modLanguage = "scala")
object ExampleMod {

  val LOGGER: Logger = LogManager.getLogger(Tags.MOD_NAME)

  @SidedProxy(modId = Tags.MOD_ID, clientSide = Tags.CLIENT_PROXY, serverSide = Tags.SERVER_PROXY)
  var proxy: IProxy = uninitialized


  /**
   * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
   * Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
   * </a>
   */
  @Mod.EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    proxy.preInit(event)
  }
}

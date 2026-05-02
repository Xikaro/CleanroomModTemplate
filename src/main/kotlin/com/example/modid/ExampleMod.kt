package com.example.modid

import com.example.modid.proxy.IProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.MOD_VERSION, modLanguage = "kotlin")
class ExampleMod {
    companion object {
        val LOGGER: Logger = LogManager.getLogger(Tags.MOD_NAME)

        @SidedProxy(modId = Tags.MOD_ID, clientSide = Tags.CLIENT_PROXY, serverSide = Tags.SERVER_PROXY)
        lateinit var proxy: IProxy
    }

    /**
     * [Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here]
     * (https://cleanroommc.com/wiki/forge-mod-development/event#overview)
     */
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.preInit(event)
    }
}

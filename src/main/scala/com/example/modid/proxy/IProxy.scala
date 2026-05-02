package com.example.modid.proxy

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

trait IProxy {

  def preInit(event: FMLPreInitializationEvent): Unit = {
  }

  def init(event: FMLInitializationEvent): Unit = {
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
  }
}

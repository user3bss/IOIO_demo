package com.bmt.custom_classes;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;

public class IOIO_info {
	public IOIO_info(){ 
	}
	public String getinfo(IOIO ioio){
		return String.format(
				"IOIOLib: %s\n" +
				"Application firmware: %s\n" +
				"Bootloader firmware: %s\n" +
				"Hardware: %s",
				ioio.getImplVersion(VersionType.IOIOLIB_VER),
				ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
				ioio.getImplVersion(VersionType.BOOTLOADER_VER),
				ioio.getImplVersion(VersionType.HARDWARE_VER));
	}
}

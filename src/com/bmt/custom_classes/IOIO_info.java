package com.bmt.custom_classes;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;

public class IOIO_info {
	IOIO ioio_ = null;
	public IOIO_info(IOIO ioio){
		ioio_ = ioio; 
	}
	public String getinfo(){
		return String.format(
				"IOIOLib: %s\n" +
				"Application firmware: %s\n" +
				"Bootloader firmware: %s\n" +
				"Hardware: %s",
				ioio_.getImplVersion(VersionType.IOIOLIB_VER),
				ioio_.getImplVersion(VersionType.APP_FIRMWARE_VER),
				ioio_.getImplVersion(VersionType.BOOTLOADER_VER),
				ioio_.getImplVersion(VersionType.HARDWARE_VER));
	}
}

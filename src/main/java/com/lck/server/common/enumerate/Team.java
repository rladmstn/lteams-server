package com.lck.server.common.enumerate;

import lombok.Getter;

@Getter
public enum Team {
	GEN("Gen.G"),
	T1("T1"),
	HLE("Hanwha Life Esports"),
	DK("Dplus KIA"),
	KT("Kt Rolster"),
	KDF("Kwangdong Freecs"),
	FOX("FearX"),
	NS("Nongshim RedForce"),
	DRX("DRX"),
	BRO("OKSavingBank BRION");

	private final String name;

	Team(String name) {
		this.name = name;
	}
}

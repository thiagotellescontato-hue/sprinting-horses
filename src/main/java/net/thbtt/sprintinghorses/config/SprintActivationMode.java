package net.thbtt.sprintinghorses.config;

public enum SprintActivationMode {
	HOLD,
	TOGGLE;

	public SprintActivationMode next() {
		return this == HOLD ? TOGGLE : HOLD;
	}
}

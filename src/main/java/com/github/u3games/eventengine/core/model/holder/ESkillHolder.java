package com.github.u3games.eventengine.core.model.holder;

public class ESkillHolder {

    private final int mSkillId;
    private final int mSkillLevel;

    public ESkillHolder(int skillId, int skillLevel) {
        mSkillId = skillId;
        mSkillLevel = skillLevel;
    }

    public int getSkillId() {
        return mSkillId;
    }

    public int getSkillLevel() {
        return mSkillLevel;
    }
}

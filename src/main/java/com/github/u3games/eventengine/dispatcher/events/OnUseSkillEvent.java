package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.ESkill;
import com.github.u3games.eventengine.core.model.entity.EEntity;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnUseSkillEvent extends ListenerEvent {

    private final EEntity mCaster;
    private final ESkill mSkill;
    private final EEntity mTarget;

    public OnUseSkillEvent(EEntity caster, ESkill skill, EEntity target) {
        mCaster = caster;
        mSkill = skill;
        mTarget = target;
    }

    public EEntity getCaster() {
        return mCaster;
    }

    public ESkill getSkill() {
        return mSkill;
    }

    public EEntity getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_USE_SKILL;
    }
}

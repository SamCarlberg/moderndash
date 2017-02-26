package edu.wpi.moderndash.action;

/**
 * No-op implementation of {@link Action}.
 */
class NopAction implements Action {

    @Override
    public void act() {
        // nop
    }

    @Override
    public void undo() {
        // nop
    }

}

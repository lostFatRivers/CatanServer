package com.jokerbee.state;

/**
 * 状态转换抽象;
 */
public final class TransitionModel<S extends Enum<S>, T extends Enum<T>> {
    /** 变换名称*/
    private final T transition;

    /** 起始状态 */
    private final S fromState;
    /** 终结状态 */
    private final S toState;

    public TransitionModel(T transition, S fromState, S toState) {
        this.transition = transition;
        this.fromState = fromState;
        this.toState = toState;
    }

    public T getTransition() {
        return transition;
    }

    public S getFromState() {
        return fromState;
    }

    public S getToState() {
        return toState;
    }

    @Override
    public String toString() {
        return "transition: " + transition.name() +
                ", \tfrom: " + fromState.name() + ", \tto: " + toState.name();
    }
}

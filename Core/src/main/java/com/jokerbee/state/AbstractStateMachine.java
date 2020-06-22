package com.jokerbee.state;

import com.jokerbee.state.anno.State;
import com.jokerbee.state.anno.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 状态机抽象类;
 *
 * @param <S> 状态枚举;
 * @param <T> 转换枚举;
 */
public abstract class AbstractStateMachine<S extends Enum<S>, T extends Enum<T>> {
    protected static Logger logger = LoggerFactory.getLogger("TEST");

    /** 当前状态 */
    protected volatile S currentState;

    /** 状态转换对应 model */
    protected Map<T, TransitionModel<S, T>> transitions;
    /** 起始状态对应 model */
    private Map<S, List<TransitionModel<S, T>>> fromStateTransitions;

    /** 状态切换回调方法 */
    private Map<String, Method> onStateMethods;
    /** 转换执行回调方法 */
    private Map<String, Method> onTransitionMethods;

    public AbstractStateMachine(S originalState) {
        this.currentState = originalState;
        this.transitions = new HashMap<>();
        this.fromStateTransitions = new HashMap<>();
        this.onStateMethods = new HashMap<>();
        this.onTransitionMethods = new HashMap<>();
        this.assembleHooks();
    }

    private void assembleHooks() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method eachM : methods) {
            State stateA = eachM.getAnnotation(State.class);
            if (stateA != null) {
                onStateMethods.put(stateA.value(), eachM);
            }
            Transition transitionA = eachM.getAnnotation(Transition.class);
            if (transitionA != null) {
                onTransitionMethods.put(transitionA.value(), eachM);
            }
        }
    }

    protected void registerTransition(T transition, S fromState, S toState) {
        TransitionModel<S, T> tModel = new TransitionModel<>(transition, fromState, toState);
        transitions.put(transition, tModel);
        this.addFromStateTransition(fromState, tModel);
    }

    private void addFromStateTransition(S fromState, TransitionModel<S, T> tModel) {
        if (!fromStateTransitions.containsKey(fromState)) {
            fromStateTransitions.put(fromState, new LinkedList<>());
        }
        fromStateTransitions.get(fromState).add(tModel);
    }

    private TransitionModel<S, T> getStateTransition(S fromState, S toState) {
        if (!fromStateTransitions.containsKey(fromState)) {
            logger.warn("no state transition:{}", fromState);
            return null;
        }
        List<TransitionModel<S, T>> models = fromStateTransitions.get(fromState);
        for (TransitionModel<S, T> eachModel : models) {
            if (eachModel.getToState().equals(toState)) {
                return eachModel;
            }
        }
        return null;
    }

    public S getCurrentState() {
        return this.currentState;
    }

    public boolean isState(S stateName) {
        return currentState.equals(stateName);
    }

    public boolean canToState(S stateName) {
        if (!fromStateTransitions.containsKey(currentState)) {
            logger.warn("current state cannot transform to any state:{}", currentState);
            return false;
        }
        List<TransitionModel<S, T>> models = fromStateTransitions.get(currentState);
        for (TransitionModel<S, T> eachModel : models) {
            if (eachModel.getToState().equals(stateName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 切换到目标状态;
     *
     * @param state 目标状态枚举;
     * @param params 切换回调所需参数;
     */
    public void toState(S state, Object... params) {
        TransitionModel<S, T> tModel = this.getStateTransition(currentState, state);
        if (!this.canToState(state) || tModel == null) {
            throw new IllegalStateException("[" + currentState.name() + "] cannot transform to new state: [" + state.name() + "]");
        }
        currentState = state;

        Method method = onStateMethods.get(state.name());
        if (method == null) return;
        try {
            method.setAccessible(true);
            method.invoke(this, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method tMethod = onTransitionMethods.get(tModel.getTransition().name());
        if (tMethod == null) return;
        try {
            tMethod.setAccessible(true);
            tMethod.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nStateMachine: {\n\tcurrent state: ").append(currentState.name()).append("\n\t{");
        for (TransitionModel<S, T> eachModel : transitions.values()) {
            sb.append("\n\t\t").append(eachModel.toString());
        }
        sb.append("\n\t}\n}\n");
        return sb.toString();
    }
}
